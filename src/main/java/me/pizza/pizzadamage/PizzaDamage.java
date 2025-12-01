package me.pizza.pizzadamage;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import me.pizza.pizzadamage.command.PizzaDamageCommand;
import me.pizza.pizzadamage.listener.AttackListener;
import me.pizza.pizzadamage.listener.PlayerListener;
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
    public void onLoad() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new PizzaDamageCommand().createCommand());
        });
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        plugin = this;

        hologramManager = new HologramManager();
        configManager = new ConfigManager(this);
        fontManager = new FontManager(configManager);

        getServer().getPluginManager().registerEvents(new AttackListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        hologramManager.removeAllHolograms();
    }
}
