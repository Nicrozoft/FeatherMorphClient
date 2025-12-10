package xyz.nifeather.morph.server.commands.impl.plugin;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.Permissions;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.IServerBrigadierCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageGrantCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageMorphCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageRevokeCommand;
import xyz.nifeather.morph.server.commands.impl.plugin.managements.ManageUnMorphCommand;

import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

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

    private final List<IServerBrigadierCommand> subCommands;

    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        var then = Commands.literal("manage")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN));

        subCommands.forEach(ibc -> ibc.registerAsChild(then));

        parentBuilder.then(then);
    }
}
