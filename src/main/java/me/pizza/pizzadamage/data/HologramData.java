package me.pizza.pizzadamage.data;

import lombok.AllArgsConstructor;

import java.util.List;

import com.github.retrooper.packetevents.protocol.player.User;

@AllArgsConstructor
public class HologramData {

    public int entityId;
    public List<User> users;
}
