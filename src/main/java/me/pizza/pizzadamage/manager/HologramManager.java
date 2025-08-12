package me.pizza.pizzadamage.manager;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import lombok.Getter;
import me.pizza.pizzadamage.PizzaDamage;
import me.pizza.pizzadamage.api.event.CreateHologramEvent;
import me.pizza.pizzadamage.data.HologramData;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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

    private static final Random RANDOM = new Random();
    private static final double Y_OFFSET = 0.15;
    private static final double R_OFFSET = 0.1;
    private static final double ENTITY_HEIGHT_PERCENT = 1;
    private static final double ENTITY_WIDTH_PERCENT = 0.75;

    @Getter
    private final List<HologramData> holograms = new ArrayList<>();

    public void spawn(List<Player> players, Entity entity, Component text) {
        if (players == null || players.isEmpty() || entity == null || text == null) return;

        CreateHologramEvent event = new CreateHologramEvent(text);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        Bukkit.getScheduler().runTaskAsynchronously(PizzaDamage.getPlugin(), () -> {
            int entityId = SpigotReflectionUtil.generateEntityId();
            List<User> users = players
                    .stream()
                    .map(p -> PacketEvents.getAPI().getPlayerManager().getUser(p))
                    .toList();

            Bukkit.getScheduler().runTask(PizzaDamage.getPlugin(), () -> {
                Location spawnLocation = getSpawnLocation(entity);
                HologramData hologram = new HologramData(entityId, users);
                holograms.add(hologram);

                Bukkit.getScheduler().runTaskAsynchronously(PizzaDamage.getPlugin(), () -> {
                    users.forEach(user -> {
                        user.sendPacket(new WrapperPlayServerSpawnEntity(
                                entityId, UUID.randomUUID(),
                                EntityTypes.TEXT_DISPLAY,
                                SpigotConversionUtil.fromBukkitLocation(spawnLocation),
                                0F, 0, null
                        ));

                        user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, List.of(
                                new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, event.getText()),
                                new EntityData<>(10, EntityDataTypes.INT, 50),
                                new EntityData<>(15, EntityDataTypes.BYTE, (byte) 3),
                                new EntityData<>(25, EntityDataTypes.INT, 0x00000000),
                                new EntityData<>(16, EntityDataTypes.INT, (15 << 4) | (15 << 20))
                        )));

                        user.sendPacket(new WrapperPlayServerEntityTeleport(
                                entityId,
                                SpigotConversionUtil.fromBukkitLocation(spawnLocation.clone().add(0, 4, 0)),
                                true
                        ));
                    });
                    // TODO: Check if this should be global task
                    Bukkit.getScheduler().runTaskLater(PizzaDamage.getPlugin(), () -> removeHologram(hologram), 60L);
                });
            });
        });
    }

    private void removeHologram(HologramData hologram) {
        hologram.users.forEach(user -> user.sendPacket(new WrapperPlayServerDestroyEntities(hologram.entityId)));
        holograms.remove(hologram);
    }

    public void removeAllHolograms() {
        List<HologramData> copy = new ArrayList<>(holograms);
        copy.forEach(this::removeHologram);
        holograms.clear();
    }

    private Location getSpawnLocation(Entity entity) {
        double a = RANDOM.nextDouble() * (2.0 * Math.PI);
        double width = (entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ()) / 2.0;
        double r = R_OFFSET + (width * ENTITY_WIDTH_PERCENT) + RANDOM.nextDouble() * 0.2;
        double h = Y_OFFSET + entity.getHeight() * ENTITY_HEIGHT_PERCENT + (RANDOM.nextDouble() - 0.5) * 0.2;
        return entity.getLocation().add(Math.cos(a) * r, h, Math.sin(a) * r);
    }
}