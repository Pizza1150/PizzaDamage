package me.pizza.pizzadamage;

import lombok.Getter;
import me.pizza.pizzadamage.command.CoreCommand;
import me.pizza.pizzadamage.listener.AttackListener;
import me.pizza.pizzadamage.manager.ConfigManager;
import me.pizza.pizzadamage.manager.FontManager;
import me.pizza.pizzadamage.manager.HologramManager;

import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class PizzaDamage extends JavaPlugin {

    @Getter
    private static PizzaDamage plugin;

    private HologramManager hologramManager;
    private ConfigManager configManager;
    private FontManager fontManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        plugin = this;

        hologramManager = new HologramManager();
        configManager = new ConfigManager(this);
        fontManager = new FontManager(configManager);

        getCommand("pizzadamage").setExecutor(new CoreCommand());

        getServer().getPluginManager().registerEvents(new AttackListener(this), this);
    }

    @Override
    public void onDisable() {
        hologramManager.removeAllHolograms();
    }
}
