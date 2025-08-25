package me.pizza.pizzadamage.api;

import me.pizza.pizzadamage.PizzaDamage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PizzaDamageAPI {

    public static void spawnHologram(Player player, Entity entity, String text) {
        List<Player> players = List.of(player);
        Component comp = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        PizzaDamage.getPlugin().getHologramManager().spawnHologram(players, entity, comp);
    }
}
