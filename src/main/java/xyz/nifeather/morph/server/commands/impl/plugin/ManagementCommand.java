package xyz.nifeather.morph.server.commands.impl.plugin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.IBrigadierCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageGrantCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageMorphCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageRevokeCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageUnMorphCommand;

import java.util.List;

public class ManagementCommand extends BrigadierCommand
{
    public ManagementCommand()
    {
        subCommands = List.of(
                new ManageGrantCommand(),
                new ManageRevokeCommand(),
                new ManageMorphCommand(),
                new ManageUnMorphCommand()
        );
    }

    private final List<IBrigadierCommand> subCommands;

    @Override
    public void registerAsChild(ArgumentBuilder<ServerCommandSource, ?> parentBuilder)
    {
        var then = CommandManager.literal("manage")
                .requires(source -> source.hasPermissionLevel(server.getOpPermissionLevel()));

        subCommands.forEach(ibc -> ibc.registerAsChild(then));

        parentBuilder.then(then);
    }
}
