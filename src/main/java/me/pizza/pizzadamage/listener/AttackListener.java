package me.pizza.pizzadamage.listener;

import io.lumine.mythic.lib.api.event.AttackUnregisteredEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import me.pizza.pizzadamage.PizzaDamage;
import me.pizza.pizzadamage.manager.FontManager.FontType;
import me.pizza.pizzadamage.util.LocationUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AttackListener implements Listener {

    private final PizzaDamage plugin;

    public AttackListener(PizzaDamage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(AttackUnregisteredEvent ev) {
        // Listen only when attacker is a player for better performance
        if (!ev.getAttack().hasAttacker() || !ev.getAttack().isPlayer()) return;

        // Check if there are players who can see the hologram
        List<Player> players = (plugin.getConfigManager().isShowToAllPlayers())
                ? LocationUtil.getPlayersInRadius(ev.getEntity().getLocation(), plugin.getConfigManager().getShowRadius())
                : (ev.getAttack().isPlayer())
                        ? List.of(ev.getAttack().getPlayer())
                        : List.of();

        if (players.isEmpty()) return;

        double value = ev.toBukkit().getFinalDamage();
        if (value < 1) return;

        StringBuilder builder = new StringBuilder();
        String damage = plugin.getConfigManager().getDecimalFormat().format(value);

        DamageMetadata meta = ev.getDamage();
        List<Element> elements = new ArrayList<>(meta.collectElements());
        if (!elements.isEmpty()) {
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                builder.append(element.getColor()).append(element.getLoreIcon());
                String text = (i == elements.size() - 1) ? "&f" : " ";
                builder.append(text);
            }
            builder.append(plugin.getFontManager().toCustomFont(damage, FontType.ELEMENT));
        }

        else if (meta.hasType(DamageType.SKILL)) {
            builder.append(plugin.getFontManager().toCustomFont(damage, FontType.SKILL));
        }

        else {
            FontType fontType = meta.isWeaponCriticalStrike() ? FontType.CRIT : FontType.NORMAL;
            builder.append(plugin.getFontManager().toCustomFont(damage, fontType));
        }

        String text = builder.toString();
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        plugin.getHologramManager().spawnHologram(players, ev.getEntity(), component);
    }
}
