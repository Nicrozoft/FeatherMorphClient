package xyz.nifeather.morph.client.commands.subCommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.storage.SavedDisguiseStorage;
import xyz.nifeather.morph.client.storage.struct.SavedDisguise;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;
import xyz.nifeather.morph.shared.commands.IBrigadierCommand;

import java.util.concurrent.CompletableFuture;

public class SaveDisguiseSubCommand implements IBrigadierCommand<FabricClientCommandSource>
{
    @Override
    public void registerAsChild(ArgumentBuilder<FabricClientCommandSource, ?> parentBuilder)
    {
        parentBuilder.then(
                ClientCommandManager.literal("saved-morphs")
                        .then(
                                ClientCommandManager.literal("save")
                                        .then(
                                                ClientCommandManager.argument("name", StringArgumentType.greedyString())
                                                        .executes(this::runSaveDisguise)
                                        )
                        )
                        .then(
                                ClientCommandManager.literal("drop")
                                        .then(
                                                ClientCommandManager.argument("name", StringArgumentType.greedyString())
                                                        .suggests(this::suggestSavedDisguise)
                                                        .executes(this::runDropDisguise)
                                        )
                        )
                        .then(
                                ClientCommandManager.literal("disguise")
                                        .then(
                                                ClientCommandManager.argument("name", StringArgumentType.greedyString())
                                                        .suggests(this::suggestSavedDisguise)
                                                        .executes(this::runSelectDisguise)
                                        )
                        )
        );
    }

    private SavedDisguiseStorage savedDisguiseStorage()
    {
        return FeatherMorphClientBootstrap.getInstance().savedDisguiseStorage;
    }

    private CompletableFuture<Suggestions> suggestSavedDisguise(CommandContext<FabricClientCommandSource> context,
                                                                SuggestionsBuilder builder)
    {
        var input = builder.getRemaining();
        savedDisguiseStorage().listAll().forEach(id ->
        {
            if (id.contains(input))
                builder.suggest(id);
        });

        return builder.buildFuture();
    }

    private int runDropDisguise(CommandContext<FabricClientCommandSource> context)
    {
        var saveName = StringArgumentType.getString(context, "name");
        context.getSource().sendFeedback(Component.literal("Dropped %s!".formatted(saveName)));
        return 1;
    }

    private int runSelectDisguise(CommandContext<FabricClientCommandSource> context)
    {
        var saveName = StringArgumentType.getString(context, "name");
        var saved = this.savedDisguiseStorage().get(saveName);

        if (saved == null || saved.equals(savedDisguiseStorage().getDefault()))
        {
            context.getSource().sendFeedback(Component.literal("Not found!"));
            return 0;
        }

        FeatherMorphClientBootstrap.getInstance().requestDisguise(saved.disguiseIdentifier(), saved.properties());

        return 1;
    }

    private int runSaveDisguise(CommandContext<FabricClientCommandSource> context)
    {
        if (ClientDisguiseSyncer.getCurrentInstance() == null || ClientDisguiseSyncer.getCurrentInstance().disposed())
        {
            context.getSource().sendFeedback(Component.literal("Not disguising!"));
            return 0;
        }

        var saveName = StringArgumentType.getString(context, "name");
        var savedDisguise = SavedDisguise.fromSyncer(ClientDisguiseSyncer.getCurrentInstance());

        if (savedDisguiseStorage().save(savedDisguise, saveName))
            context.getSource().sendFeedback(Component.literal("Saved as %s!".formatted(saveName)));
        else
            context.getSource().sendFeedback(Component.literal("未能保存当前形态"));
        return 1;
    }
}
