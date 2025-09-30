package xyz.nifeather.morph.server.commands;

import xyz.nifeather.morph.server.commands.impl.MorphCommand;
import xyz.nifeather.morph.server.commands.impl.PluginMainCommand;
import xyz.nifeather.morph.server.commands.impl.UnMorphCommand;

import java.util.List;

public class CommandHub
{
    public CommandHub()
    {
        commands = List.of(
                new MorphCommand(),
                new UnMorphCommand(),
                new PluginMainCommand()
        );
    }

    private final List<IServerBrigadierCommand> commands;

    public void registerCommands(CommandRegistrationContext context)
    {
        commands.forEach(ibc -> ibc.register(context.dispatcher()));
    }
}
