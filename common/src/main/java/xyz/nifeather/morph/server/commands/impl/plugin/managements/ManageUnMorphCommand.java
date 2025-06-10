package xyz.nifeather.morph.server.commands.impl.plugin.managements;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.morphs.MorphManager;

public class ManageUnMorphCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        parentBuilder.then(
                Commands.literal("unmorph")
                        .then(
                                Commands.argument("who", EntityArgument.player())
                                        .executes(this::onUnMorphDisguiseCommand)
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private MorphManager morphManager;

    private int onUnMorphDisguiseCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgument.getPlayer(context, "who");

        morphManager.unMorph(who);

        sender.sendSystemMessage(Component.translatableWithFallback("morph.command.manage.unmorph.success", "Undisguised %s successfully", who.getScoreboardName()));

        return 1;
    }
}
