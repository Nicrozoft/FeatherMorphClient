package xyz.nifeather.morph.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import xyz.nifeather.morph.client.commands.subCommands.SaveDisguiseSubCommand;
import xyz.nifeather.morph.shared.commands.IBrigadierCommand;

import java.util.List;

public class FabricClientCommand implements IBrigadierCommand<FabricClientCommandSource>
{
    private final List<IBrigadierCommand<FabricClientCommandSource>> subCommands = List.of(
            new SaveDisguiseSubCommand()
    );

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        var cmd = ClientCommandManager.literal("morphclient")
                .executes(this::executeNoArgs);

        subCommands.forEach(child -> child.registerAsChild(cmd));

        dispatcher.register(cmd);
    }

    private int executeNoArgs(CommandContext<FabricClientCommandSource> context)
    {
        context.getSource().sendFeedback(Component.literal("You are running FeatherMorphClient!"));
        return 1;
    }
}
