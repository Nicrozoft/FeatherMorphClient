package xyz.nifeather.morph.server.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.IBrigadierCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.ManagementCommand;

import java.util.List;

public class PluginMainCommand extends BrigadierCommand
{
    public PluginMainCommand()
    {
        subCommands = List.of(
                new ManagementCommand()
        );
    }

    private final List<IBrigadierCommand> subCommands;

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        var cmd = CommandManager.literal("feathermorph");
        subCommands.forEach(ibc -> ibc.registerAsChild(cmd));

        dispatcher.register(cmd);
    }
}
