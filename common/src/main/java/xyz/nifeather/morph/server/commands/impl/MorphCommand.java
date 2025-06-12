package xyz.nifeather.morph.server.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.commands.arguments.DisguiseIdentifierSuggestions;
import xyz.nifeather.morph.shared.commands.arguments.RelaxedStringArgumentType;
import xyz.nifeather.morph.server.morphs.MorphManager;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.commands.IBrigadierCommand;

public class MorphCommand extends ServerPluginObject implements IBrigadierCommand
{
    @Resolved(shouldSolveImmediately = true)
    private MorphManager morphManager;

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
                Commands.literal("morph")
                        .then(
                                Commands.argument("id", RelaxedStringArgumentType.INSTANCE)
                                        .suggests(DisguiseIdentifierSuggestions::forPlayer)
                                        .executes(ctx ->
                                        {
                                            if (!ctx.getSource().isPlayer())
                                            {
                                                ctx.getSource().sendFailure(Component.literal("You must be a player to use this command"));
                                                return 0;
                                            }

                                            var executor = ctx.getSource().getPlayerOrException();

                                            String id = StringArgumentType.getString(ctx, "id");

                                            morphManager.morph(executor, id);

                                            return 1;
                                        })
                        ));
    }
}
