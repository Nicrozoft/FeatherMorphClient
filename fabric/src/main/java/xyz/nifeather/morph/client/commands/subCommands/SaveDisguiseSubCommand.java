package xyz.nifeather.morph.client.commands.subCommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import xyz.nifeather.morph.FeatherMorphCommonBootstrap;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;
import xyz.nifeather.morph.shared.commands.IBrigadierCommand;

public class SaveDisguiseSubCommand implements IBrigadierCommand<FabricClientCommandSource>
{
    @Override
    public void registerAsChild(ArgumentBuilder<FabricClientCommandSource, ?> parentBuilder)
    {
        parentBuilder.then(
                ClientCommandManager.literal("favorite-disguise")
                        .then(
                                ClientCommandManager.literal("save")
                                        .then(
                                                ClientCommandManager.argument("name", StringArgumentType.word())
                                                        .executes(this::runSaveDisguise)
                                        )
                        )
                        .then(
                                ClientCommandManager.literal("disguise")
                                        .then(
                                                ClientCommandManager.argument("name", StringArgumentType.word())
                                                        .executes(this::runSelectDisguise)
                                        )
                        )
        );
    }

    private int runSelectDisguise(CommandContext<FabricClientCommandSource> context)
    {
        return 1;
    }

    private int runSaveDisguise(CommandContext<FabricClientCommandSource> context)
    {
        var morphManager = FeatherMorphClientBootstrap.getInstance().morphManager;
        if (ClientDisguiseSyncer.getCurrentInstance() == null)
        {
            context.getSource().sendFeedback(Component.literal("Not disguising!"));
            return 0;
        }

        var saveName = StringArgumentType.getString(context, "name");
        return 1;
    }
}
