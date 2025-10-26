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
import xyz.nifeather.morph.client.graphics.color.MaterialColors;
import xyz.nifeather.morph.client.network.handlers.V3ProtocolHandler;
import xyz.nifeather.morph.client.properties.struct.MorphEquipmentStruct;
import xyz.nifeather.morph.client.storage.SavedDisguiseStorage;
import xyz.nifeather.morph.client.storage.struct.SavedDisguise;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;
import xyz.nifeather.morph.network.Constants;
import xyz.nifeather.morph.network.utils.ProtocolEquipmentSlot;
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
                        .then(
                                ClientCommandManager.literal("refresh")
                                        .executes(this::refreshStorage)
                        )
        );
    }

    private int refreshStorage(CommandContext<FabricClientCommandSource> context)
    {
        savedDisguiseStorage().refresh();
        context.getSource().sendFeedback(Component.translatable("text.morphclient.refresh_cache"));
        return 1;
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

        Component message = savedDisguiseStorage().drop(saveName)
                ? Component.translatable("text.morphclient.drop_morph_success", saveName)
                : Component.translatable("text.morphclient.drop_morph_failed",saveName);

        context.getSource().sendFeedback(message);

        return 1;
    }

    private int runSelectDisguise(CommandContext<FabricClientCommandSource> context)
    {
        var saveName = StringArgumentType.getString(context, "name");
        var saved = this.savedDisguiseStorage().get(saveName);

        if (saved == null || saved.equals(savedDisguiseStorage().getDefault()))
        {
            context.getSource().sendFeedback(Component.translatable("text.morphclient.saved_morph_not_found"));
            return 0;
        }

        FeatherMorphClientBootstrap.getInstance().requestDisguise(saved.disguiseIdentifier(), saved.properties());

        return 1;
    }

    private int runSaveDisguise(CommandContext<FabricClientCommandSource> context)
    {
        if (ClientDisguiseSyncer.getCurrentInstance() == null || ClientDisguiseSyncer.getCurrentInstance().disposed())
        {
            context.getSource().sendFeedback(Component.translatable("text.morphclient.not_disguising"));
            return 0;
        }

        var saveName = StringArgumentType.getString(context, "name");
        var savedDisguise = SavedDisguise.fromSyncer(ClientDisguiseSyncer.getCurrentInstance());

        if (savedDisguiseStorage().save(savedDisguise, saveName))
            context.getSource().sendFeedback(Component.translatable("text.morphclient.save_morph_success", saveName));
        else
            context.getSource().sendFeedback(Component.translatable("text.morphclient.save_morph_failed"));

        var serverHandler = FeatherMorphClientBootstrap.getInstance().serverHandler;
        var requiredApiLevel = Constants.ApiLevel.NETWORK_DISGUISE_PROPERTIES.protocolVersion;
        if (!(serverHandler.protocolHandler() instanceof V3ProtocolHandler) || serverHandler.getServerApiVersion() < requiredApiLevel)
            context.getSource().sendFeedback(Component.translatable("text.morphclient.saving_not_supported").withColor(MaterialColors.Amber500.getColor()));

        return 1;
    }
}
