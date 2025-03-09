package xyz.nifeather.morph.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

public interface IBrigadierCommand
{
    default void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
    }

    default void registerAsChild(ArgumentBuilder<ServerCommandSource, ?> parentBuilder)
    {
    }
}
