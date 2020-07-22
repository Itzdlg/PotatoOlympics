package me.schooltests.potatoolympics.bedwars.shop;

import me.schooltests.potatoolympics.core.util.InventoryUtil;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public final class Purchasable {
    public static final ItemStack WOOL = new ItemBuilder(Material.WOOL).amount(16).name("&aWool").lore("&7Cost: &f4 Iron").lore("").lore("&7Great for bridging across").lore("&7islands. Turns in your team's").lore("&7color.").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack CLAY = new ItemBuilder(Material.HARD_CLAY).amount(16).name("&aHardened Clay").lore("&7Cost: &f12 Iron").lore("").lore("&7Basic block to defend your bed.").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack GLASS = new ItemBuilder(Material.GLASS).amount(4).name("&aBlast-Proof Glass").lore("&7Cost: &f12 Iron").lore("").lore("&7Immune to explosions.").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack ENDSTONE = new ItemBuilder(Material.ENDER_STONE).amount(12).name("&aEnd Stone").lore("&7Cost: &f24 Iron").lore("").lore("&7Solid block to defend your bed.").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack LADDER = new ItemBuilder(Material.LADDER).amount(16).name("&aLadder").lore("&7Cost: &f4 Iron").lore("").lore("&7Save cats in trees.").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack WOOD = new ItemBuilder(Material.WOOD).amount(16).name("&aWood Planks").lore("&7Cost: &64 Gold").lore("").lore("&7Basic block to defend your bed.").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack OBSIDIAN = new ItemBuilder(Material.OBSIDIAN).amount(4).name("&aObsidian").lore("&7Cost: &24 Emerald").lore("").lore("&7Extreme protection for your bed.").lore("").lore("&eClick to purchase!").get();

    public static final ItemStack STONE_SWORD = new ItemBuilder(Material.STONE_SWORD).amount(1).name("&aStone Sword").lore("&7Cost: &f10 Iron").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack IRON_SWORD = new ItemBuilder(Material.IRON_SWORD).amount(1).name("&aIron Sword").lore("&7Cost: &67 Gold").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack DIAMOND_SWORD = new ItemBuilder(Material.DIAMOND_SWORD).amount(1).name("&aDiamond Sword").lore("&7Cost: &24 Emerald").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack KB_STICK = new ItemBuilder(Material.STICK).amount(1).name("&aKnockback Stick").lore("&7Cost: &65 Gold").lore("").lore("&eClick to purchase!").get();

    public static final ItemStack CHAIN_ARMOR = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).amount(1).name("&aChainmail Armor").lore("&7Cost: &f40 Iron").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack IRON_ARMOR = new ItemBuilder(Material.IRON_CHESTPLATE).amount(1).name("&aIron Armor").lore("&7Cost: &612 Gold").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack DIAMOND_ARMOR = new ItemBuilder(Material.DIAMOND_CHESTPLATE).amount(1).name("&aDiamond Armor").lore("&7Cost: &26 Emerald").lore("").lore("&eClick to purchase!").get();

    public static final ItemStack SHEARS = new ItemBuilder(Material.SHEARS).amount(1).name("&aShears").lore("&7Cost: &f20 Iron").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack WOOD_PICKAXE = new ItemBuilder(Material.WOOD_PICKAXE).amount(1).name("&aWood Pickaxe").lore("&7Cost: &f10 Iron").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED).get();
    public static final ItemStack IRON_PICKAXE = new ItemBuilder(Material.IRON_PICKAXE).amount(1).name("&aIron Pickaxe").lore("&7Cost: &f10 Iron").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED, 2).get();
    public static final ItemStack GOLD_PICKAXE = new ItemBuilder(Material.GOLD_PICKAXE).amount(1).name("&aGold Pickaxe").lore("&7Cost: &63 Gold").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.DAMAGE_ALL, 2).get();
    public static final ItemStack DIAMOND_PICKAXE = new ItemBuilder(Material.DIAMOND_PICKAXE).amount(1).name("&aDiamond Pickaxe").lore("&7Cost: &66 Gold").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED, 3).get();

    public static final ItemStack WOOD_AXE = new ItemBuilder(Material.WOOD_AXE).amount(1).name("&aWood Axe").lore("&7Cost: &f10 Iron").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED).get();
    public static final ItemStack STONE_AXE = new ItemBuilder(Material.STONE_AXE).amount(1).name("&aStone Axe").lore("&7Cost: &f10 Iron").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED).get();
    public static final ItemStack IRON_AXE = new ItemBuilder(Material.IRON_AXE).amount(1).name("&aIron Axe").lore("&7Cost: &63 Gold").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED, 2).get();
    public static final ItemStack DIAMOND_AXE = new ItemBuilder(Material.DIAMOND_AXE).amount(1).name("&aDiamond Axe").lore("&7Cost: &66 Gold").lore("").lore("&eClick to purchase!").enchant(Enchantment.DIG_SPEED, 3).get();

    public static final ItemStack BOW = new ItemBuilder(Material.BOW).amount(1).name("&aNormal Bow").lore("&7Cost: &612 Gold").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack POWER_BOW = new ItemBuilder(Material.BOW).amount(1).name("&aPower Bow").lore("&7Cost: &624 Gold").lore("").lore("&eClick to purchase!").enchant(Enchantment.ARROW_DAMAGE).get();
    public static final ItemStack PUNCH_BOW = new ItemBuilder(Material.BOW).amount(1).name("&aPower & Punch Bow").lore("&7Cost: &26 Emerald").lore("").lore("&eClick to purchase!").enchant(Enchantment.ARROW_DAMAGE).enchant(Enchantment.ARROW_KNOCKBACK).get();
    public static final ItemStack ARROW = new ItemBuilder(Material.ARROW).amount(8).name("&aArrow").lore("&7Cost: &62 Gold").lore("").lore("&eClick to purchase!").get();

    public static final ItemStack SPEED_POT = new ItemBuilder(Material.POTION).amount(1).name("&aSpeed Potion").lore("&7Cost: &21 Emerald").durability(1).lore("").lore("&eClick to purchase!").effect(PotionEffectType.SPEED, 45 * 20, 1, true).get();
    public static final ItemStack JUMP_POT = new ItemBuilder(Material.POTION).amount(1).name("&aJump Potion").lore("&7Cost: &21 Emerald").durability(8).lore("").lore("&eClick to purchase!").effect(PotionEffectType.JUMP, 45 * 20, 4, true).get();
    public static final ItemStack INVIS_POT = new ItemBuilder(Material.POTION).amount(1).name("&aInvis Potion").lore("&7Cost: &22 Emerald").durability(14).lore("").lore("&eClick to purchase!").effect(PotionEffectType.JUMP, 30 * 20, 4, true).get();

    public static final ItemStack GOLDEN_APPLE = new ItemBuilder(Material.GOLDEN_APPLE).amount(1).name("&aGolden Apple").lore("&7Cost: &63 Gold").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack BED_BUG = new ItemBuilder(Material.SNOW_BALL).amount(1).name("&aSilverfish Snowball").lore("&7Cost: &f40 Iron").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack IRON_GOLEM = new ItemBuilder(Material.MONSTER_EGG).durability(91).amount(1).name("&aIron Golem Spawn Egg").lore("&7Cost: &f120 Iron").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack FIREBALL = new ItemBuilder(Material.FIREBALL).amount(1).name("&aFireball").lore("&7Cost: &f40 Iron").lore("").lore("&eClick to purchase!").get();

    public static final ItemStack TNT = new ItemBuilder(Material.TNT).amount(1).name("&aTNT").lore("&7Cost: &64 Gold").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack ENDERPEARL = new ItemBuilder(Material.ENDER_PEARL).amount(1).name("&aEnder Pearl").lore("&7Cost: &24 Emerald").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack WATER_BUCKET = new ItemBuilder(Material.WATER_BUCKET).amount(1).name("&aWater Bucket").lore("&7Cost: &63 Gold").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack BRIDGE_EGG = new ItemBuilder(Material.EGG).amount(1).name("&aBridge Egg").lore("&7Cost: &22 Emerald").lore("").lore("&eClick to purchase!").get();
    public static final ItemStack MILK = new ItemBuilder(Material.MILK_BUCKET).amount(1).name("&aMagic Milk").lore("&7Cost: &64 Gold").lore("").lore("&eClick to purchase!").get();

    private Player player;
    public Purchasable(Player player) {
        this.player = player;
    }

    public ItemBuilder of(Material material, String name, int amount, int durability, int price, Currency currency, boolean forShop) {
        if (forShop) {
            Material currencyMaterial = currency.getMaterial();
            ChatColor currencyColor = currency.getColor();
            String currencyDisplay = currency.getDisplay();

            ItemBuilder item = new ItemBuilder(material);
            item.amount(amount);
            item.durability(durability);

            boolean hasEnoughToBuy = InventoryUtil.contains(player.getInventory(), new ItemStack(currencyMaterial, price));
            item.name((hasEnoughToBuy ? ChatColor.GREEN : ChatColor.RED) + name);
            item.lore("&7Cost: " + currencyColor + price + " " + currencyDisplay);
            item.lore("");
            if (hasEnoughToBuy) item.lore(ChatColor.YELLOW + "Click to purchase!");
            else item.lore(ChatColor.RED + "Missing: " + price + " " + currencyDisplay + "(s)!");

            return item.flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);

        } else return new ItemBuilder(material).name(ChatColor.YELLOW + name).amount(amount).durability(durability).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
    }
}