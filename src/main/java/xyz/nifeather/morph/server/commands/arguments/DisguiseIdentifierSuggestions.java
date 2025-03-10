package xyz.nifeather.morph.server.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import xiamomc.pluginbase.Managers.DependencyManager;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.server.FeatherMorphFabricMain;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DisguiseIdentifierSuggestions
{
    public static <S> CompletableFuture<Suggestions> forPlayer(final CommandContext<S> context, final SuggestionsBuilder builder)
    {
        var dependencies = DependencyManager.getInstance(FeatherMorphFabricMain.pluginNamespace());
        if (dependencies == null)
            return builder.buildFuture();

        var morphManager = dependencies.get(FabricMorphManager.class, false);
        if (morphManager == null)
            return builder.buildFuture();

        var source = context.getSource();

        PlayerEntity player = null;

        if (source instanceof ServerCommandSource serverCommandSource)
            player = serverCommandSource.getPlayer();

        if (player == null)
            return builder.buildFuture();

        String input = builder.getRemainingLowerCase();

        var availableDisguises = morphManager.getUnlockedDisguiseIds(player);

        return CompletableFuture.supplyAsync(() ->
        {
            for (String identifier : availableDisguises)
            {
                if (!identifier.contains(input))
                    continue;

                builder.suggest(identifier);
            }

            return builder.build();
        });
    }

    public static <S> CompletableFuture<Suggestions> allAvailable(final CommandContext<S> context, final SuggestionsBuilder builder)
    {
        var dependencies = DependencyManager.getInstance(FeatherMorphFabricMain.pluginNamespace());
        if (dependencies == null)
            return builder.buildFuture();

        var morphManager = dependencies.get(FabricMorphManager.class, false);
        if (morphManager == null)
            return builder.buildFuture();

        return CompletableFuture.supplyAsync(() ->
        {
            var input = builder.getRemainingLowerCase();

            for (var p : morphManager.listProviders())
            {
                if (p.namespace().equals("fallback")) continue;

                var providerNamespace = p.namespace();
                p.availableDisguises().forEach(path ->
                {
                    var id = providerNamespace + ":" + path;
                    if (id.toLowerCase().contains(input))
                        builder.suggest(id);
                });

                builder.suggest(providerNamespace + ":" + "@all");
            }

            return builder.build();
        });
    }
}
