package me.schooltests.potatoolympics.bedwars.shop;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.game.BedwarsTeam;
import me.schooltests.potatoolympics.core.data.GameTeam;
import me.schooltests.potatoolympics.core.util.InventoryUtil;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UpgradesGUI {
    enum UpgradesPage { NORMAL, TRAPS }

    private final int sharpnessCost = 4;
    private final int hasteCost = 3;
    private final Map<Integer, Integer> protectionCosts = new HashMap<Integer, Integer>() {{
        put(1, 2);
        put(2, 4);
        put(3, 6);
        put(4, 8);
    }};

    private Player player;
    private UpgradesPage page;
    private Listener guiListener;
    private Inventory inventory;
    private Map<Integer, Consumer<InventoryClickEvent>> clickEvents = new HashMap<>();

    public UpgradesGUI(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 54, ChatColor.YELLOW + "Upgrades");

        guiListener = new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getWhoClicked().getUniqueId().equals(player.getUniqueId())
                        && e.getView().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "Upgrades")) {
                    if (clickEvents.containsKey(e.getSlot())) clickEvents.get(e.getSlot()).accept(e);
                    open(page);
                    e.setCancelled(true);
                }
            }

            @EventHandler
            public void close(InventoryCloseEvent e) {
                if (e.getPlayer().getUniqueId().equals(player.getUniqueId())
                        && e.getView().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "Upgrades")) {
                    HandlerList.unregisterAll(guiListener);
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(guiListener, POBedwars.getInstance());
    }

    public void open(UpgradesPage page) {
        BedwarsTeam team = POBedwars.getInstance().getBedwarsGame().getTeam(player);
        if (team == null) return;
        if (page == UpgradesPage.NORMAL) {
            ItemBuilder sharpness = new ItemBuilder(Material.IRON_SWORD);
            sharpness.enchant(Enchantment.DAMAGE_ALL, 1);
            sharpness.name("&eSharpened Swords");

            clickEvents.put(11, (event) -> {
                if (InventoryUtil.contains(player.getInventory(), new ItemStack(Material.DIAMOND, sharpnessCost))) {
                    InventoryUtil.remove(player.getInventory(), new ItemStack(Material.DIAMOND, sharpnessCost));
                    team.setSharpness(true);
                    team.getGameTeam().getTeamMembers().forEach(teamPlayer -> teamPlayer.getPlayer().ifPresent(lp ->
                            lp.sendMessage(ChatColor.GREEN + "Your team now has " + ChatColor.YELLOW + "Sharpened Swords" + ChatColor.GREEN + "!")));
                }
            });

            inventory.setItem(11, sharpness.get());

            int protLevel = team.getProtection() + 1;
            int protCost = protectionCosts.get(protLevel);
            ItemBuilder prot = new ItemBuilder(Material.IRON_CHESTPLATE);
            prot.name("&eProtection " + protLevel);
            prot.lore("&7Cost: &b" + protCost);

            clickEvents.put(12, (event) -> {
                if (InventoryUtil.contains(player.getInventory(), new ItemStack(Material.DIAMOND, protCost))) {
                    InventoryUtil.remove(player.getInventory(), new ItemStack(Material.DIAMOND, protCost));
                    team.setProtection(protLevel);
                    team.getGameTeam().getTeamMembers().forEach(teamPlayer -> teamPlayer.getPlayer().ifPresent(lp ->
                            lp.sendMessage(ChatColor.GREEN + "Your team now has " + ChatColor.YELLOW + "Protection " + protLevel + ChatColor.GREEN + "!")));
                }
            });

            inventory.setItem(12, prot.get());

            ItemBuilder haste = new ItemBuilder(Material.GOLD_PICKAXE);
            haste.name("&eHaste");

            clickEvents.put(13, (event) -> {
                if (InventoryUtil.contains(player.getInventory(), new ItemStack(Material.DIAMOND, hasteCost))) {
                    InventoryUtil.remove(player.getInventory(), new ItemStack(Material.DIAMOND, hasteCost));
                    team.setHaste(true);
                    team.getGameTeam().getTeamMembers().forEach(teamPlayer -> teamPlayer.getPlayer().ifPresent(lp ->
                            lp.sendMessage(ChatColor.GREEN + "Your team now has " + ChatColor.YELLOW + "Haste" + ChatColor.GREEN + "!")));
                }
            });

            inventory.setItem(13, haste.get());
        } else if (page == UpgradesPage.TRAPS) {

        }
    }
}