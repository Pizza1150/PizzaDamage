package me.pizza.pizzadamage.manager;

import lombok.Getter;
import me.pizza.pizzadamage.PizzaDamage;

import java.io.File;
import java.text.DecimalFormat;

@Getter
public class ConfigManager {

    private final PizzaDamage plugin;

    private final DecimalFormat decimalFormat = new DecimalFormat("#");

    private final char[] normalCharacters = new char[10];
    private final char[] critCharacters = new char[10];
    private final char[] skillCharacters = new char[10];
    private final char[] elementCharacters = new char[10];
    private final char[] dotCharacters = new char[10];

    private boolean showToAllPlayers;
    private boolean useCustomFont;

    private double showRadius;
    private double minDamage;

    private char space;

    public ConfigManager(PizzaDamage plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) plugin.saveDefaultConfig();

        plugin.reloadConfig();

        showToAllPlayers = plugin.getConfig().getBoolean("hologram.show-to-all");
        useCustomFont = plugin.getConfig().getBoolean("custom-font.enabled");
        showRadius = plugin.getConfig().getDouble("hologram.show-radius");
        minDamage = plugin.getConfig().getDouble("hologram.min-damage");

        String spaceChar = plugin.getConfig().getString("custom-font.space");
        space = spaceChar != null && !spaceChar.isEmpty() ? spaceChar.charAt(0) : ' ';

        for (int i = 0; i < 10; i++) {
            String normalChar = plugin.getConfig().getString("custom-font.normal." + i);
            String critChar = plugin.getConfig().getString("custom-font.crit." + i);
            String skillChar = plugin.getConfig().getString("custom-font.skill." + i);
            String elementChar = plugin.getConfig().getString("custom-font.element." + i);
            String dotChar = plugin.getConfig().getString("custom-font.dot." + i);

            if (normalChar != null && !normalChar.isEmpty()) normalCharacters[i] = normalChar.charAt(0);
            else normalCharacters[i] = (char) ('0' + i);

            if (critChar != null && !critChar.isEmpty()) critCharacters[i] = critChar.charAt(0);
            else critCharacters[i] = (char) ('0' + i);

            if (skillChar != null && !skillChar.isEmpty()) skillCharacters[i] = skillChar.charAt(0);
            else skillCharacters[i] = (char) ('0' + i);

            if (elementChar != null && !elementChar.isEmpty()) elementCharacters[i] = elementChar.charAt(0);
            else elementCharacters[i] = (char) ('0' + i);

            if (dotChar != null && !dotChar.isEmpty()) dotCharacters[i] = dotChar.charAt(0);
            else dotCharacters[i] = (char) ('0' + i);
        }
    }
}