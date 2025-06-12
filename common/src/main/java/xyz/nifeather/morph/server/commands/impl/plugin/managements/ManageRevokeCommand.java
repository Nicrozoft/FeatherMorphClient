package xyz.nifeather.morph.server.commands.impl.plugin.managements;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.arguments.DisguiseIdentifierSuggestions;
import xyz.nifeather.morph.server.morphs.MorphManager;
import xyz.nifeather.morph.shared.commands.arguments.RelaxedStringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class ManageRevokeCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        parentBuilder.then(
                Commands.literal("revoke")
                        .then(
                                Commands.argument("who", EntityArgument.player())
                                        .then(
                                                Commands.argument("what", RelaxedStringArgumentType.INSTANCE)
                                                        .suggests((ctx, suggestionBuilder) -> DisguiseIdentifierSuggestions.forInputPlayer(ctx, suggestionBuilder, "who"))
                                                        .executes(this::onRevokeCommand)
                                        )
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private MorphManager morphManager;

    private int onRevokeCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgument.getPlayer(context, "who");
        var what = StringArgumentType.getString(context, "what");

        morphManager.revokeDisguiseFromPlayer(who, what);

        sender.sendSystemMessage(Component.translatableWithFallback("morph.command.manage.revoke.success", "Revoke %s from %s success!", what, who.getName()));

        return 1;
    }
}
