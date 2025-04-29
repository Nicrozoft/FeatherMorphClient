package xyz.nifeather.morph.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.llamalad7.mixinextras.sugar.Share;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.nifeather.morph.client.config.ModConfigData;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.network.commands.ClientSetEquipCommand;
import xyz.nifeather.morph.client.utilties.NbtUtils;
import xyz.nifeather.morph.network.commands.C2S.*;
import xyz.nifeather.morph.network.commands.CommandRegistriesNew;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CClearRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CPartialRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CRemoveRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CSetRenderRevealCommand;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.payload.*;
import xyz.nifeather.morph.client.utilties.NbtHelperCopy;
import xyz.nifeather.morph.network.BasicServerHandler;
import xyz.nifeather.morph.network.Constants;
import xyz.nifeather.morph.network.commands.CommandRegistries;
import xyz.nifeather.morph.network.commands.S2C.*;
import xyz.nifeather.morph.network.commands.S2C.clientrender.*;
import xyz.nifeather.morph.network.commands.S2C.query.S2CQueryCommand;
import xyz.nifeather.morph.network.commands.S2C.set.*;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;
import xiamomc.pluginbase.Exceptions.NullDependencyException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler extends MorphClientObject implements BasicServerHandler<Player>
{
    private final FeatherMorphClient client;

    private final CommandRegistriesNew registries = new CommandRegistriesNew();

    public ServerHandler(FeatherMorphClient client)
    {
        this.client = client;
    }

    @Initializer
    private void load()
    {
        registries.registerS2C(S2CCommandNames.Current, S2CCurrentCommand::fromArguments)
                .registerS2C(S2CCommandNames.Query, S2CQueryCommand::fromArguments)
                .registerS2C(S2CCommandNames.ReAuth, S2CReAuthCommand::fromArguments)
                .registerS2C(S2CCommandNames.UnAuth, S2CUnAuthCommand::fromArguments)
                .registerS2C(S2CCommandNames.SwapHands, S2CSwapCommand::fromArguments)
                .registerS2C(S2CCommandNames.Request, S2CRequestCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetReveal, S2CSetRenderRevealCommand::fromArguments)
                .registerS2C(S2CCommandNames.AddReveal, S2CPartialRevealCommand::fromArguments)
                .registerS2C(S2CCommandNames.ClearReveal, S2CClearRevealCommand::fromArguments)
                .registerS2C(S2CCommandNames.RemoveReveal, S2CRemoveRevealCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRAdd, S2CRenderMapAddCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRClear, S2CRenderMapClearCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRMap, S2CRenderMapSyncCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRRemove, S2CRenderMapRemoveCommand::fromArguments)
                .registerS2C(S2CCommandNames.CRMeta, S2CRenderMapMetaCommand::fromArguments)
                .registerS2C(S2CCommandNames.SwapHands, S2CSwapCommand::fromArguments)
                .registerS2C("animation", S2CAnimationCommand::fromArguments);

        registries.registerS2C(S2CCommandNames.SetSelfViewing, S2CSetSelfViewingCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetModifyBoundingBox, S2CSetModifyBoundingBoxCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetAvailableAnimations, S2CSetAvailableAnimationsCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetDisplayingFakeEquip, S2CSetDisplayingFakeEquipCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSNbt, S2CSetSNbtCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSkillCooldown, S2CSetSkillCooldownCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetSelfViewIdentifier, S2CSetSelfViewIdentifierCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetProfile, S2CSetProfileCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetAggressive, S2CSetAggressiveCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetFakeEquip, ClientSetEquipCommand::fromArguments)
                .registerS2C(S2CCommandNames.SetRevealing, S2CSetMobRevealingCommand::fromArguments);
    }

    //region Common

    @Resolved
    private ClientMorphManager morphManager;

    @Resolved
    private DisguiseInstanceTracker instanceTracker;

    @Resolved
    private ModConfigData config;

    @Resolved
    private ClientSkillHandler skillHandler;

    @Resolved
    private DisguiseInstanceTracker tracker;

    //endregion

    //region Network

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

    private final Map<ResourceLocation, Function<String, CustomPacketPayload>> payloadMap = new Object2ObjectArrayMap<>();

    private void initializePayloadMap()
    {
        logger.info("Registering payload types...");

        payloadMap.put(SharedValues.initializeChannelIdentifier, raw -> new MorphInitChannelPayload(raw.toString()));
        payloadMap.put(SharedValues.commandChannelIdentifier, raw -> new MorphCommandPayload(raw.toString()));
        //payloadMap.put(SharedValues.versionChannelIdentifier, raw -> new MorphVersionChannelPayload(MorphVersionChannelPayload.parseInt(raw.toString())));

        payloadMap.put(SharedValues.commandChannelIdentifierLegacy, raw -> new LegacyMorphCommandPayload(raw.toString()));
        payloadMap.put(SharedValues.versionChannelIdentifierLegacy, raw -> new LegacyMorphVersionChannelPayload(LegacyMorphVersionChannelPayload.parseInt(raw.toString())));

        logger.info("Done.");
    }

    private int objectToInteger(Object obj)
    {
        try
        {
            return Integer.parseInt(obj.toString());
        }
        catch (Throwable t)
        {
            logger.warn("Error occurred parsing server protocol version: " + t.getMessage());
            t.printStackTrace();

            return 1;
        }
    }

    public void sendCommand(ResourceLocation channel, String cmd)
    {
        var func = payloadMap.getOrDefault(channel, null);
        if (func == null)
            throw new NullDependencyException("Null func for channel " + channel + "?!");

        var payload = func.apply(cmd);

        ClientPlayNetworking.send(payload);
    }

    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public boolean sendCommand(AbstractC2SCommand<?> command)
    {
        var record = C2SCommandRecord.fromC2SCommand(command);
        var cmd = gson.toJson(record);

        if (!usingLegacyPackets)
            sendCommand(SharedValues.commandChannelIdentifier, cmd);
        else
            sendCommand(SharedValues.commandChannelIdentifierLegacy, cmd);

        return true;
    }

    @Override
    public void connect()
    {
        this.resetServerStatus();

        var command = new ClientInitializeRecordV3(List.of(SharedValues.newProtocolIdentify), getImplmentingApiVersion(), false);
        this.sendCommand(SharedValues.initializeChannelIdentifier, gson.toJson(command));
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

    //region Impl of Serverhandler

    @Override
    public void onCurrentCommand(xyz.nifeather.morph.network.commands.S2C.S2CCurrentCommand s2CCurrentCommand)
    {
        var id = s2CCurrentCommand.getDisguiseIdentifier();
        morphManager.setCurrent(id.equals("null") ? null : id);
    }

    @Override
    public void onReAuthCommand(xyz.nifeather.morph.network.commands.S2C.S2CReAuthCommand s2CReAuthCommand)
    {
        this.disconnect();
        this.connect();
    }

    @Override
    public void onUnAuthCommand(xyz.nifeather.morph.network.commands.S2C.S2CUnAuthCommand s2CUnAuthCommand)
    {
        this.disconnect();
    }

    @Override
    public void onSwapCommand(xyz.nifeather.morph.network.commands.S2C.S2CSwapCommand s2CSwapCommand)
    {
        morphManager.swapHand();
    }

    private final AtomicBoolean displaySetToast = new AtomicBoolean();

    @Override
    public void onQueryCommand(xyz.nifeather.morph.network.commands.S2C.query.S2CQueryCommand s2CQueryCommand)
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
    public void onSetSelfViewingCommand(S2CSetSelfViewingCommand s2CSetToggleSelfCommand)
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
    public void onSetRevealing(S2CSetMobRevealingCommand command)
    {
        morphManager.revealingValue.set(command.getValue());
    }

    @Resolved
    private ClientRequestManager requestManager;

    @Override
    public void onExchangeRequestReceive(S2CRequestCommand s2CRequestCommand)
    {
        if (s2CRequestCommand.type == S2CRequestCommand.Type.Unknown)
            logger.warn("Received an invalid exchange request");

        requestManager.addRequest(s2CRequestCommand.type, s2CRequestCommand.sourcePlayer);
    }

    @Override
    public void onMapCommand(S2CSetRenderRevealCommand s2CMapCommand)
    {
        var map = s2CMapCommand.getMap();

        instanceTracker.playerMap.clear();
        instanceTracker.playerMap.putAll(map);
    }

    @Override
    public void onMapPartialCommand(S2CPartialRevealCommand s2CPartialMapCommand)
    {
        instanceTracker.playerMap.putAll(s2CPartialMapCommand.getMap());
    }

    @Override
    public void onMapClearCommand(S2CClearRevealCommand s2CMapClearCommand)
    {
        instanceTracker.playerMap.clear();
    }

    @Override
    public void onMapRemoveCommand(S2CRemoveRevealCommand s2CMapRemoveCommand)
    {
        var id = s2CMapRemoveCommand.getTargetId();
        instanceTracker.playerMap.remove(id);
    }

    @Override
    public void onClientMapSyncCommand(S2CRenderMapSyncCommand s2CRenderMapSyncCommand)
    {
        instanceTracker.onSyncCommand(s2CRenderMapSyncCommand);
    }

    @Override
    public void onClientMapAddCommand(S2CRenderMapAddCommand s2CRenderMapAddCommand)
    {
        instanceTracker.onAddCommand(s2CRenderMapAddCommand);
    }

    @Override
    public void onClientMapRemoveCommand(S2CRenderMapRemoveCommand s2CRenderMapRemoveCommand)
    {
        instanceTracker.onRemoveCommand(s2CRenderMapRemoveCommand);
    }

    @Override
    public void onClientMapClearCommand(S2CRenderMapClearCommand s2CRenderMapClearCommand)
    {
        instanceTracker.onClearCommand(s2CRenderMapClearCommand);
    }

    @Override
    public void onClientMapMetaNbtCommand(S2CRenderMapMetaCommand s2CRenderMapMetaCommand)
    {
        instanceTracker.onMetaCommand(s2CRenderMapMetaCommand);
    }

    @Override
    public void onAnimationCommand(S2CAnimationCommand command)
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

    public final Bindable<Boolean> serverReady = new Bindable<>(false);
    private boolean handshakeReceived;
    private boolean apiVersionChecked;

    public void resetServerStatus()
    {
        handshakeReceived = false;
        apiVersionChecked = false;
        usingLegacyPackets = false;

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
        serverReady.set(handshakeReceived && apiVersionChecked);
        displaySetToast.set(false);
    }

    private boolean networkInitialized;

    private boolean usingLegacyPackets;

    public void initializeNetwork()
    {
        if (networkInitialized)
            throw new RuntimeException("The network has been initialized once!");

        initializePayloadMap();

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
        {
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
        {
            connect();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
        {
            disconnect();
        });

        PayloadTypeRegistry.playC2S().register(MorphInitChannelPayload.id, MorphInitChannelPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MorphVersionChannelPayload.id, MorphVersionChannelPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MorphCommandPayload.id, MorphCommandPayload.CODEC);

        //初始化网络
        ClientPlayNetworking.registerGlobalReceiver(MorphInitChannelPayload.id, (payload, context) ->
        {
            String msgDeny = "no";

            var respond = gson.fromJson(payload.message(), InitializeRespondV3.class);
            serverVersion = respond.apiVersion();

            SharedValues.client_UseNewPacketSerializeMethod = true;
            usingLegacyPackets = false;
            serverReady.set(true);
/*
            if (1+1<2)
            {
                if (content.stream().noneMatch(s -> s.equals(SharedValues.newProtocolIdentify)))
                {
                    logger.info("The server is using legacy method to serialize commands.");
                    usingLegacyPackets = true;

                    SharedValues.client_UseNewPacketSerializeMethod = false;
                }
                else
                {
                    logger.info("The server is using new method to serialize commands.");
                    usingLegacyPackets = false;

                    SharedValues.client_UseNewPacketSerializeMethod = true;
                }

                if (content.stream().anyMatch(s -> s.equals(msgDeny)))
                {
                    logger.error("Initialize failed: Denied by server");
                    return;
                }
            }
*/
            handshakeReceived = true;
            apiVersionChecked = true;
            updateServerStatus();

            // Server parses version with Integer.parseInt(), and client only accepts integer value not string
            // What a cursed pair :(

            //if (!usingLegacyPackets)
            //    sendCommand(SharedValues.versionChannelIdentifier, "" + getImplmentingApiVersion());
            //else
            //    sendCommand(SharedValues.versionChannelIdentifierLegacy, "" + getImplmentingApiVersion());

            sendCommand(new C2SRequestInitialCommand());
            sendCommand(new C2SSetSingleOptionCommand(C2SSetSingleOptionCommand.ClientOptionEnum.CLIENTVIEW, config.allowClientView));
            sendCommand(new C2SSetSingleOptionCommand(C2SSetSingleOptionCommand.ClientOptionEnum.HUD, config.displayDisguiseOnHud));
        });

        ClientPlayNetworking.registerGlobalReceiver(MorphVersionChannelPayload.id, (payload, context) ->
        {
            this.handleVersion(payload.protocolVersion());
        });

        ClientPlayNetworking.registerGlobalReceiver(MorphCommandPayload.id, (payload, context) ->
        {
            handleCommand(payload.content());
        });

        // Legacy

        PayloadTypeRegistry.playC2S().register(LegacyMorphCommandPayload.id, LegacyMorphCommandPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LegacyMorphVersionChannelPayload.id, LegacyMorphVersionChannelPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(LegacyMorphVersionChannelPayload.id, (payload, context) ->
        {
            handleVersion(payload.getProtocolVersion());
        });

        ClientPlayNetworking.registerGlobalReceiver(LegacyMorphCommandPayload.id, (payload, context) ->
        {
            handleCommand(payload.content());
        });

        networkInitialized = true;
    }

    private void handleCommand(String input)
    {
        try
        {
            if (config.verbosePackets)
                logger.info("Received client command: " + input);

            var commandRecord = gson.fromJson(input, S2CCommandRecord.class);

            if (!serverReady.get() && !commandRecord.commandName().equals("reauth"))
            {
                if (config.verbosePackets)
                    logger.warn("Received command before initialize complete, not processing... ('%s')".formatted(input));

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
            logger.error("发生异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleVersion(int input)
    {
        serverVersion = input;
        apiVersionChecked = true;
        updateServerStatus();

        logger.info("Server API version: " + serverVersion);
    }

    public static Boolean serverSideSneaking;

    //endregion Network
}
