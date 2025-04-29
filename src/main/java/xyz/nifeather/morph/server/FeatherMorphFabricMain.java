package xyz.nifeather.morph.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.morph.network.Constants;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.morph.server.commands.CommandRegistrationContext;
import xyz.nifeather.morph.server.commands.FabricCommandHub;
import xyz.nifeather.morph.server.events.CommonEventProcessor;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;
import xyz.nifeather.morph.server.network.FabricClientHandler;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.payload.MorphCommandPayload;
import xyz.nifeather.morph.shared.payload.MorphInitChannelPayload;
import xyz.nifeather.morph.shared.payload.MorphVersionChannelPayload;

import java.io.File;

public class FeatherMorphFabricMain extends XiaMoJavaPlugin
{
    public static String pluginNamespace()
    {
        return "feathermorph_fabric_main";
    }

    @Override
    public String namespace()
    {
        return pluginNamespace();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("FeatherMorph$FabricServer");

    @Override
    protected Logger getSLF4JLogger()
    {
        return LOGGER;
    }

    @Nullable
    private Runnable mainLoop;

    @Override
    public void startMainLoop(Runnable r)
    {
        logger.info("START MAIN LOOP!");
        this.mainLoop = r;
    }

    @Override
    public void runAsync(Runnable r)
    {
        Util.backgroundExecutor().execute(r);
    }

    public FabricClientHandler clientHandler;
    public FabricMorphManager morphManager;

    @Override
    protected void enable()
    {
        ServerPlayNetworking.registerGlobalReceiver(MorphInitChannelPayload.id, this::onInitPayload);
        ServerPlayNetworking.registerGlobalReceiver(MorphVersionChannelPayload.id, this::onApiPayload);
        ServerPlayNetworking.registerGlobalReceiver(MorphCommandPayload.id, this::onPlayCommandPayload);

        // Global dependencies
        dependencyManager.cache(morphManager = new FabricMorphManager());
        dependencyManager.cache(clientHandler = new FabricClientHandler());

        // Events
        var events = new CommonEventProcessor();
        events.initListener();

        // Commands
        commandHub = new FabricCommandHub();
    }

    @Override
    protected void disable()
    {
        ServerPlayNetworking.unregisterGlobalReceiver(MorphInitChannelPayload.id.id());
        ServerPlayNetworking.unregisterGlobalReceiver(MorphVersionChannelPayload.id.id());
        ServerPlayNetworking.unregisterGlobalReceiver(MorphCommandPayload.id.id());

        morphManager.dispose();
    }

    @Nullable
    private File dataFolder;

    @Override
    public @NotNull File getDataFolder()
    {
        if (dataFolder == null)
        {
            ServerLevel serverWorld = MorphServerLoader.mcserver.overworld();

            var dataFile = new File(serverWorld.getServer().getWorldPath(LevelResource.ROOT).toFile(), "data");
            dataFolder = new File(dataFile, "feathermorph-fabric");
        }

        return dataFolder;
    }

    //region Command register

    private FabricCommandHub commandHub;

    public void onCommandRegister(CommandRegistrationContext context)
    {
        if (commandHub != null)
            commandHub.registerCommands(context);
        else
            LOGGER.warn("NULL commandHub?! This shouldn't happen!");
    }


    //endregion Command register

    //region Payload handle

    private void onPlayCommandPayload(MorphCommandPayload morphCommandPayload, ServerPlayNetworking.Context context)
    {
        clientHandler.onCommandPayload(morphCommandPayload, context);
    }

    private void onInitPayload(MorphInitChannelPayload packet, ServerPlayNetworking.Context context)
    {
        var player = context.player();
        LOGGER.info("On init payload! from " + player);

        var payload = new MorphInitChannelPayload(SharedValues.newProtocolIdentify);

        ServerPlayNetworking.send(player, payload);
    }

    private void onApiPayload(MorphVersionChannelPayload morphVersionChannelPayload, ServerPlayNetworking.Context context)
    {
        var player = context.player();
        LOGGER.info("%s logged in with api version %s!".formatted(player.getName(), morphVersionChannelPayload.getProtocolVersion()));

        var payload = new MorphVersionChannelPayload(Constants.PROTOCOL_VERSION);
        ServerPlayNetworking.send(player, payload);
    }

    //endregion Payload handle

    public void tick(MinecraftServer tickingServer)
    {
        if (mainLoop != null)
        {
            mainLoop.run();

            //for (ServerPlayerEntity serverPlayerEntity : tickingServer.getPlayerManager().getPlayerList())
            //    serverPlayerEntity.sendMessage(Text.literal("" + currentTick + " :: " + this.schedules.size() + " :: cancel? " + cancelSchedules), true);
        }
    }
}
