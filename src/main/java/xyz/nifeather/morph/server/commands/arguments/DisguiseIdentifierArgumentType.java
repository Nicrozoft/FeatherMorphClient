package xyz.nifeather.morph.server.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DisguiseIdentifierArgumentType extends ServerPluginObject implements ArgumentType<String>
{
    private static final List<String> EXAMPLES = List.of("allay", "minecraft:allay", "player:Icalingua");
    public static final DisguiseIdentifierArgumentType INSTANCE = new DisguiseIdentifierArgumentType();

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

    private final List<String> cachedAvailableIDs = ObjectLists.synchronize(new ObjectArrayList<>());

    @Override
    @NotNull
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return suggestPlayerDisguises(context, builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphs;

    private <S> CompletableFuture<Suggestions> suggestPlayerDisguises(CommandContext<S> context, SuggestionsBuilder builder)
    {
        var source = context.getSource();

        PlayerEntity player = null;

        if (source instanceof ServerCommandSource serverCommandSource)
            player = serverCommandSource.getPlayer();
        else if (source instanceof ClientCommandSource)
            player = MinecraftClient.getInstance().player; // 如果我们用客户端进入世界（使用内置服务器），那么指令补全的时候这里会是 ClientCommandSource... Minecraft，很神奇吧？

        if (player == null)
            return builder.buildFuture();

        String input = builder.getRemainingLowerCase();

        var availableDisguises = morphs.getUnlockedDisguiseIds(player);

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
}
