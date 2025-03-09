package xyz.nifeather.morph.server.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.commands.IBrigadierCommand;

public class MorphCommand extends ServerPluginObject implements IBrigadierCommand
{
    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        dispatcher.register(
                CommandManager.literal("morph")
                        .then(
                                CommandManager.argument("id", StringArgumentType.greedyString())
                                        .executes(ctx ->
                                        {
                                            if (!ctx.getSource().isExecutedByPlayer())
                                            {
                                                ctx.getSource().sendError(Text.literal("You must be a player to use this command"));
                                                return 0;
                                            }

                                            var executor = ctx.getSource().getPlayerOrThrow();

                                            String id = StringArgumentType.getString(ctx, "id");

                                            morphManager.morph(executor, id);

                                            return 1;
                                        })
                        ));
    }
}
