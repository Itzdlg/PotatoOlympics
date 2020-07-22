package me.schooltests.potatoolympics.core.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemBuilder implements Listener {
    private ItemStack item;
    private JavaPlugin plugin;
    private boolean registeredListener = false;
    private final Set<PotionEffect> effects = new HashSet<>();

    public ItemBuilder(final ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(final Material material) {
        item = new ItemStack(material);
    }

    public ItemBuilder type(final Material material) {
        item.setType(material);
        return this;
    }

    public ItemBuilder amount(final int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder durability(final int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public ItemBuilder name(final String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder enchant(final Enchantment ench, final int level) {
        item.addUnsafeEnchantment(ench, level);
        return this;
    }

    public ItemBuilder enchant(final Enchantment ench) {
        return enchant(ench, 1);
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(new ArrayList<String>());
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder effect(final PotionEffectType type, final int duration, final int amp, final boolean ambient) {
        return effect(new PotionEffect(type, duration, amp, ambient, false));
    }

    public ItemBuilder effect(final PotionEffect effect) {
        if (item.getType().equals(Material.POTION)) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            meta.addCustomEffect(effect, true);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder flag(ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder unbreakable(boolean value) {
        ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder color(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack get() {
        return item;
    }
}