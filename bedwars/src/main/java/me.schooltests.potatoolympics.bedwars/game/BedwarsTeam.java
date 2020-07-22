package me.schooltests.potatoolympics.bedwars.game;

import me.schooltests.potatoolympics.core.data.GameTeam;
import me.schooltests.potatoolympics.core.data.TeamPlayer;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import me.schooltests.potatoolympics.core.util.TeamUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BedwarsTeam {
    private GameTeam gameTeam;
    private boolean hasBed = true;
    private boolean dead = false;
    private ArmorLevel armorLevel = ArmorLevel.LEATHER;
    private int protection = 0;
    private int sharpness = 0;
    private boolean haste = false;
    private boolean healPool = false;

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

    public int getSharpness() {
        return sharpness;
    }

    public void setSharpness(int sharpness) {
        this.sharpness = sharpness;
    }

    public boolean hasHaste() {
        return haste;
    }

    public void setHaste(boolean haste) {
        this.haste = haste;
    }

    public boolean hasHealPool() {
        return healPool;
    }

    public void setHealPool(boolean healPool) {
        this.healPool = healPool;
    }
}