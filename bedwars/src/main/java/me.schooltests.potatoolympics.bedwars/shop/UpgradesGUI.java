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
    enum EnumUpgrade { PROTECTION, SHARPNESS, HASTE }
    enum UpgradesPage { NORMAL, TRAPS }
    private final Map<Pair<EnumUpgrade, Integer>, Integer> upgradeCosts = new HashMap<Pair<EnumUpgrade, Integer>, Integer>() {{
        put(Pair.of(EnumUpgrade.SHARPNESS, 1), 4);
        put(Pair.of(EnumUpgrade.PROTECTION, 1), 2);
        put(Pair.of(EnumUpgrade.PROTECTION, 2), 4);
        put(Pair.of(EnumUpgrade.PROTECTION, 3), 6);
        put(Pair.of(EnumUpgrade.PROTECTION, 4), 8);
        put(Pair.of(EnumUpgrade.HASTE, 1), 2);
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
        if (page == UpgradesPage.NORMAL) {
            ItemBuilder sharpness = new ItemBuilder(Material.IRON_SWORD);
            sharpness.enchant(Enchantment.DAMAGE_ALL, 1);
            sharpness.name("&eSharpened Swords");

            clickEvents.put(11, (event) -> {
                BedwarsTeam team = POBedwars.getInstance().getBedwarsGame().getTeam(player);
                if (InventoryUtil.contains(player.getInventory(), new ItemStack(Material.DIAMOND, upgradeCosts.get(Pair.of(EnumUpgrade.HASTE, 1))))) {
                    InventoryUtil.remove(player.getInventory(), new ItemStack(Material.DIAMOND, upgradeCosts.get(Pair.of(EnumUpgrade.HASTE, 1))));
                    if (team != null) {
                        team.setSharpness(true);
                        team.getGameTeam().getTeamMembers().forEach(teamPlayer -> teamPlayer.getPlayer().ifPresent(lp ->
                                lp.sendMessage(ChatColor.GREEN + "Your team now has " + ChatColor.YELLOW + "Sharpened Swords" + ChatColor.GREEN + "!")));
                    }
                }
            });

            inventory.setItem(11, sharpness.get());



            ItemBuilder haste = new ItemBuilder(Material.GOLD_PICKAXE);
            haste.name("&eHaste");

            clickEvents.put(13, (event) -> {
                BedwarsTeam team = POBedwars.getInstance().getBedwarsGame().getTeam(player);
                if (InventoryUtil.contains(player.getInventory(), new ItemStack(Material.DIAMOND, upgradeCosts.get(Pair.of(EnumUpgrade.HASTE, 1))))) {
                    InventoryUtil.remove(player.getInventory(), new ItemStack(Material.DIAMOND, upgradeCosts.get(Pair.of(EnumUpgrade.HASTE, 1))));
                    if (team != null) {
                        team.setHaste(true);
                        team.getGameTeam().getTeamMembers().forEach(teamPlayer -> teamPlayer.getPlayer().ifPresent(lp ->
                                lp.sendMessage(ChatColor.GREEN + "Your team now has " + ChatColor.YELLOW + "Haste" + ChatColor.GREEN + "!")));
                    }
                }
            });

            inventory.setItem(13, haste.get());
        } else {

        }
    }
}