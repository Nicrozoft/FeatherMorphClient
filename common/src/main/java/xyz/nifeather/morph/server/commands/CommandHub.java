package xyz.nifeather.morph.server.commands;

import xyz.nifeather.morph.server.commands.impl.MorphCommand;
import xyz.nifeather.morph.server.commands.impl.PluginMainCommand;
import xyz.nifeather.morph.server.commands.impl.UnMorphCommand;

public class CommandHub {
    private final IBrigadierCommand[] commands;

    public CommandHub() {
        commands = new IBrigadierCommand[]{new MorphCommand(),
                new UnMorphCommand(),
                new PluginMainCommand()};
    }

    public void registerCommands(CommandRegistrationContext context) {
        for (IBrigadierCommand command : commands) command.register(context.dispatcher());
    }
}
