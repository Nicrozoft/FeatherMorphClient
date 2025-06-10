package xyz.nifeather.morph.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.morph.network.Constants;
import xyz.nifeather.morph.network.commands.S2C.InitializeRespondV3;
import xyz.nifeather.morph.server.commands.CommandHub;
import xyz.nifeather.morph.server.commands.CommandRegistrationContext;
import xyz.nifeather.morph.server.events.CommonEventProcessor;
import xyz.nifeather.morph.server.morphs.MorphManager;
import xyz.nifeather.morph.server.network.ClientHandler;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.payload.V3MorphCommandPayload;
import xyz.nifeather.morph.shared.payload.V3MorphInitChannelPayload;

import java.io.File;
import java.util.List;

public class FeatherMorphMain extends XiaMoJavaPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("FeatherMorph$FabricServer");
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    public ClientHandler clientHandler;
    public MorphManager morphManager;
    @Nullable
    private Runnable mainLoop;
    @Nullable
    private File dataFolder;
    private CommandHub commandHub;

    public static String pluginNamespace() {
        return "feathermorph_fabric_main";
    }

    @Override
    public String namespace() {
        return pluginNamespace();
    }

    @Override
    protected Logger getSLF4JLogger() {
        return LOGGER;
    }

    @Override
    public void startMainLoop(Runnable r) {
        logger.info("START MAIN LOOP!");
        this.mainLoop = r;
    }

    @Override
    public void runAsync(Runnable r) {
        Util.backgroundExecutor().execute(r);
    }

    @Override
    protected void enable() {
        ServerPlayNetworking.registerGlobalReceiver(V3MorphInitChannelPayload.id, this::onInitPayload);
        ServerPlayNetworking.registerGlobalReceiver(V3MorphCommandPayload.id, this::onPlayCommandPayload);

        // Global dependencies
        dependencyManager.cache(morphManager = new MorphManager());
        dependencyManager.cache(clientHandler = new ClientHandler());

        // Events
        var events = new CommonEventProcessor();
        events.initListener();

        // Commands
        commandHub = new CommandHub();
    }

    //region Command register

    @Override
    protected void disable() {
        ServerPlayNetworking.unregisterGlobalReceiver(V3MorphInitChannelPayload.id.id());
        ServerPlayNetworking.unregisterGlobalReceiver(V3MorphCommandPayload.id.id());

        morphManager.dispose();
    }

    @Override
    public @NotNull File getDataFolder() {
        if (dataFolder == null) {
            ServerLevel serverWorld = MorphServerLoader.mcserver.overworld();

            var dataFile = new File(serverWorld.getServer().getWorldPath(LevelResource.ROOT).toFile(), "data");
            dataFolder = new File(dataFile, "feathermorph-fabric");
        }

        return dataFolder;
    }


    //endregion Command register

    //region Payload handle

    public void onCommandRegister(CommandRegistrationContext context) {
        if (commandHub != null)
            commandHub.registerCommands(context);
        else
            LOGGER.warn("NULL commandHub?! This shouldn't happen!");
    }

    private void onPlayCommandPayload(V3MorphCommandPayload morphCommandPayload, ServerPlayNetworking.Context context) {
        clientHandler.onCommandPayload(morphCommandPayload, context);
    }

    private void onInitPayload(V3MorphInitChannelPayload packet, ServerPlayNetworking.Context context) {
        var player = context.player();
        LOGGER.info("On init payload! from " + player);

        var respond = new InitializeRespondV3(List.of(SharedValues.newProtocolIdentify), Constants.PROTOCOL_VERSION);
        var payload = new V3MorphInitChannelPayload(gson.toJson(respond));

        ServerPlayNetworking.send(player, payload);
    }

    //endregion Payload handle

    public void tick(MinecraftServer tickingServer) {
        if (mainLoop != null) {
            mainLoop.run();

            //for (ServerPlayerEntity serverPlayerEntity : tickingServer.getPlayerManager().getPlayerList())
            //    serverPlayerEntity.sendMessage(Text.literal("" + currentTick + " :: " + this.schedules.size() + " :: cancel? " + cancelSchedules), true);
        }
    }
}
