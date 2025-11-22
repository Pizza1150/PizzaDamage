package me.pizza.pizzadamage.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.pizza.pizzadamage.PizzaDamage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class PizzaDamageCommand {

    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("pizzadamage")
                .requires(command -> command.getSender().isOp())

                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            PizzaDamage.getPlugin().getConfigManager().reload();
                            ctx.getSource().getSender().sendRichMessage("<#79ff53>PizzaDamage reloaded");

                            return Command.SINGLE_SUCCESS;
                        }))

                .then(Commands.literal("clear")
                        .executes(ctx -> {
                            PizzaDamage.getPlugin().getHologramManager().removeAllHolograms();
                            ctx.getSource().getSender().sendRichMessage("<#79ff53>Cleared all damage holograms");

                            return Command.SINGLE_SUCCESS;
                        }))

                .then(Commands.literal("debug")
                        .executes(ctx -> {
                            int amount = PizzaDamage.getPlugin().getHologramManager().getHolograms().size();
                            ctx.getSource().getSender().sendRichMessage("<#ffd039>There is " + amount + " damage holograms");

                            return Command.SINGLE_SUCCESS;
                        }))

                .build();
    }
}
