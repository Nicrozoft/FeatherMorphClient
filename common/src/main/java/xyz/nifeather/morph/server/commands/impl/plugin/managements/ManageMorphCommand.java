package xyz.nifeather.morph.server.commands.impl.plugin.managements;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.arguments.DisguiseIdentifierSuggestions;
import xyz.nifeather.morph.server.morphs.MorphManager;
import xyz.nifeather.morph.shared.commands.arguments.RelaxedStringArgumentType;

public class ManageMorphCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        parentBuilder.then(
                Commands.literal("morph")
                        .then(
                                Commands.argument("who", EntityArgument.player())
                                        .then(
                                                Commands.argument("what", RelaxedStringArgumentType.INSTANCE)
                                                        .suggests(DisguiseIdentifierSuggestions::allAvailable)
                                                        .executes(this::onMorphDisguiseCommand)
                                        )
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private MorphManager morphManager;

    private int onMorphDisguiseCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgument.getPlayer(context, "who");
        var what = StringArgumentType.getString(context, "what");

        if (!morphManager.morph(who, what, true))
            sender.sendSystemMessage(Component.translatableWithFallback("morph.command.manage.morph.failed", "Failed to disguise %s as %s", who.getScoreboardName(), what));
        else
            sender.sendSystemMessage(Component.translatableWithFallback("morph.command.manage.morph.success", "Successfully disguised %s as %s", who.getScoreboardName(), what));

        return 1;
    }
}
