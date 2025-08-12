package me.pizza.pizzadamage.api.event;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CreateHologramEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    @Setter
    private Component text;

    public CreateHologramEvent(Component text) {
        this.text = text;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
