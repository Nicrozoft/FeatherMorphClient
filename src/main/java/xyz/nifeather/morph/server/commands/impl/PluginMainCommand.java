package xyz.nifeather.morph.server.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.IBrigadierCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.ManagementCommand;

import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

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
    public void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        var cmd = Commands.literal("feathermorph");
        subCommands.forEach(ibc -> ibc.registerAsChild(cmd));

        dispatcher.register(cmd);
    }
}
