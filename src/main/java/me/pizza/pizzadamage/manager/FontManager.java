package me.pizza.pizzadamage.manager;

public class FontManager {

    private final ConfigManager config;

    public FontManager(ConfigManager configManager) {
        this.config = configManager;
    }

    public String toCustomFont(String number, FontType fontType) {
        if (!config.isUseCustomFont()) return number;

        StringBuilder builder = new StringBuilder();
        boolean firstDigitFound = false;

        for (char c : number.toCharArray()) {
            if (c >= '0' && c <= '9') {
                if (firstDigitFound) builder.append(config.getSpace());
                int digit = c - '0';

                switch (fontType) {
                    case ELEMENT -> builder.append(config.getElementCharacters()[digit]);
                    case DOT -> builder.append(config.getDotCharacters()[digit]);
                    case SKILL -> builder.append(config.getSkillCharacters()[digit]);
                    case CRIT -> builder.append(config.getCritCharacters()[digit]);
                    default -> builder.append(config.getNormalCharacters()[digit]);
                }
                
                firstDigitFound = true;
            } else builder.append(c);
        }
        return builder.toString();
    }

    public enum FontType {
        ELEMENT,
        DOT,
        SKILL,
        CRIT,
        NORMAL
    }
}
