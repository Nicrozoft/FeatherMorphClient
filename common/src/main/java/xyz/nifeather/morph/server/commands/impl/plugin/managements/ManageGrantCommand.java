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

public class ManageGrantCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<CommandSourceStack, ?> parentBuilder)
    {
        parentBuilder.then(
                Commands.literal("grant")
                        .then(
                                Commands.argument("who", EntityArgument.player())
                                        .then(
                                                Commands.argument("what", RelaxedStringArgumentType.INSTANCE)
                                                        .suggests(DisguiseIdentifierSuggestions::allAvailable)
                                                        .executes(this::onGrantDisguiseCommand)
                                        )
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private MorphManager morphManager;

    private int onGrantDisguiseCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgument.getPlayer(context, "who");
        var what = StringArgumentType.getString(context, "what");

        if (what.endsWith(":@all"))
        {
            var provider = morphManager.getProvider(what);
            provider.availableDisguises().forEach(id -> morphManager.grantDisguiseToPlayer(who, provider.wrapId(id)));
        }
        else
        {
            morphManager.grantDisguiseToPlayer(who, what);
        }

        sender.sendSystemMessage(Component.translatableWithFallback("morph.command.manage.grant.success", "Grant %s to %s success!", what, who.getName()));

        return 1;
    }
}
