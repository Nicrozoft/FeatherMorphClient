package xyz.nifeather.morph.server.commands;

import xyz.nifeather.morph.server.commands.impl.MorphCommand;
import xyz.nifeather.morph.server.commands.impl.UnMorphCommand;

import java.util.List;

public class FabricCommandHub
{
    public FabricCommandHub()
    {
        commands = List.of(
                new MorphCommand(),
                new UnMorphCommand()
        );
    }

    private final List<IBrigadierCommand> commands;

    public void registerCommands(CommandRegistrationContext context)
    {
        commands.forEach(ibc -> ibc.register(context.dispatcher()));
    }
}
