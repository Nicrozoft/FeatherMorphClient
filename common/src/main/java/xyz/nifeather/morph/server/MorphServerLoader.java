package xyz.nifeather.morph.server;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.morph.server.commands.CommandRegistrationContext;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.platform.Services;

public class MorphServerLoader
{
    public static final Logger LOGGER = LoggerFactory.getLogger("FeatherMorph$MorphServerLoader");
    @Nullable
    public static MinecraftServer mcserver;
    @Nullable
    private CommandRegistrationContext registrationContext;
    @Nullable
    private FeatherMorphMain main;

    public void onModLoad()
    {
        Services.PLATFORM.registerServerStoppingEvent(this::onServerStop);
        Services.PLATFORM.registerWorldLoadEvent(this::onServerStart);
        Services.PLATFORM.registerCommandRegistrationEvent(this::onCommandRegister);
        Services.PLATFORM.registerServerStartTickEvent(Identifier.fromNamespaceAndPath("feathermorph_fabric_server", "server_tick"), this::onServerTick);
    }

    public void onCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher,
                                  CommandBuildContext registryAccess,
                                  Commands.CommandSelection environment)
    {
        LOGGER.info("Caching CommandRegistrationContext as we register commands later.");

        this.registrationContext = new CommandRegistrationContext(dispatcher, registryAccess, environment);
    }

    private void onServerStart(MinecraftServer startingServer, ServerLevel world)
    {
        if (mcserver == startingServer)
            return;

        mcserver = startingServer;

        if (!SharedValues.allowSinglePlayerDebugging)
        {
            LOGGER.error("SinglePlayer debug is disabled.");
            return;
        }

        var newInstance = new FeatherMorphMain();
        newInstance.enablePlugin();

        if (this.registrationContext != null)
            newInstance.onCommandRegister(registrationContext);

        this.registrationContext = null;
        this.main = newInstance;
    }

    private void onServerTick(MinecraftServer minecraftServer)
    {
        if (main != null)
            main.tick(minecraftServer);
    }

    private void onServerStop(MinecraftServer mcServer)
    {
        if (main != null)
            main.disablePlugin();

        main = null;
        mcserver = null;
    }
}
