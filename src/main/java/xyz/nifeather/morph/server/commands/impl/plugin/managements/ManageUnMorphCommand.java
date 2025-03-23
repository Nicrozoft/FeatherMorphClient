package xyz.nifeather.morph.server.commands.impl.plugin.managements;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.BrigadierCommand;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;

public class ManageUnMorphCommand extends BrigadierCommand
{
    @Override
    public void registerAsChild(ArgumentBuilder<ServerCommandSource, ?> parentBuilder)
    {
        parentBuilder.then(
                CommandManager.literal("unmorph")
                        .then(
                                CommandManager.argument("who", EntityArgumentType.player())
                                        .executes(this::onUnMorphDisguiseCommand)
                        )
        );
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    private int onUnMorphDisguiseCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        var sender = context.getSource();

        var who = EntityArgumentType.getPlayer(context, "who");

        morphManager.unMorph(who);

        sender.sendMessage(Text.translatableWithFallback("morph.command.manage.unmorph.success", "Undisguised %s successfully", who.getNameForScoreboard()));

        return 1;
    }
}
