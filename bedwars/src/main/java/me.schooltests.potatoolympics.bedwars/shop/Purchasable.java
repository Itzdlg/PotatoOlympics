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

public final class Purchasable { private Player player;
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