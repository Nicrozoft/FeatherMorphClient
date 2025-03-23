package xyz.nifeather.morph.server.commands.impl.plugin.managements;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.commands.arguments.DisguiseIdentifierSuggestions;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;
import xyz.nifeather.morph.shared.commands.arguments.RelaxedStringArgumentType;

public class ManageGrantCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<ServerCommandSource, ?> parentBuilder)
    {
        parentBuilder.then(
                CommandManager.literal("grant")
                        .then(
                                CommandManager.argument("who", EntityArgumentType.player())
                                        .then(
                                                CommandManager.argument("what", RelaxedStringArgumentType.INSTANCE)
                                                        .suggests(DisguiseIdentifierSuggestions::allAvailable)
                                                        .executes(this::onGrantDisguiseCommand)
                                        )
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    private int onGrantDisguiseCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgumentType.getPlayer(context, "who");
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

        sender.sendMessage(Text.translatableWithFallback("morph.command.manage.grant.success", "Grant %s to %s success!", what, who.getName()));

        return 1;
    }
}
