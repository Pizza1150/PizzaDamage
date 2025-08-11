package me.pizza.pizzadamage.data;

import lombok.AllArgsConstructor;

import org.bukkit.util.Vector;
import com.github.retrooper.packetevents.protocol.player.User;

@AllArgsConstructor
public class HologramData {

    public int entityId;
    public User user;
    public double x, y, z;
    public Vector dir;
    public double vy;
    public int life;
}
