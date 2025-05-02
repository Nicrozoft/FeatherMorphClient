package xyz.nifeather.morph.server.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.network.BasicClientHandler;
import xyz.nifeather.morph.network.InitializeState;
import xyz.nifeather.morph.network.PlayerOptions;
import xyz.nifeather.morph.network.commands.C2S.*;
import xyz.nifeather.morph.network.commands.CommandRegistriesNew;
import xyz.nifeather.morph.network.commands.S2C.AbstractS2CCommand;
import xyz.nifeather.morph.network.commands.S2C.S2CCommandRecord;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CAddAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.clientrender.S2CCRSyncRegisterCommand;
import xyz.nifeather.morph.network.commands.S2C.query.QueryType;
import xyz.nifeather.morph.network.commands.S2C.query.S2CQueryCommand;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.morph.network.commands.S2C.set.S2CSetSelfViewingStatusCommand;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.morphs.FabricDisguiseSession;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;
import xyz.nifeather.morph.shared.payload.V3MorphCommandPayload;

import java.util.List;
import java.util.Map;

public class FabricClientHandler extends ServerPluginObject implements BasicClientHandler<ServerPlayer>
{
    private final CommandRegistriesNew commandRegistries = new CommandRegistriesNew();

    public FabricClientHandler()
    {
        commandRegistries.registerC2S(C2SCommandNames.RequestInitial, C2SRequestInitialCommand::fromArguments)
                .registerC2S(C2SCommandNames.Morph, C2SMorphCommand::fromArguments)
                .registerC2S(C2SCommandNames.ActivateSkill, C2SActivateSkillCommand::fromArguments)
                .registerC2S(C2SCommandNames.SetSingleOption, C2SSetSingleOptionCommand::fromArguments)
                .registerC2S(C2SCommandNames.ToggleSelf, C2SToggleSelfCommand::fromArguments)
                .registerC2S(C2SCommandNames.Unmorph, C2SUnmorphCommand::fromArguments)
                .registerC2S(C2SCommandNames.ExchangeRequestManagement, C2SExchangeRequestManagementCommand::fromArguments)
                .registerC2S(C2SCommandNames.RequestAnimation, C2SRequestAnimationCommand::fromArguments);
    }

    private final Bindable<Boolean> logInComingPackets = new Bindable<>(true);

    private void logPacket(boolean isOutGoingPacket, ServerPlayer player, String channel, String data, int size)
    {
        var arrow = isOutGoingPacket ? " -> " : " <- ";

        String builder = channel + arrow
                + player.getName().tryCollapseToString()
                + " :: "
                + "'%s'".formatted(data)
                + " (≈ %s bytes)".formatted(size);

        logger.info(builder);
    }

    public void onCommandPayload(V3MorphCommandPayload morphCommandPayload, ServerPlayNetworking.Context context)
    {
        var player = context.player();
        var input = morphCommandPayload.content();

        if (logInComingPackets.get())
            logPacket(false, player, morphCommandPayload.type().id().toString(), input, input.length());

        try
        {
            var record = gson.fromJson(input, C2SCommandRecord.class);
            var command = commandRegistries.createC2SCommand(record.commandName(), record.arguments());
            command.setOwner(player);
            command.onCommand(this);
        }
        catch (Throwable t)
        {
            logger.error("Failed to handle client command '%s': %s".formatted(input, t.getMessage()));
            logger.error("Disconnecting player " + player.getScoreboardName());
            disconnect(player);
        }
    }

    // todo: Implement this
    public boolean clientConnected(ServerPlayer player)
    {
        return true;
    }

    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private boolean sendCommand(ServerPlayer player, AbstractS2CCommand<?> command, boolean forceSend)
    {
        var record = S2CCommandRecord.fromS2CCommand(command);
        var cmd = gson.toJson(record);

        logPacket(true, player, V3MorphCommandPayload.id.id().toString(), cmd, cmd.length());

        var payload = new V3MorphCommandPayload(cmd);

        ServerPlayNetworking.send(player, payload);
        return true;
    }

    @Override
    public boolean sendCommand(ServerPlayer player, AbstractS2CCommand<?> basicS2CCommand)
    {
        return this.sendCommand(player, basicS2CCommand, false);
    }

    /**
     * 获取某一玩家的客户端版本
     *
     * @param ServerPlayerEntity 目标玩家
     * @return 此玩家的客户端版本
     */
    @Override
    public int getPlayerVersion(ServerPlayer ServerPlayerEntity)
    {
        return 0;
    }

    /**
     * 获取所有已连接的玩家
     *
     * @return 一个包含所有已连接玩家的列表
     * @apiNote 此列表可能包含已连接但未初始化的玩家
     */
    @Override
    public List<ServerPlayer> getConnectedPlayers()
    {
        return List.of();
    }

