package xyz.nifeather.morph.server.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.commands.IBrigadierCommand;

public class UnMorphCommand extends ServerPluginObject implements IBrigadierCommand
{
    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(
                CommandManager.literal("unmorph")
                        .executes(ctx ->
                        {
                            if (!ctx.getSource().isExecutedByPlayer())
                            {
                                ctx.getSource().sendError(Text.literal("You must be a player to use this command"));
                                return 0;
                            }

                            var executor = ctx.getSource().getPlayerOrThrow();

                            morphManager.unMorph(executor);

                            return 1;
                        })
        );
    }
}
