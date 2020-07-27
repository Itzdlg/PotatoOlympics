package me.schooltests.potatoolympics.bedwars.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerPurchaseItemEvent extends PlayerEvent implements Cancellable {
    Player player;
    ItemStack item;
    boolean cancelled;

    public PlayerPurchaseItemEvent(Player player, ItemStack item) {
        super(player);
        this.player = player;
        this.item = item;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public ItemStack getItem() {
        return item;
    }
}
