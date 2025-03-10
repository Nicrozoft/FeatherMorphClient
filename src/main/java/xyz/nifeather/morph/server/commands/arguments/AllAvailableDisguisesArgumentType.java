package xyz.nifeather.morph.server.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AllAvailableDisguisesArgumentType extends ServerPluginObject implements ArgumentType<String>
{
    private static final List<String> EXAMPLES = List.of("allay", "minecraft:allay", "player:Icalingua");
    public static final AllAvailableDisguisesArgumentType INSTANCE = new AllAvailableDisguisesArgumentType();

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    @Override
    @NotNull
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        int begin = reader.getCursor();

        if (!reader.canRead())
            reader.skip();

        while (reader.canRead() && !Character.isWhitespace(reader.peek()))
            reader.skip();

        return reader.getString().substring(begin, reader.getCursor());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return ArgumentType.super.listSuggestions(context, builder);
    }

    private final List<String> cachedAvailableIDs = ObjectLists.synchronize(new ObjectArrayList<>());

    private <S> CompletableFuture<Suggestions> suggestAllDisguises(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            var input = builder.getRemainingLowerCase();

            if (!cachedAvailableIDs.isEmpty())
            {
                cachedAvailableIDs.forEach(id ->
                {
                    if (!id.toLowerCase().contains(input))
                        return;

                    builder.suggest(id);
                });

                return builder.build();
            }

            for (var p : morphManager.listProviders())
            {
                if (p.namespace().equals("fallback")) continue;

                var providerNamespace = p.namespace();
                p.availableDisguises().forEach(path ->
                {
                    var id = providerNamespace + ":" + path;
                    if (id.toLowerCase().contains(input))
                        builder.suggest(id);

                    cachedAvailableIDs.add(id);
                });

                builder.suggest(providerNamespace + ":" + "@all");
                cachedAvailableIDs.add(providerNamespace + ":" + "@all");
            }

            return builder.build();
        });
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }
}
