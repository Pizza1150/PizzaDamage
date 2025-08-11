package me.pizza.pizzadamage.manager;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import me.pizza.pizzadamage.PizzaDamage;
import me.pizza.pizzadamage.data.HologramData;
import net.kyori.adventure.text.Component;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;

public class HologramManager {

    private static final double RADIAL_VELOCITY = 0.3;
    private static final double GRAVITY = 0.0;
    private static final double INITIAL_UPWARD_VELOCITY = 0.3;
    private static final double ENTITY_HEIGHT_PERCENT = 1;
    private static final double Y_OFFSET = 0.15;
    private static final double R_OFFSET = 0.1;
    private static final double ENTITY_WIDTH_PERCENT = 0.75;
    private static final Random RANDOM = new Random();

    private final List<HologramData> holograms = new ArrayList<>();

    public HologramManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PizzaDamage.getPlugin(), () -> {
            Iterator<HologramData> it = holograms.iterator();
            while (it.hasNext()) {
                HologramData holo = it.next();

                holo.vy += -10.0 * GRAVITY * 0.15;
                holo.x += holo.dir.getX() * 0.15;
                holo.y += holo.vy * 0.15;
                holo.z += holo.dir.getZ() * 0.15;
                com.github.retrooper.packetevents.protocol.world.Location location = new com.github.retrooper.packetevents.protocol.world.Location(holo.x, holo.y, holo.z, 0f, 0f);

                holo.user.sendPacket(new WrapperPlayServerEntityTeleport(
                        holo.entityId,
                        location,
                        true
                ));

                holo.life--;
                if (holo.life <= 0) {
                    holo.user.sendPacket(new WrapperPlayServerDestroyEntities(holo.entityId));
                    it.remove();
                }
            }
        }, 0L, 3L);
    }

    public void spawn(Player player, Entity entity, Component text) {
        Bukkit.getScheduler().runTaskAsynchronously(PizzaDamage.getPlugin(), () -> {
            int entityId = SpigotReflectionUtil.generateEntityId();
            Location loc = getSpawnLocation(entity);
            Vector dir = getDirection(player, entity).multiply(2.0 * RADIAL_VELOCITY);

            User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

            user.sendPacket(new WrapperPlayServerSpawnEntity(
                    entityId, UUID.randomUUID(),
                    EntityTypes.TEXT_DISPLAY,
                    SpigotConversionUtil.fromBukkitLocation(loc),
                    0F, 0, null
            ));
            
            user.sendPacket(new WrapperPlayServerEntityMetadata(entityId, List.of(
                    new EntityData<>(23, EntityDataTypes.ADV_COMPONENT, text),
                    new EntityData<>(12, EntityDataTypes.VECTOR3F, new Vector3f(1.2F, 1.2F, 1.2F)),
                    new EntityData<>(10, EntityDataTypes.INT, 3),
                    new EntityData<>(15, EntityDataTypes.BYTE, (byte) 3),
                    new EntityData<>(25, EntityDataTypes.INT, 0x00000000)
            )));

            holograms.add(new HologramData(
                    entityId, user,
                    loc.getX(), loc.getY(), loc.getZ(),
                    dir, INITIAL_UPWARD_VELOCITY * 6,
                    20
            ));
        });
    }

    private Location getSpawnLocation(Entity entity) {
        double a = RANDOM.nextDouble() * (2.0 * Math.PI);
        double width = (entity.getBoundingBox().getWidthX() + entity.getBoundingBox().getWidthZ()) / 2.0;
        double r = R_OFFSET + (width * ENTITY_WIDTH_PERCENT) + RANDOM.nextDouble() * 0.2;
        double h = Y_OFFSET + entity.getHeight() * ENTITY_HEIGHT_PERCENT + (RANDOM.nextDouble() - 0.5) * 0.2;
        return entity.getLocation().add(Math.cos(a) * r, h, Math.sin(a) * r);
    }

    private Vector getDirection(Entity attacker, Entity target) {
        Vector dir = target.getLocation().toVector().subtract(attacker.getLocation().toVector()).setY(0);
        if (dir.lengthSquared() > 0.0) {
            double a = Math.atan2(dir.getZ(), dir.getX());
            a += (Math.PI / 2D) * (RANDOM.nextDouble() - 0.5D);
            return new Vector(Math.cos(a), 0.0, Math.sin(a));
        }
        return dir;
    }

    public void removeAllHolograms() {
        holograms.forEach(holo -> holo.user.sendPacket(new WrapperPlayServerDestroyEntities(holo.entityId)));
        holograms.clear();
    }
}