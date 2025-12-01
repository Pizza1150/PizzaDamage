package me.pizza.pizzadamage.manager;

import lombok.Getter;
import me.pizza.pizzadamage.PizzaDamage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.EntityType;

@Getter
public class ConfigManager {

    private final PizzaDamage plugin;

    private final DecimalFormat decimalFormat = new DecimalFormat("#");

    private final Set<EntityType> blacklistEntityType = new HashSet<>();

    private final String[] normalCharacters = new String[10];
    private final String[] critCharacters = new String[10];
    private final String[] skillCharacters = new String[10];
    private final String[] critSkillCharacters = new String[10];
    private final String[] elementCharacters = new String[10];
    private final String[] dotCharacters = new String[10];

    private boolean onlyPlayer;
    private boolean showToAllPlayers;
    private boolean useCustomFont;

    private double showRadius;
    private double minDamage;

    private String space;

    public ConfigManager(PizzaDamage plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) plugin.saveDefaultConfig();

        plugin.reloadConfig();

        onlyPlayer = plugin.getConfig().getBoolean("hologram.only-player");
        showToAllPlayers = plugin.getConfig().getBoolean("hologram.show-to-all");
        useCustomFont = plugin.getConfig().getBoolean("custom-font.enabled");
        showRadius = plugin.getConfig().getDouble("hologram.show-radius");
        minDamage = plugin.getConfig().getDouble("hologram.min-damage");

        String spaceChar = plugin.getConfig().getString("custom-font.space");
        space = (spaceChar != null && !spaceChar.isEmpty()) ? spaceChar : " ";

        blacklistEntityType.clear();
        for (String type : plugin.getConfig().getStringList("hologram.blacklist-entity-type")) {
            try {
                EntityType entityType = EntityType.valueOf(type);
                blacklistEntityType.add(entityType);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid EntityType in config: " + type);
            }
        }

        for (int i = 0; i < 10; i++) {
            normalCharacters[i] = getConfigStringOrDefault("custom-font.normal." + i, String.valueOf(i));
            critCharacters[i] = getConfigStringOrDefault("custom-font.crit." + i, String.valueOf(i));
            skillCharacters[i] = getConfigStringOrDefault("custom-font.skill." + i, String.valueOf(i));
            critSkillCharacters[i] = getConfigStringOrDefault("custom-font.crit-skill." + i, String.valueOf(i));
            elementCharacters[i] = getConfigStringOrDefault("custom-font.element." + i, String.valueOf(i));
            dotCharacters[i] = getConfigStringOrDefault("custom-font.dot." + i, String.valueOf(i));
        }
    }

    private String getConfigStringOrDefault(String path, String defaultValue) {
        String value = plugin.getConfig().getString(path);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