    /**
     * 获取某一玩家的连接状态
     *
     * @param ServerPlayerEntity 目标玩家
     * @return 此玩家的连接状态
     */
    @Override
    public InitializeState getInitializeState(ServerPlayer ServerPlayerEntity)
    {
        return null;
    }

    /**
     * 检查玩家的客户端是否已连接并初始化
     *
     * @param ServerPlayerEntity 目标玩家
     * @return 此玩家是否已经初始化
     */
    @Override
    public boolean isPlayerInitialized(ServerPlayer ServerPlayerEntity)
    {
        return false;
    }

    /**
     * 检查玩家的连接状态
     *
     * @param ServerPlayerEntity 目标玩家
     * @return 此玩家的连接状态
     */
    @Override
    public boolean isPlayerConnected(ServerPlayer ServerPlayerEntity)
    {
        return false;
    }

    /**
     * 断开与玩家的初始化连接
     *
     * @param ServerPlayerEntity 目标玩家
     */
    @Override
    public void disconnect(ServerPlayer ServerPlayerEntity)
    {
    }

    /**
     * 获取玩家的某个配置
     *
     * @param ServerPlayerEntity 目标玩家
     */
    @Override
    public @Nullable PlayerOptions<ServerPlayer> getPlayerOption(ServerPlayer ServerPlayerEntity)
    {
        logger.warn("getPlayerOption is not implemented yet.");
        return null;
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    @Override
    public void onInitialCommand(C2SRequestInitialCommand command)
    {
        ServerPlayer player = command.getOwner();

        var unlocked = morphManager.getUnlockedDisguiseIds(player);
        var cmd = new S2CQueryCommand(QueryType.SET, unlocked);

        this.sendCommand(player, cmd);
        this.sendCommand(player, new S2CSetSelfViewingStatusCommand(true));

        Map<Integer, String> renderMap = new Object2ObjectOpenHashMap<>();

        for (FabricDisguiseSession session : morphManager.listAllSession())
            renderMap.put(session.player().getId(), session.disguiseIdentifier());

        this.sendCommand(player, new S2CCRSyncRegisterCommand(renderMap));

        Map<Integer, String> revealMap = new Object2ObjectOpenHashMap<>();

        for (FabricDisguiseSession session : morphManager.listAllSession())
            revealMap.put(session.player().getId(), session.player().getName().tryCollapseToString());

        this.sendCommand(player, new S2CAddAdminRevealCommand(revealMap));
    }

    @Override
    public void onMorphCommand(C2SMorphCommand command)
    {
        ServerPlayer player = command.getOwner();
        String disguiseId = command.identifier();

        morphManager.morph(player, disguiseId);
    }

    @Override
    public void onOptionCommand(C2SSetSingleOptionCommand command)
    {
    }

    @Override
    public void onSkillCommand(C2SActivateSkillCommand command)
    {
    }

    @Override
    public void onToggleSelfCommand(C2SToggleSelfCommand command)
    {
        ServerPlayer player = command.getOwner();
        var val = command.getSelfViewMode();

        switch (val)
        {
            case ON, CLIENT_ON -> sendCommand(player, new S2CSetSelfViewingStatusCommand(true));
            default -> sendCommand(player, new S2CSetSelfViewingStatusCommand(false));
        }
    }

    @Override
    public void onUnmorphCommand(C2SUnmorphCommand command)
    {
        ServerPlayer player = command.getOwner();

        morphManager.unMorph(player);
    }

    @Override
    public void onRequestCommand(C2SExchangeRequestManagementCommand command)
    {
    }

    @Override
    public void onAnimationCommand(C2SRequestAnimationCommand command)
    {
        ServerPlayer player = command.getOwner();

        var session = morphManager.getSessionFor(player);
        if (session == null)
        {
            player.sendSystemMessage(Component.literal("Session is NULL, you are not disguised!"));
            return;
        }

        var animationProvider = session.disguiseProvider().getAnimationProvider();
        var animationId = command.getAnimationId();
        var seqPair = animationProvider.getAnimationSetFor(session.disguiseIdentifier()).sequenceOf(animationId);

        if (!session.tryScheduleSequence(animationId, seqPair.left()))
            player.sendSystemMessage(Component.literal("Playing Animation is not available now."));
    }

    /**
     * 向某个玩家的客户端发送差异信息
     *
     * @param addits 添加
     * @param removal 删除
     * @param player 目标玩家
     */
    public void sendDiff(@Nullable List<String> addits, @Nullable List<String> removal, ServerPlayer player)
    {
        if (addits != null)
            this.sendCommand(player, new S2CQueryCommand(QueryType.ADD, addits));

        if (removal != null)
            this.sendCommand(player, new S2CQueryCommand(QueryType.REMOVE, removal));
    }
}
