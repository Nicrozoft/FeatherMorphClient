package xyz.nifeather.morph.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public interface IBrigadierCommand
{
    default void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
    }

    default void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
    }
}
