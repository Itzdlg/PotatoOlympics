package me.schooltests.potatoolympics.bedwars.shop;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.game.ArmorLevel;
import me.schooltests.potatoolympics.bedwars.game.BedwarsTeam;
import me.schooltests.potatoolympics.core.data.GameTeam;
import me.schooltests.potatoolympics.core.util.InventoryUtil;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import me.schooltests.potatoolympics.core.util.TeamUtil;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ShopGUI implements Listener {
    enum ShopPage { QUICK_BUY, BLOCKS, WEAPONS, ARMOR, TOOLS, BOWS, POTIONS, MISC }
    class EnchantInfo {
        public Enchantment ench;
        public int level;
        EnchantInfo(Enchantment ench, int level) {
            this.ench = ench;
            this.level = level;
        }
    }

    private Player player;
    private ShopPage page;
    private Listener guiListener;
    private Inventory inventory;
    private Map<Integer, Consumer<InventoryClickEvent>> clickEvents = new HashMap<>();

    public ShopGUI(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 54, ChatColor.YELLOW + "Shop");

        guiListener = new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                if (e.getWhoClicked().getUniqueId().equals(player.getUniqueId())
                        && e.getView().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "Shop")) {
                    if (clickEvents.containsKey(e.getSlot())) clickEvents.get(e.getSlot()).accept(e);
                    open(page);
                    e.setCancelled(true);
                }
            }

            @EventHandler
            public void close(InventoryCloseEvent e) {
                if (e.getPlayer().getUniqueId().equals(player.getUniqueId())
                        && e.getView().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "Shop")) {
                    HandlerList.unregisterAll(guiListener);
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(guiListener, POBedwars.getInstance());

        setHeader();
    }

    public void open(ShopPage page) {
        BedwarsTeam bedwarsTeam = POBedwars.getInstance().getBedwarsGame().getTeam(player);
        GameTeam team = bedwarsTeam.getGameTeam();
        this.page = page;
        clearShopSlots();
        switch (page) {
            case QUICK_BUY:
                setHeaderCursor(ShopPage.QUICK_BUY);
                add(Material.WOOL, "Wool", 16, TeamUtil.getWoolColor(team), 4, Currency.IRON);
                add(Material.WOOD, "Wood", 16, 4, Currency.GOLD);
                add(Material.HARD_CLAY, "Clay", 16, TeamUtil.getWoolColor(team), 12, Currency.IRON);

                add(Material.STICK, "Knockback Stick", 1, 5, Currency.GOLD, new EnchantInfo(Enchantment.KNOCKBACK, 1));

                if (bedwarsTeam.hasSharpness())
                    add(Material.STONE_SWORD, "Stone Sword", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DAMAGE_ALL, 1));
                else
                    add(Material.STONE_SWORD, "Stone Sword", 1, 10, Currency.IRON);

                if (bedwarsTeam.hasSharpness())
                    add(Material.IRON_SWORD, "Iron Sword", 1, 7, Currency.GOLD, new EnchantInfo(Enchantment.DAMAGE_ALL, 1));
                else
                    add(Material.IRON_SWORD, "Iron Sword", 1, 7, Currency.GOLD);

                add(Material.LADDER, "Ladder", 16, 4, Currency.IRON);
                add(ArmorLevel.CHAINMAIL, 40, Currency.IRON);
                add(ArmorLevel.IRON, 12, Currency.GOLD);

                add(Material.GOLDEN_APPLE, "Golden Apple", 1, 3, Currency.GOLD);
                add(Material.FIREBALL, "Fireball", 1, 40, Currency.IRON);
                add(Material.TNT, "TNT", 1, 4, Currency.GOLD);

                add(Material.POTION, "Speed Potion", 1, 1, 1, Currency.EMERALD, Color.YELLOW, new PotionEffect(PotionEffectType.SPEED, 45 * 20, 4, true, false));
                add(Material.POTION, "Jump Potion", 1, 8, 1, Currency.EMERALD, Color.GREEN, new PotionEffect(PotionEffectType.JUMP, 45 * 20, 4, true, false));
                add(Material.POTION, "Invisibility Potion", 1, 14, 2, Currency.EMERALD, Color.GRAY, new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 1, true, false));

                add(Material.ARROW, "Arrow", 8, 2, Currency.GOLD);

                if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.WOOD_PICKAXE).amount(1).name("&eWood Pickaxe").get()))
                    add(Material.IRON_PICKAXE, "Iron Pickaxe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 2));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.IRON_PICKAXE).amount(1).name("&eIron Pickaxe").get()))
                    add(Material.GOLD_PICKAXE, "Gold Pickaxe", 1, 3, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 3), new EnchantInfo(Enchantment.DAMAGE_ALL, 2));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.GOLD_PICKAXE).amount(1).name("&eGold Pickaxe").get()))
                    add(Material.DIAMOND_PICKAXE, "Diamond Pickaxe", 1, 6, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 3));
                else
                    add(Material.WOOD_PICKAXE, "Wood Pickaxe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 1));

                if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.WOOD_AXE).amount(1).name("&eWood Axe").get()))
                    add(Material.STONE_AXE, "Stone Axe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 1));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.STONE_AXE).amount(1).name("&eStone Axe").get()))
                    add(Material.IRON_AXE, "Iron Axe", 1, 3, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 2));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.IRON_AXE).amount(1).name("&eIron Axe").get()))
                    add(Material.DIAMOND_AXE, "Diamond Axe", 1, 6, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 3));
                else
                    add(Material.WOOD_AXE, "Wood Axe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 1));
                break;
            case BLOCKS:
                setHeaderCursor(ShopPage.BLOCKS);
                add(Material.WOOL, "Wool", 16, TeamUtil.getWoolColor(team), 4, Currency.IRON);
                add(Material.WOOD, "Wood", 16, 4, Currency.GOLD);
                add(Material.HARD_CLAY, "Clay", 16, TeamUtil.getWoolColor(team), 12, Currency.IRON);
                add(Material.GLASS, "Blast Proof Glass", 4, 12, Currency.IRON);
                add(Material.ENDER_STONE, "End Stone", 12, 24, Currency.IRON);
                add(Material.LADDER, "Ladder", 16, 4, Currency.IRON);
                add(Material.OBSIDIAN, "Obsidian", 4, 4, Currency.EMERALD);
                break;
            case WEAPONS:
                setHeaderCursor(ShopPage.WEAPONS);
                add(Material.STICK, "Knockback Stick", 1, 5, Currency.GOLD, new EnchantInfo(Enchantment.KNOCKBACK, 1));
                add(Material.STONE_SWORD, "Stone Sword", 1, 10, Currency.IRON);

                if (bedwarsTeam.hasSharpness())
                    add(Material.STONE_SWORD, "Stone Sword", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DAMAGE_ALL, 1));
                else
                    add(Material.STONE_SWORD, "Stone Sword", 1, 10, Currency.IRON);

                if (bedwarsTeam.hasSharpness())
                    add(Material.IRON_SWORD, "Iron Sword", 1, 7, Currency.GOLD, new EnchantInfo(Enchantment.DAMAGE_ALL, 1));
                else
                    add(Material.IRON_SWORD, "Iron Sword", 1, 7, Currency.GOLD);

                if (bedwarsTeam.hasSharpness())
                    add(Material.DIAMOND_SWORD, "Diamond Sword", 1, 4, Currency.EMERALD, new EnchantInfo(Enchantment.DAMAGE_ALL, 1));
                else
                    add(Material.DIAMOND_SWORD, "Diamond Sword", 1, 4, Currency.EMERALD);

                break;
            case ARMOR:
                setHeaderCursor(ShopPage.ARMOR);
                add(ArmorLevel.CHAINMAIL, 40, Currency.IRON);
                add(ArmorLevel.IRON, 12, Currency.GOLD);
                add(ArmorLevel.DIAMOND, 6, Currency.EMERALD);
                break;
            case TOOLS:
                setHeaderCursor(ShopPage.TOOLS);
                add(Material.SHEARS, "Shears", 1, 20, Currency.IRON);
                if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.WOOD_PICKAXE).amount(1).name("&eWood Pickaxe").get()))
                    add(Material.IRON_PICKAXE, "Iron Pickaxe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 2));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.IRON_PICKAXE).amount(1).name("&eIron Pickaxe").get()))
                    add(Material.GOLD_PICKAXE, "Gold Pickaxe", 1, 3, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 3), new EnchantInfo(Enchantment.DAMAGE_ALL, 2));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.GOLD_PICKAXE).amount(1).name("&eGold Pickaxe").get()))
                    add(Material.DIAMOND_PICKAXE, "Diamond Pickaxe", 1, 6, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 3));
                else
                    add(Material.WOOD_PICKAXE, "Wood Pickaxe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 1));

                if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.WOOD_AXE).amount(1).name("&eWood Axe").get()))
                    add(Material.STONE_AXE, "Stone Axe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 1));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.STONE_AXE).amount(1).name("&eStone Axe").get()))
                    add(Material.IRON_AXE, "Iron Axe", 1, 3, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 2));
                else if (InventoryUtil.contains(player.getInventory(), new ItemBuilder(Material.IRON_AXE).amount(1).name("&eIron Axe").get()))
                    add(Material.DIAMOND_AXE, "Diamond Axe", 1, 6, Currency.GOLD, new EnchantInfo(Enchantment.DIG_SPEED, 3));
                else
                    add(Material.WOOD_AXE, "Wood Axe", 1, 10, Currency.IRON, new EnchantInfo(Enchantment.DIG_SPEED, 1));
                break;
            case BOWS:
                setHeaderCursor(ShopPage.BOWS);
                add(Material.BOW, "Normal Bow", 1, 12, Currency.GOLD);
                add(Material.BOW, "Power Bow", 1, 24, Currency.GOLD, new EnchantInfo(Enchantment.ARROW_DAMAGE, 1));
                add(Material.BOW, "Punch Bow", 1, 6, Currency.EMERALD, new EnchantInfo(Enchantment.ARROW_DAMAGE, 1), new EnchantInfo(Enchantment.ARROW_KNOCKBACK, 1));
                add(Material.ARROW, "Arrows", 8, 2, Currency.GOLD);
                break;
            case POTIONS:
                setHeaderCursor(ShopPage.POTIONS);
                add(Material.POTION, "Speed Potion", 1, 1, 1, Currency.EMERALD, Color.YELLOW, new PotionEffect(PotionEffectType.SPEED, 45 * 20, 4, true, false));
                add(Material.POTION, "Jump Potion", 1, 8, 1, Currency.EMERALD, Color.GREEN, new PotionEffect(PotionEffectType.JUMP, 45 * 20, 4, true, false));
                add(Material.POTION, "Invisibility Potion", 1, 14, 2, Currency.EMERALD, Color.GRAY, new PotionEffect(PotionEffectType.INVISIBILITY, 30 * 20, 1, true, false));
                break;
            case MISC:
                setHeaderCursor(ShopPage.MISC);
                add(Material.FIREBALL, "Fireball", 1, 40, Currency.IRON);
                add(Material.TNT, "TNT", 1, 4, Currency.GOLD);
                add(Material.ENDER_PEARL, "Ender Pearl", 1, 4, Currency.EMERALD);
                add(Material.WATER_BUCKET, "Water Bucket", 1, 3, Currency.GOLD);
                add(Material.EGG, "Bridge Egg", 1, 2, Currency.EMERALD);
                add(Material.MILK_BUCKET, "Magic Milk", 1, 4, Currency.GOLD);
                break;
        }

        if (!player.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "Shop")) player.openInventory(inventory);
    }

    private void add(Material material, String name, int amount, int durability, int price, Currency currency) {
        int slot = getOpenSlot(page == ShopPage.QUICK_BUY);
        inventory.setItem(slot, new Purchasable(player).of(material, name, amount, durability, price, currency, true).get());
        clickEvents.put(slot, (event) -> {
            if (InventoryUtil.contains(player.getInventory(), new ItemStack(currency.getMaterial(), price))) {
                ItemBuilder builder = new Purchasable(player).of(material, name, amount, durability, price, currency, false);
                InventoryUtil.remove(player.getInventory(), new ItemStack(currency.getMaterial(), price));
                player.getInventory().addItem(builder.get());
            }
        });
    }

    private void add(Material material, String name, int amount, int price, Currency currency) {
        add(material, name, amount, 0, price, currency);
    }

    private void add(Material material, String name, int amount, int price, Currency currency, EnchantInfo... enchants) {
        int slot = getOpenSlot(page == ShopPage.QUICK_BUY);
        ItemBuilder enchanted = new Purchasable(player).of(material, name, amount, 0, price, currency, true);
        for (EnchantInfo i : enchants)
            enchanted.enchant(i.ench, i.level);
        inventory.setItem(slot, enchanted.get());
        clickEvents.put(slot, (event) -> {
            if (InventoryUtil.contains(player.getInventory(), new ItemStack(currency.getMaterial(), price))) {
                boolean success = true;
                ItemBuilder builder = new Purchasable(player).of(material, name, amount, 0, price, currency, false);
                for (EnchantInfo i : enchants)
                    builder.enchant(i.ench, i.level);

                if (builder.get().getType() != Material.WOOD_AXE && builder.get().getType() != Material.WOOD_PICKAXE) player.getInventory().addItem(builder.get());
                if (builder.get().getType() == Material.IRON_PICKAXE) InventoryUtil.remove(player.getInventory(), new ItemStack(Material.WOOD_PICKAXE));
                if (builder.get().getType() == Material.GOLD_PICKAXE) InventoryUtil.remove(player.getInventory(), new ItemStack(Material.IRON_PICKAXE));
                if (builder.get().getType() == Material.DIAMOND_PICKAXE) InventoryUtil.remove(player.getInventory(), new ItemStack(Material.GOLD_PICKAXE));

                if (builder.get().getType() == Material.STONE_AXE) InventoryUtil.remove(player.getInventory(), new ItemStack(Material.WOOD_AXE));
                if (builder.get().getType() == Material.IRON_AXE) InventoryUtil.remove(player.getInventory(), new ItemStack(Material.STONE_AXE));
                if (builder.get().getType() == Material.DIAMOND_AXE) InventoryUtil.remove(player.getInventory(), new ItemStack(Material.IRON_AXE));

                if (builder.get().getType() == Material.WOOD_PICKAXE) {
                    if (!InventoryUtil.contains(player.getInventory(), new ItemStack(Material.DIAMOND_PICKAXE))) player.getInventory().addItem(builder.get());
                    else success = false;
                }

                if (builder.get().getType() == Material.WOOD_AXE) {
                    if (!InventoryUtil.contains(player.getInventory(), new ItemStack(Material.DIAMOND_AXE))) player.getInventory().addItem(builder.get());
                    else success = false;
                }

                if (success) InventoryUtil.remove(player.getInventory(), new ItemStack(currency.getMaterial(), price));
            }
        });
    }

    private void add(Material material, String name, int amount, int durability, int price, Currency currency, Color color, PotionEffect... potions) {
        int slot = getOpenSlot(page == ShopPage.QUICK_BUY);
        inventory.setItem(slot, new Purchasable(player).of(material, name, amount, durability, price, currency, true).color(color).get());
        clickEvents.put(slot, (event) -> {
            if (InventoryUtil.contains(player.getInventory(), new ItemStack(currency.getMaterial(), price))) {
                InventoryUtil.remove(player.getInventory(), new ItemStack(currency.getMaterial(), price));
                ItemBuilder builder = new Purchasable(player).of(material, name, amount, durability, price, currency, false);
                for (PotionEffect effect : potions)
                    builder.effect(effect);
                player.getInventory().addItem(builder.color(color).get());
            }
        });
    }

    private void add(ArmorLevel armorLevel, int price, Currency currency) {
        Material armorBoots;
        switch (armorLevel) {
            case CHAINMAIL:
                armorBoots = Material.CHAINMAIL_BOOTS;
                break;
            case IRON:
                armorBoots = Material.IRON_BOOTS;
                break;
            case DIAMOND:
                armorBoots = Material.DIAMOND_BOOTS;
                break;
            default:
                armorBoots = Material.LEATHER_BOOTS;
                break;
        }

        int slot = getOpenSlot(page == ShopPage.QUICK_BUY);
        inventory.setItem(slot, new Purchasable(player).of(armorBoots, armorLevel.name() + " Armor", 1, 0, price, currency, true).get());
        clickEvents.put(slot, (event) -> {
            if (InventoryUtil.contains(player.getInventory(), new ItemStack(currency.getMaterial(), price))) {
                BedwarsTeam team = POBedwars.getInstance().getBedwarsGame().getTeam(player);
                if (team.getArmorLevel().ordinal() < armorLevel.ordinal()) {
                    InventoryUtil.remove(player.getInventory(), new ItemStack(currency.getMaterial(), price));
                    team.setArmorLevel(armorLevel);
                    team.resetArmor();
                } else {
                    player.sendMessage(ChatColor.GREEN + "You already have a higher level of armor!");
                }
            }
        });
    }

    private int getOpenSlot(boolean vertical) {
        List<Integer> availableSlots;
        if (vertical) availableSlots = Arrays.asList(19,28,37,20,29,38,21,30,39,23,32,41,24,33,42,25,34,43);
        else availableSlots = Arrays.asList(19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43);
        for (Integer i : availableSlots) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) return i;
        }

        return 43;
    }

    private void clearShopSlots() {
        List<Integer> availableSlots = Arrays.asList(19,28,37,20,29,38,21,30,39,22,31,40,23,32,41,24,33,42,25,34,43);
        for (Integer i : availableSlots) {
            inventory.clear(i);
            clickEvents.remove(i);
        }
    }

    private void setHeader() {
        inventory.setItem(0, new ItemBuilder(Material.NETHER_STAR).name("&aQuick Buy").amount(1).get());
        clickEvents.put(0, (event) -> open(ShopPage.QUICK_BUY));

        inventory.setItem(1, new ItemBuilder(Material.HARD_CLAY).name("&aBlocks").amount(1).get());
        clickEvents.put(1, (event) -> open(ShopPage.BLOCKS));

        inventory.setItem(2, new ItemBuilder(Material.GOLD_SWORD).name("&aWeapons").amount(1).get());
        clickEvents.put(2, (event) -> open(ShopPage.WEAPONS));

        inventory.setItem(3, new ItemBuilder(Material.NETHER_STAR).name("&aArmor").amount(1).get());
        clickEvents.put(3, (event) -> open(ShopPage.ARMOR));

        inventory.setItem(4, new ItemBuilder(Material.IRON_PICKAXE).name("&aTools").amount(1).get());
        clickEvents.put(4, (event) -> open(ShopPage.TOOLS));

        inventory.setItem(5, new ItemBuilder(Material.BOW).name("&aBows").amount(1).get());
        clickEvents.put(5, (event) -> open(ShopPage.BOWS));

        inventory.setItem(6, new ItemBuilder(Material.POTION).durability(6).name("&aPotions").amount(1).get());
        clickEvents.put(6, (event) -> open(ShopPage.POTIONS));

        inventory.setItem(7, new ItemBuilder(Material.TNT).name("&aMisc").amount(1).get());
        clickEvents.put(7, (event) -> open(ShopPage.MISC));
    }

    private void setHeaderCursor(ShopPage page) {
        for (int i = 9; i < 18; i++) inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name("&c").get());
        inventory.setItem(page.ordinal() + 9, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(5).name("&aCurrent").get());
    }
}
