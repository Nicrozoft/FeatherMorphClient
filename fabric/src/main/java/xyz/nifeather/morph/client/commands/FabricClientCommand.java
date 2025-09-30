package xyz.nifeather.morph.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import xyz.nifeather.morph.shared.commands.IBrigadierCommand;

import java.util.List;

public class FabricClientCommand implements IBrigadierCommand<FabricClientCommandSource>
{
    private final List<IBrigadierCommand<FabricClientCommandSource>> subCommands = List.of();

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(
                ClientCommandManager.literal("morphclient")
                        .executes(this::executeNoArgs)
        );
    }

    private int executeNoArgs(CommandContext<FabricClientCommandSource> context)
    {
        context.getSource().sendFeedback(Component.literal("You are running FeatherMorphClient!"));
        return 1;
    }
}
