package xyz.nifeather.morph.server.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.IServerBrigadierCommand;
import xyz.nifeather.morph.server.morphs.MorphManager;
import xyz.nifeather.morph.server.ServerPluginObject;

public class UnMorphCommand extends ServerPluginObject implements IServerBrigadierCommand
{
    @Resolved(shouldSolveImmediately = true)
    private MorphManager morphManager;

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("unmorph")
                        .executes(ctx ->
                        {
                            if (!ctx.getSource().isPlayer())
                            {
                                ctx.getSource().sendFailure(Component.literal("You must be a player to use this command"));
                                return 0;
                            }

                            var executor = ctx.getSource().getPlayerOrException();

                            morphManager.unMorph(executor);

                            return 1;
                        })
        );
    }
}
