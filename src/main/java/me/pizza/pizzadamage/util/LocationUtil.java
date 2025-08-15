package me.pizza.pizzadamage.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LocationUtil {

    private static final Random RANDOM = new Random();

    public static List<Player> getPlayersInRadius(Location location, double radius) {
        List<Player> players = new ArrayList<>();
        for (Entity e : location.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof Player p) players.add(p);
        }
        return players;
    }

    public static Location getRandomLocation(Entity entity) {
        double a = RANDOM.nextDouble() * (2.0 * Math.PI);
        double width = (entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ()) / 2.0;
        // double r = R_OFFSET + (width * ENTITY_WIDTH_PERCENT) + RANDOM.nextDouble() * 0.2;
        double r = 0.1 + (width * 0.75) + RANDOM.nextDouble() * 0.2;
        // double h = Y_OFFSET + entity.getHeight() * ENTITY_HEIGHT_PERCENT + (RANDOM.nextDouble() - 0.5) * 0.2;
        double h = 0.15 + entity.getHeight() * 1.0 + (RANDOM.nextDouble() - 0.5) * 0.2;
        return entity.getLocation().add(Math.cos(a) * r, h, Math.sin(a) * r);
    }
}
