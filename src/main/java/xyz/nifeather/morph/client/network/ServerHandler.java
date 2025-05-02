package xyz.nifeather.morph.client.network;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.nifeather.morph.client.*;
import xyz.nifeather.morph.client.config.ModConfigData;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.network.commands.ClientSetEquipCommand;
import xyz.nifeather.morph.client.network.handlers.IProtocolHandler;
import xyz.nifeather.morph.client.network.handlers.V3ProtocolHandler;
import xyz.nifeather.morph.client.utilties.NbtUtils;
import xyz.nifeather.morph.network.commands.C2S.*;
import xyz.nifeather.morph.network.commands.CommandRegistriesNew;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CAddAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CClearAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CRemoveAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CSyncAdminRevealCommand;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.payload.*;
import xyz.nifeather.morph.client.utilties.NbtHelperCopy;
import xyz.nifeather.morph.network.BasicServerHandler;
import xyz.nifeather.morph.network.Constants;
import xyz.nifeather.morph.network.commands.S2C.*;
import xyz.nifeather.morph.network.commands.S2C.clientrender.*;
import xyz.nifeather.morph.network.commands.S2C.query.S2CQueryCommand;
import xyz.nifeather.morph.network.commands.S2C.set.*;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler extends MorphClientObject implements BasicServerHandler<Player>
{
    private final FeatherMorphClient client;

    private final CommandRegistriesNew registries = new CommandRegistriesNew();

    private final LegacyServerHandler legacyServerHandler = new LegacyServerHandler(this);

    public ServerHandler(FeatherMorphClient client)
    {
        this.client = client;

        // Misc Commands
        registries.registerS2C(S2CCommandNames.SetCurrent, S2CSetCurrentCommand::fromArguments)
                .registerS2C(S2CCommandNames.ReAuth, S2CReAuthCommand::fromArguments)
                .registerS2C(S2CCommandNames.UnAuth, S2CUnAuthCommand::fromArguments)
                .registerS2C(S2CCommandNames.SwapHands, S2CSwapCommand::fromArguments)
                .registerS2C(S2CCommandNames.Query, S2CQueryCommand::fromArguments);

        // Exchange Request
        registries.registerS2C(S2CCommandNames.UpdateRequestStatus, S2CUpdateRequestStatusCommand::fromArguments);

        // Some Set Commands
        registries.registerS2C(S2CCommandNames.SetAggressive, S2CSetAggressiveCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetFakeEquip, ClientSetEquipCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetDisplayingFakeEquip, S2CSetDisplayingFakeEquipCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSkinProfile, S2CSetProfileCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSelfViewIdentifier, S2CSetSelfViewIdentifierCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSkillCooldown, S2CSetSkillCooldownCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSNbt, S2CSetSNbtCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSneaking, S2CSetSneakingCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSelfViewing, S2CSetSelfViewingStatusCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetModifyBoundingBox, S2CSetModifyBoundingBoxCommand::fromArguments);

        // Mob Reveal
        registries.registerS2C(S2CCommandNames.SetMobReveal, S2CSetMobRevealCommand::fromArguments);

        // Animations
        registries.registerS2C(S2CCommandNames.PlayAnimation, S2CPlayAnimationCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetAnimationDisplayName, S2CSetAnimationDisplayNameCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetAvailableAnimations, S2CSetAvailableAnimationsCommand::fromArguments);

        // Admin Reveal
        registries.registerS2C(S2CCommandNames.AdminRevealSync, S2CSyncAdminRevealCommand::fromArguments)
                .registerS2C(S2CCommandNames.AdminRevealAdd, S2CAddAdminRevealCommand::fromArguments)
                .registerS2C(S2CCommandNames.AdminRevealClear, S2CClearAdminRevealCommand::fromArguments)
                .registerS2C(S2CCommandNames.AdminRevealRemove, S2CRemoveAdminRevealCommand::fromArguments);

        // Client Renderer
        registries.registerS2C(S2CCommandNames.CRAdd, S2CCRRegisterCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRClear, S2CCRClearCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRSyncRender, S2CCRSyncRegisterCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRRemove, S2CCRUnregisterCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRMeta, S2CCRSetMetaCommand::fromArguments);
    }

    @Resolved
    private ClientMorphManager morphManager;

    @Resolved
    private DisguiseInstanceTracker instanceTracker;

    @Resolved
    private ModConfigData config;

    @Resolved
    private ClientSkillHandler skillHandler;

    //region Network

    private IProtocolHandler protocolHandler = V3ProtocolHandler.INSTANCE;

    public void setProtocolHandler(IProtocolHandler newHandler)
    {
        logger.info("ProtocolHandler set to " + newHandler.getClass().getSimpleName());
        this.protocolHandler = newHandler;
    }

    public IProtocolHandler protocolHandler()
    {
        return protocolHandler;
    }

    public boolean serverReady()
    {
        return serverReady.get();
    }

    private int serverVersion = -1;

    public int getServerVersion()
    {
        return serverVersion;
    }

    public boolean serverApiMatch()
    {
        return this.getServerVersion() == getImplmentingApiVersion();
    }

    public boolean sendCommand(AbstractC2SCommand<?> command)
    {
        try
        {
            protocolHandler.sendCommand(command);
        }
        catch (Throwable t)
        {
            logger.error("Failed to send command: " + t.getMessage());
        }

        return true;
    }

    private void tryProtocols()
    {
        var initRecord = new ClientInitializeRecordV3(List.of(SharedValues.newProtocolIdentify), getImplmentingApiVersion(), false);
        V3ProtocolHandler.INSTANCE.sendInitializeRequest(initRecord);

        this.addSchedule(() ->
        {
            if (serverReady.get()) return;
            legacyServerHandler.sendInitializeV2(List.of(SharedValues.newProtocolIdentify), getImplmentingApiVersion());
        }, 20);
    }

    @Override
    public void connect()
    {
        this.resetServerStatus();

        setProtocolHandler(V3ProtocolHandler.INSTANCE);
        tryProtocols();
    }

    @Override
    public void disconnect()
    {
        resetServerStatus();
    }

    @Override
    public int getServerApiVersion()
    {
        return serverVersion;
    }

    @Override
    public int getImplmentingApiVersion()
    {
        return Constants.PROTOCOL_VERSION;
    }

    public final Bindable<Boolean> serverReady = new Bindable<>(false);
    private boolean handshakeReceived;

    public void resetServerStatus()
    {
        handshakeReceived = false;

        morphManager.reset();
        updateServerStatus();
        instanceTracker.reset();
    }

    public void testSetServerReady()
    {
        serverReady.set(true);
    }

    private void updateServerStatus()
    {
        serverReady.set(handshakeReceived);
        displaySetToast.set(false);
    }

    private boolean networkInitialized;

    public void initializeNetwork()
    {
        if (networkInitialized)
            throw new RuntimeException("The network has been initialized once!");

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> connect());

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> disconnect());

        PayloadTypeRegistry.playC2S().register(V3MorphInitChannelPayload.id, V3MorphInitChannelPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(V3MorphCommandPayload.id, V3MorphCommandPayload.CODEC);

        //初始化网络
        ClientPlayNetworking.registerGlobalReceiver(V3MorphInitChannelPayload.id, (payload, context) ->
        {
            logPacket(false, SharedValues.initializeChannelV3, payload.message());

            logger.info("Server is using V3 packets");
            var respond = protocolHandler.handleInitializeRespond(payload);
            this.handleServerInitRespond(respond);
        });

        ClientPlayNetworking.registerGlobalReceiver(V3MorphCommandPayload.id, (payload, context) ->
        {
            logPacket(false, SharedValues.commandChannelV3, payload.content());

            var result = protocolHandler.handleCommandInput(payload);
            if (!result.success())
                return;

            this.handleCommand(result.result());
        });

        networkInitialized = true;
    }

    public void handleServerInitRespond(InitializeRespondV3 respond)
    {
        if (serverReady.get())
        {
            logger.warn("Received init respond while the server is ready?!");
            Thread.dumpStack();
            return;
        }

        serverVersion = respond.apiVersion();
        logger.info("Server is using command API V" + serverVersion);

        serverReady.set(true);

        handshakeReceived = true;
        updateServerStatus();

        sendCommand(new C2SRequestInitialCommand());
        sendCommand(new C2SSetSingleOptionCommand(C2SSetSingleOptionCommand.ClientOptionEnum.CLIENTVIEW, config.allowClientView));
        sendCommand(new C2SSetSingleOptionCommand(C2SSetSingleOptionCommand.ClientOptionEnum.HUD, config.displayDisguiseOnHud));
    }

    public void handleCommand(S2CCommandRecord commandRecord)
    {
        try
        {
            //if (config.verbosePackets)
            //    logger.info("Received client command: " + input);

            if (!serverReady.get() && !commandRecord.commandName().equals("reauth"))
            {
                if (config.verbosePackets)
                    logger.warn("Received command before initialize complete, not processing... ('%s')".formatted(commandRecord.commandName()));

                return;
            }

            var baseName = commandRecord.commandName();
            var arguments = commandRecord.arguments();
            var cmd = registries.createS2CCommand(baseName, arguments);

            if (RenderSystem.isOnRenderThread())
                cmd.onCommand(this);
            else
                FeatherMorphClient.getInstance().schedule(() -> cmd.onCommand(this));
        }
        catch (Exception e)
        {
            logger.error("Error handling server command：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Boolean serverSideSneaking;

    public static void logPacket(boolean isOutGoingPacket, ResourceLocation channel, String content)
    {
        if (!FeatherMorphClient.getInstance().getModConfigData().verbosePackets) return;

        var arrow = isOutGoingPacket ? " -> " : " <- ";

        String builder = channel.toString() + arrow
                + "SERVER"
                + " :: "
                + "'%s'".formatted(content);

        FeatherMorphClient.LOGGER.info(builder);
    }

    //endregion Network

    //region Impl of Serverhandler

    @Override
    public void onCurrentCommand(S2CSetCurrentCommand s2CCurrentCommand)
    {
        var id = s2CCurrentCommand.getDisguiseIdentifier();
        morphManager.setCurrent(id);
    }

    @Override
    public void onReAuthCommand(S2CReAuthCommand s2CReAuthCommand)
    {
        this.disconnect();
        this.connect();
    }

    @Override
    public void onUnAuthCommand(S2CUnAuthCommand s2CUnAuthCommand)
    {
        this.disconnect();
    }

    @Override
    public void onSwapCommand(S2CSwapCommand s2CSwapCommand)
    {
        morphManager.swapHand();
    }

    private final AtomicBoolean displaySetToast = new AtomicBoolean();

    @Override
    public void onQueryCommand(S2CQueryCommand s2CQueryCommand)
    {
        var diff = s2CQueryCommand.getDiff();
        var modConfig = FeatherMorphClient.getInstance().getModConfigData();
        switch (s2CQueryCommand.queryType())
        {
            case ADD -> morphManager.addDisguises(diff, modConfig.displayGrantRevokeToast);
            case REMOVE -> morphManager.removeDisguises(diff, modConfig.displayGrantRevokeToast);
            case SET ->
            {
                morphManager.setDisguises(diff, displaySetToast.get() && modConfig.displayQuerySetToast);
                displaySetToast.set(true);
            }
        }
    }

    @Override
    public void onSetAggressiveCommand(S2CSetAggressiveCommand s2CSetAggressiveCommand)
    {
        var aggressive = s2CSetAggressiveCommand.val;

        var syncer = instanceTracker.getSyncerFor(Minecraft.getInstance().player);

        if (syncer != null)
        {
            var instance = syncer.getDisguiseInstance();

            if (instance instanceof Ghast ghast)
                ghast.setCharging(aggressive);
            else if (instance instanceof Warden warden && aggressive)
                warden.handleEntityEvent(EntityEvent.SONIC_CHARGE);
        }
    }

    @Override
    public void onSetFakeEquipCommand(S2CSetFakeEquipCommand<?> s2CSetEquipCommand)
    {
        if (!(s2CSetEquipCommand.getItemStack() instanceof ItemStack stack)) return;

        switch (s2CSetEquipCommand.getSlot())
        {
            case MAINHAND -> morphManager.setEquip(EquipmentSlot.MAINHAND, stack);
            case OFF_HAND -> morphManager.setEquip(EquipmentSlot.OFFHAND, stack);

            case HELMET -> morphManager.setEquip(EquipmentSlot.HEAD, stack);
            case CHESTPLATE -> morphManager.setEquip(EquipmentSlot.CHEST, stack);
            case LEGGINGS -> morphManager.setEquip(EquipmentSlot.LEGS, stack);
            case BOOTS -> morphManager.setEquip(EquipmentSlot.FEET, stack);
        }
    }

    @Override
    public void onSetDisplayingFakeEquipCommand(S2CSetDisplayingFakeEquipCommand s2CSetFakeEquipCommand)
    {
        morphManager.equipOverriden.set(s2CSetFakeEquipCommand.displaying);
    }

    @Override
    public void onSetSNbtCommand(S2CSetSNbtCommand s2CSetSNbtCommand)
    {
        var nbt = NbtUtils.parseSNbt(s2CSetSNbtCommand.getSNbt());
        if (nbt == null)
            nbt = new CompoundTag();

        morphManager.currentNbtCompound.set(nbt);
    }

    @Override
    public void onSetProfileCommand(S2CSetProfileCommand s2CSetProfileCommand)
    {
        try
        {
            var nbt = NbtUtils.parseOrThrow(s2CSetProfileCommand.getProfileSNbt());

            var profile = NbtHelperCopy.toGameProfile(nbt);

            if (profile != null)
                this.client.schedule(() -> morphManager.updateSkin(profile));
        }
        catch (Throwable t)
        {
            logger.warn("Failed processing S2CSetProfileCommand: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public void onSetSelfViewIdentifierCommand(S2CSetSelfViewIdentifierCommand s2CSetSelfViewCommand)
    {
        //morphManager.selfViewIdentifier.set(s2CSetSelfViewCommand.serializeArguments());
    }

    @Override
    public void onSetSkillCooldownCommand(S2CSetSkillCooldownCommand s2CSetSkillCooldownCommand)
    {
        skillHandler.setSkillCooldown(s2CSetSkillCooldownCommand.val);
    }

    @Override
    public void onSetSneakingCommand(S2CSetSneakingCommand s2CSetSneakingCommand)
    {
        serverSideSneaking = s2CSetSneakingCommand.sneaking;
    }

    @Override
    public void onSetSelfViewingCommand(S2CSetSelfViewingStatusCommand s2CSetToggleSelfCommand)
    {
        var enabled = s2CSetToggleSelfCommand.selfViewing();

        morphManager.selfVisibleEnabled.set(enabled);

        var iEntity = (IMorphClientEntity) Minecraft.getInstance().player;
        iEntity.featherMorph$requestBypassDispatcherRedirect(this, !enabled);
    }

    @Override
    public void onSetModifyBoundingBox(S2CSetModifyBoundingBoxCommand s2CSetModifyBoundingBoxCommand)
    {
        modifyBoundingBox = s2CSetModifyBoundingBoxCommand.getModifyBoundingBox();

        var clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null)
            clientPlayer.refreshDimensions();
    }

    public static boolean modifyBoundingBox = false;

    @Override
    public void onSetRevealing(S2CSetMobRevealCommand command)
    {
        morphManager.revealingValue.set(command.getValue());
    }

    @Resolved
    private ClientRequestManager requestManager;

    @Override
    public void onExchangeRequestReceive(S2CUpdateRequestStatusCommand s2CRequestCommand)
    {
        if (s2CRequestCommand.type == S2CUpdateRequestStatusCommand.Type.Unknown)
            logger.warn("Received an invalid exchange request");

        requestManager.addRequest(s2CRequestCommand.type, s2CRequestCommand.sourcePlayer);
    }

    @Override
    public void onMapCommand(S2CSyncAdminRevealCommand s2CMapCommand)
    {
        var map = s2CMapCommand.getMap();

        instanceTracker.playerMap.clear();
        instanceTracker.playerMap.putAll(map);
    }

    @Override
    public void onMapPartialCommand(S2CAddAdminRevealCommand s2CPartialMapCommand)
    {
        instanceTracker.playerMap.putAll(s2CPartialMapCommand.getMap());
    }

    @Override
    public void onMapClearCommand(S2CClearAdminRevealCommand s2CMapClearCommand)
    {
        instanceTracker.playerMap.clear();
    }

    @Override
    public void onMapRemoveCommand(S2CRemoveAdminRevealCommand s2CMapRemoveCommand)
    {
        var id = s2CMapRemoveCommand.getTargetId();
        instanceTracker.playerMap.remove(id);
    }

    @Override
    public void onClientMapSyncCommand(S2CCRSyncRegisterCommand s2CRenderMapSyncCommand)
    {
        instanceTracker.onSyncCommand(s2CRenderMapSyncCommand);
    }

    @Override
    public void onClientMapAddCommand(S2CCRRegisterCommand s2CRenderMapAddCommand)
    {
        instanceTracker.onAddCommand(s2CRenderMapAddCommand);
    }

    @Override
    public void onClientMapRemoveCommand(S2CCRUnregisterCommand s2CRenderMapRemoveCommand)
    {
        instanceTracker.onRemoveCommand(s2CRenderMapRemoveCommand);
    }

    @Override
    public void onClientMapClearCommand(S2CCRClearCommand s2CRenderMapClearCommand)
    {
        instanceTracker.onClearCommand(s2CRenderMapClearCommand);
    }

    @Override
    public void onClientMapMetaNbtCommand(S2CCRSetMetaCommand s2CRenderMapMetaCommand)
    {
        instanceTracker.onMetaCommand(s2CRenderMapMetaCommand);
    }

    @Override
    public void onAnimationCommand(S2CPlayAnimationCommand command)
    {
        //logger.info("Update animation : " + command.getArgumentAt(0, "???"));
        morphManager.playEmote(command.getAnimId());
    }

    @Override
    public void onValidAnimationsCommand(S2CSetAvailableAnimationsCommand command)
    {
        //logger.info("Received:");
        var cmdList = new ObjectArrayList<String>(command.getAvailableAnimations());
        cmdList.removeIf(String::isBlank);
        //cmdList.forEach(s -> logger.info("|- " + s));
        morphManager.setEmotes(cmdList);
        //logger.info("End.");
    }

    @Override
    public void onSetAnimationDisplayCommand(S2CSetAnimationDisplayNameCommand command)
    {
        morphManager.setEmoteDisplay(command.getDisplayIdentifier());
    }

    //endregion Impl of ServerHandler
}
