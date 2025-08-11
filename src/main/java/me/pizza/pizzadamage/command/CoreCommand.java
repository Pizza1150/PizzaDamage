package me.pizza.pizzadamage.command;

import me.pizza.pizzadamage.PizzaDamage;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoreCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length != 1) return true;

        if (strings[0].equalsIgnoreCase("reload")) {
            PizzaDamage.getPlugin().getConfigManager().reload();
            commandSender.sendMessage("§aPizzaDamage reloaded");
            return true;
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) return List.of("reload");
        return List.of();
    }
}
