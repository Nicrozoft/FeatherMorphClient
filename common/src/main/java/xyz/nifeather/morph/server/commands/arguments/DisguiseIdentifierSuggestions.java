package xyz.nifeather.morph.server.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import xiamomc.pluginbase.Managers.DependencyManager;
import xyz.nifeather.morph.server.FeatherMorphMain;
import xyz.nifeather.morph.server.morphs.MorphManager;

import java.util.concurrent.CompletableFuture;

public class DisguiseIdentifierSuggestions
{
    // Only available in fabric
    // For plugin platform we need to use `allAvailable` instead.
    public static CompletableFuture<Suggestions> forInputPlayer(final CommandContext<CommandSourceStack> context,
                                                                final SuggestionsBuilder builder,
                                                                String playerArgumentName) throws CommandSyntaxException
    {
        var dependencies = DependencyManager.getInstance(FeatherMorphMain.pluginNamespace());
        if (dependencies == null)
            return builder.buildFuture();

        var morphManager = dependencies.get(MorphManager.class, false);
        if (morphManager == null)
            return builder.buildFuture();

        var player = EntityArgument.getPlayer(context, playerArgumentName);

        var availableDisguises = morphManager.getUnlockedDisguiseIds(player);

        String input = builder.getRemainingLowerCase();

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

    public static <S> CompletableFuture<Suggestions> forPlayer(final CommandContext<S> context, final SuggestionsBuilder builder)
    {
        var dependencies = DependencyManager.getInstance(FeatherMorphMain.pluginNamespace());
        if (dependencies == null)
            return builder.buildFuture();

        var morphManager = dependencies.get(MorphManager.class, false);
        if (morphManager == null)
            return builder.buildFuture();

        var source = context.getSource();

        Player player = null;

        if (source instanceof CommandSourceStack serverCommandSource)
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
        var dependencies = DependencyManager.getInstance(FeatherMorphMain.pluginNamespace());
        if (dependencies == null)
            return builder.buildFuture();

        var morphManager = dependencies.get(MorphManager.class, false);
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