package xyz.nifeather.morph.shared.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;

public interface IBrigadierCommand<S>
{
    default void register(CommandDispatcher<S> dispatcher)
    {
    }

    default void registerAsChild(ArgumentBuilder<S, ?> parentBuilder)
    {
    }
}
