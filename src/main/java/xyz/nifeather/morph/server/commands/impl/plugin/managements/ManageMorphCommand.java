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

public class ManageMorphCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<ServerCommandSource, ?> parentBuilder)
    {
        parentBuilder.then(
                CommandManager.literal("morph")
                        .then(
                                CommandManager.argument("who", EntityArgumentType.player())
                                        .then(
                                                CommandManager.argument("what", RelaxedStringArgumentType.INSTANCE)
                                                        .suggests(DisguiseIdentifierSuggestions::allAvailable)
                                                        .executes(this::onMorphDisguiseCommand)
                                        )
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    private int onMorphDisguiseCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgumentType.getPlayer(context, "who");
        var what = StringArgumentType.getString(context, "what");

        if (!morphManager.morph(who, what, true))
            sender.sendMessage(Text.translatableWithFallback("morph.command.manage.morph.failed", "Failed to disguise %s as %s", who.getNameForScoreboard(), what));
        else
            sender.sendMessage(Text.translatableWithFallback("morph.command.manage.morph.success", "Successfully disguised %s as %s", who.getNameForScoreboard(), what));

        return 1;
    }
}
