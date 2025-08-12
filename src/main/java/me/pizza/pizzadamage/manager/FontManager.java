package me.pizza.pizzadamage.manager;

public class FontManager {

    private final ConfigManager config;

    public FontManager(ConfigManager configManager) {
        this.config = configManager;
    }

    public String toCustomFont(String number, FontType fontType) {
        if (!config.isUseCustomFont()) return number;

        StringBuilder sb = new StringBuilder();
        boolean firstDigitFound = false;

        for (char c : number.toCharArray()) {
            if (c >= '0' && c <= '9') {
                if (firstDigitFound) sb.append(config.getSpace());
                int digit = c - '0';

                switch (fontType) {
                    case CRIT -> sb.append(config.getCritCharacters()[digit]);
                    case SKILL -> sb.append(config.getSkillCharacters()[digit]);
                    default -> sb.append(config.getNormalCharacters()[digit]);
                }
                
                firstDigitFound = true;
            } 
            else sb.append(c);
        }
        return sb.toString();
    }

    public enum FontType {
        NORMAL, 
        CRIT, 
        SKILL
    }
}
