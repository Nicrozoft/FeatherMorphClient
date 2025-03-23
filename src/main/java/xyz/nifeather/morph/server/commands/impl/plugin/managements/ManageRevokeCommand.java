package xyz.nifeather.morph.server.commands.impl.plugin.managements;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.arguments.DisguiseIdentifierSuggestions;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;
import xyz.nifeather.morph.shared.commands.arguments.RelaxedStringArgumentType;

import java.util.concurrent.CompletableFuture;

public class ManageRevokeCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<ServerCommandSource, ?> parentBuilder)
    {
        parentBuilder.then(
                CommandManager.literal("revoke")
                        .then(
                                CommandManager.argument("who", EntityArgumentType.player())
                                        .then(
                                                CommandManager.argument("what", RelaxedStringArgumentType.INSTANCE)
                                                        .suggests((ctx, suggestionBuilder) -> DisguiseIdentifierSuggestions.forInputPlayer(ctx, suggestionBuilder, "who"))
                                                        .executes(this::onRevokeCommand)
                                        )
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    private int onRevokeCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgumentType.getPlayer(context, "who");
        var what = StringArgumentType.getString(context, "what");

        morphManager.revokeDisguiseFromPlayer(who, what);

        sender.sendMessage(Text.translatableWithFallback("morph.command.manage.revoke.success", "Revoke %s from %s success!", what, who.getName()));

        return 1;
    }
}
