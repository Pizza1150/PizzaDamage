package me.pizza.pizzadamage.listener;

import io.lumine.mythic.lib.api.event.AttackUnregisteredEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import me.pizza.pizzadamage.PizzaDamage;
import me.pizza.pizzadamage.manager.FontManager.FontType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AttackListener implements Listener {

    private final PizzaDamage plugin;

    public AttackListener(PizzaDamage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(AttackUnregisteredEvent ev) {
        if (!ev.getAttack().isPlayer()) return;

        if (!plugin.getConfigManager().isSplitHologram()) {
            DamageMetadata meta = ev.getDamage();
            String damage = plugin.getConfigManager().getDecimalFormat().format(ev.toBukkit().getDamage());
            
            StringBuilder rawBuilder = new StringBuilder();

            if (!meta.collectElements().isEmpty()) {
                List<Element> elements = new ArrayList<>(meta.collectElements());
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    rawBuilder.append(element.getColor()).append(element.getLoreIcon());
                    if (i == elements.size() - 1) rawBuilder.append("&f");  // Last element
                    else rawBuilder.append(" ");
                }
                rawBuilder.append(plugin.getFontManager().toCustomFont(damage, FontType.NORMAL));
            } 
            
            else if (meta.hasType(DamageType.SKILL)) {
                rawBuilder.append(plugin.getFontManager().toCustomFont(damage, FontType.SKILL));
            } 
            
            else {
                FontType fontType = meta.isWeaponCriticalStrike() ? FontType.CRIT : FontType.NORMAL;
                rawBuilder.append(plugin.getFontManager().toCustomFont(damage, fontType));
            }

            String rawText = rawBuilder.toString();
            Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(rawText);
            plugin.getHologramManager().spawn(ev.getAttack().getPlayer(), ev.getEntity(), component);
        }

        // Split hologram
//        ev.getDamage().getPackets().forEach(packet -> {
//            String damage = decimalFormat.format(packet.getFinaldamage());
//            Element element = packet.getElement();
//
//            String rawText;
//            if (element != null) {
//                String customdamage = plugin.getConfigManager().toCustomFont(damage, ConfigManager.FontType.SKILL);
//                rawText = element.getColor() + element.getLoreIcon() + "&f" + customdamage;
//
//            } else if (packet.hasType(DamageType.SKILL)) {
//                rawText = plugin.getConfigManager().toCustomFont(damage, ConfigManager.FontType.SKILL);
//            }
//
//            else {
//                boolean isCrit = ev.getDamage().isWeaponCriticalStrike();
//                ConfigManager.FontType fontType = isCrit ? ConfigManager.FontType.CRIT : ConfigManager.FontType.NORMAL;
//                rawText = plugin.getConfigManager().toCustomFont(damage, fontType);
//            }
//
//            Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(rawText);
//            plugin.getHologramManager().spawn(ev.getPlayer(), ev.getEntity(), component);
//        });
    }
}
