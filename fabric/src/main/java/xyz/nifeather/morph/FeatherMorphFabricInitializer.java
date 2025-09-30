package xyz.nifeather.morph;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import xyz.nifeather.morph.client.commands.FabricClientCommand;
import xyz.nifeather.morph.shared.commands.IBrigadierCommand;

import java.util.List;

public class FeatherMorphFabricInitializer implements ModInitializer
{
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize()
    {
        new FeatherMorphCommonBootstrap();

        List<IBrigadierCommand<FabricClientCommandSource>> clientCommands = List.of(
                new FabricClientCommand()
        );

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) ->
        {
            clientCommands.forEach(c -> c.register(dispatcher));
        }));
    }
}
