package me.pizza.pizzadamage.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.pizza.pizzadamage.PizzaDamage;

public class CoreCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String@NotNull [] strings) {
        if (strings.length != 1) return true;

        if (strings[0].equalsIgnoreCase("reload")) {
            PizzaDamage.getPlugin().getConfigManager().reload();
            commandSender.sendMessage("§aPizzaDamage reloaded");
            return true;
        }

        else if (strings[0].equalsIgnoreCase("debug")) {
            int n = PizzaDamage.getPlugin().getHologramManager().getHolograms().size();
            commandSender.sendMessage("There is " + n + " holograms now");
            return true;
        }

        else if (strings[0].equalsIgnoreCase("clear")) {
            PizzaDamage.getPlugin().getHologramManager().removeAllHolograms();
            commandSender.sendMessage("Cleared all damage holograms!");
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String@NotNull [] strings) {
        if (strings.length == 1) return List.of("reload", "debug", "clear");
        return List.of();
    }
}
