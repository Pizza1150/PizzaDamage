package me.pizza.pizzadamage.manager;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import me.pizza.pizzadamage.PizzaDamage;
import me.pizza.pizzadamage.api.event.HologramSpawnEvent;
import me.pizza.pizzadamage.data.HologramData;
import me.pizza.pizzadamage.util.LocationUtil;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;

public class HologramManager {

    @Getter
    private final Queue<HologramData> holograms = new ConcurrentLinkedQueue<>();

    public void spawnHologram(List<Player> players, Entity entity, Component text) {
        if (players == null || players.isEmpty() || entity == null || text == null) return;

        HologramSpawnEvent event = new HologramSpawnEvent(text);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        Bukkit.getScheduler().runTaskAsynchronously(PizzaDamage.getPlugin(), () -> {
            int entityId = SpigotReflectionUtil.generateEntityId();
            List<User> users = new ArrayList<>(players.stream()
                    .map(PacketEvents.getAPI().getPlayerManager()::getUser)
                    .toList()
            );

            HologramData hologram = new HologramData(entityId, users);
            holograms.add(hologram);

            // TODO: Check if this should run async?
            Location location = LocationUtil.getRandomLocation(entity);

            users.forEach(user -> {
                user.sendPacket(new WrapperPlayServerSpawnEntity(
                        entityId, UUID.randomUUID(),
                        EntityTypes.TEXT_DISPLAY,
                        SpigotConversionUtil.fromBukkitLocation(location),
                        0F, 0, null
                ));

                user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, List.of(
                        new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, event.getText()),
                        new EntityData<>(10, EntityDataTypes.INT, 60),
                        new EntityData<>(15, EntityDataTypes.BYTE, (byte) 3),
                        new EntityData<>(25, EntityDataTypes.INT, 0x00000000),
                        new EntityData<>(16, EntityDataTypes.INT, (15 << 4) | (15 << 20))
                )));

                user.sendPacket(new WrapperPlayServerEntityTeleport(
                        entityId,
                        SpigotConversionUtil.fromBukkitLocation(location.clone().add(0, 4, 0)),
                        true
                ));
            });

            // TODO: Check if this should be global task
            Bukkit.getScheduler().runTaskLaterAsynchronously(PizzaDamage.getPlugin(), () -> removeHologram(hologram), 60L);
        });
    }

    public void clearPlayerHolograms(Player player) {
        holograms.forEach(holo -> holo.users().removeIf(user -> user.getUUID().equals(player.getUniqueId())));
    }

    private void removeHologram(HologramData hologram) {
        hologram.users().forEach(user -> user.sendPacket(new WrapperPlayServerDestroyEntities(hologram.entityId())));
        holograms.remove(hologram);
    }

    public void removeAllHolograms() {
        holograms.forEach(this::removeHologram);
    }
}