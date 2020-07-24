package me.schooltests.potatoolympics.bedwars.game;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.events.InvisEvent;
import me.schooltests.potatoolympics.bedwars.traps.EnumTrap;
import me.schooltests.potatoolympics.core.data.GameTeam;
import me.schooltests.potatoolympics.core.data.TeamPlayer;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import me.schooltests.potatoolympics.core.util.PacketUtil;
import me.schooltests.potatoolympics.core.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class BedwarsTeam {
    private GameTeam gameTeam;
    private boolean hasBed = true;
    private boolean dead = false;
    private ArmorLevel armorLevel = ArmorLevel.LEATHER;
    private int protection = 0;
    private boolean sharpness = false;
    private boolean haste = false;
    private Deque<EnumTrap> trapDeque = new ArrayDeque<>();

    public BedwarsTeam(GameTeam team) {
        this.gameTeam = team;
    }


    public GameTeam getGameTeam() {
        return gameTeam;
    }

    public void setGameTeam(GameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    public boolean hasBed() {
        return hasBed;
    }

    public void setHasBed(boolean hasBed) {
        this.hasBed = hasBed;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public ArmorLevel getArmorLevel() {
        return armorLevel;
    }

    public void setArmorLevel(ArmorLevel armorLevel) {
        this.armorLevel = armorLevel;
    }

    public void resetArmor() {
        ItemStack leggings;
        ItemStack boots;
        switch (armorLevel) {
            case CHAINMAIL:
                leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
                boots = new ItemStack(Material.CHAINMAIL_BOOTS);
                break;
            case IRON:
                leggings = new ItemStack(Material.IRON_LEGGINGS);
                boots = new ItemStack(Material.IRON_BOOTS);
                break;
            case DIAMOND:
                leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                boots = new ItemStack(Material.DIAMOND_BOOTS);
                break;
            default:
                leggings = new ItemBuilder(Material.LEATHER_LEGGINGS).color(TeamUtil.getBukkitColor(getGameTeam())).get();
                boots = new ItemBuilder(Material.LEATHER_BOOTS).color(TeamUtil.getBukkitColor(getGameTeam())).get();
        }

        for (TeamPlayer teamMember : getGameTeam().getTeamMembers()) {
            teamMember.getPlayer().ifPresent(player -> {
                if (protection > 0) {
                    player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).unbreakable(true).color(TeamUtil.getBukkitColor(getGameTeam())).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection).get());
                    player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).unbreakable(true).color(TeamUtil.getBukkitColor(getGameTeam())).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection).get());
                    player.getInventory().setLeggings(new ItemBuilder(leggings).unbreakable(true).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection).get());
                    player.getInventory().setBoots(new ItemBuilder(boots).unbreakable(true).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, protection).get());
                } else {
                    player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).unbreakable(true).color(TeamUtil.getBukkitColor(getGameTeam())).get());
                    player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).unbreakable(true).color(TeamUtil.getBukkitColor(getGameTeam())).get());
                    player.getInventory().setLeggings(new ItemBuilder(leggings).unbreakable(true).get());
                    player.getInventory().setBoots(new ItemBuilder(boots).unbreakable(true).get());
                }
            });
        }
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public boolean hasSharpness() {
        return sharpness;
    }

    public void setSharpness(boolean sharpness) {
        this.sharpness = sharpness;
    }

    public boolean hasHaste() {
        return haste;
    }

    public void setHaste(boolean haste) {
        this.haste = haste;
    }

    public void trapTriggered(Player intruder) {
        if (!trapDeque.isEmpty() && isInRegion(intruder)) {
            EnumTrap trap = trapDeque.pollFirst();
            if (trap != null) {
                getGameTeam().getTeamMembers().forEach(teamMember -> teamMember.getPlayer().ifPresent(p ->
                        PacketUtil.sendTitle(p, new PacketUtil.FormattedText("Trap Triggered", ChatColor.RED), new PacketUtil.FormattedText(trap.getName()), 15, 20, 15)));
                switch (trap) {
                    case BLIND_AND_SLOW:
                        intruder.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 4, false, false));
                        intruder.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2, false, false));
                        break;
                    case ALARM:
                        if (intruder.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            intruder.removePotionEffect(PotionEffectType.INVISIBILITY);
                            InvisEvent.sendActualArmor(intruder.getUniqueId());
                        }

                        break;
                    case MINING_FATIGUE:
                        intruder.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 0, false, false));
                        break;
                }
            }

            POBedwars.getInstance().getBedwarsGame().getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> trapTriggered(intruder), 100));
        }
    }

    private boolean isInRegion(Player player) {
        Set<ProtectedRegion> regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(player.getLocation()).getRegions();
        return regions.stream().anyMatch(r -> r.getId().endsWith(TeamUtil.getDisplayColor(getGameTeam())));
    }
}