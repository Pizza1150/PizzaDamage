package me.pizza.pizzadamage.listener;

import me.pizza.pizzadamage.PizzaDamage;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final PizzaDamage plugin;

    public PlayerListener(PizzaDamage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent ev) {
        plugin.getHologramManager().getHolograms().forEach(holo -> {
            holo.users().removeIf(user -> user.getUUID().equals(ev.getPlayer().getUniqueId()));
        });
    }
}