package me.pizza.pizzadamage.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.mythic.lib.api.event.AttackUnregisteredEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import me.pizza.pizzadamage.PizzaDamage;
import me.pizza.pizzadamage.manager.FontManager.FontType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AttackListener implements Listener {

    private final PizzaDamage plugin;

    public AttackListener(PizzaDamage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(AttackUnregisteredEvent ev) {
        if (!ev.getAttack().isPlayer()) return;

        DamageMetadata meta = ev.getDamage();
        
        if (!plugin.getConfigManager().isSplitHologram()) {
            String damage = plugin.getConfigManager().getDecimalFormat().format(meta.getDamage());
            
            StringBuilder builder = new StringBuilder();

            if (!meta.collectElements().isEmpty()) {
                List<Element> elements = new ArrayList<>(meta.collectElements());
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    builder.append(element.getColor()).append(element.getLoreIcon());
                    if (i == elements.size() - 1) builder.append("&f");  // Last element
                    else builder.append(" ");
                }
                builder.append(plugin.getFontManager().toCustomFont(damage, FontType.SKILL));
            }

            else if (meta.hasType(DamageType.SKILL)) {
                builder.append(plugin.getFontManager().toCustomFont(damage, FontType.SKILL));
            } 
            
            else {
                FontType fontType = meta.isWeaponCriticalStrike() ? FontType.CRIT : FontType.NORMAL;
                builder.append(plugin.getFontManager().toCustomFont(damage, fontType));
            }

            String rawText = builder.toString();
            Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(rawText);

            Player player = ev.getAttack().getPlayer();
            if (plugin.getConfigManager().isShowToAllPlayers()) {
                double radius = plugin.getConfigManager().getShowRadius();
                List<Player> players = getPlayersAround(player, radius);
                plugin.getHologramManager().spawn(players, ev.getEntity(), component);
            } else plugin.getHologramManager().spawn(List.of(player), ev.getEntity(), component);
        }

         else {
             meta.getPackets().forEach(packet -> {
                 String damage = plugin.getConfigManager().getDecimalFormat().format(packet.getFinalValue());
                 Element element = packet.getElement();

                 StringBuilder builder = new StringBuilder();

                 if (element != null) {
                     builder.append(element.getColor()).append(element.getLoreIcon()).append("&f");
                     builder.append(plugin.getFontManager().toCustomFont(damage, FontType.SKILL));
                 }

                 else if (packet.hasType(DamageType.SKILL)) {
                     builder.append(plugin.getFontManager().toCustomFont(damage, FontType.SKILL));
                 }

                 else {
                     FontType fontType = meta.isWeaponCriticalStrike() ? FontType.CRIT : FontType.NORMAL;
                     builder.append(plugin.getFontManager().toCustomFont(damage, fontType));
                 }

                 String rawText = builder.toString();
                 Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(rawText);

                 Player player = ev.getAttack().getPlayer();
                 if (plugin.getConfigManager().isShowToAllPlayers()) {
                     double radius = plugin.getConfigManager().getShowRadius();
                     List<Player> players = getPlayersAround(player, radius);
                     plugin.getHologramManager().spawn(players, ev.getEntity(), component);
                 } else plugin.getHologramManager().spawn(List.of(player), ev.getEntity(), component);
             });
         }
    }

    private List<Player> getPlayersAround(Player player, double radius) {
        List<Player> players = new ArrayList<>();
        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof Player p) players.add(p);
        }
        players.add(player);
        return players;
    }
}
