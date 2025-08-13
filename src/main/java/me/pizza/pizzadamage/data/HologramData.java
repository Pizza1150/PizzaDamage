package me.pizza.pizzadamage.data;

import java.util.List;

import com.github.retrooper.packetevents.protocol.player.User;

public record HologramData(
        int entityId,
        List<User> users) {}
