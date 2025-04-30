package xyz.nifeather.morph.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.client.network.handlers.V1ProtocolHandler;
import xyz.nifeather.morph.client.network.handlers.V2ProtocolHandler;
import xyz.nifeather.morph.network.Constants;
import xyz.nifeather.morph.network.commands.C2S.ClientInitializeRecordV3;
import xyz.nifeather.morph.network.commands.S2C.InitializeRespondV3;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.payload.*;

import java.util.List;

public class LegacyServerHandler
{
    private final ServerHandler serverHandler;

    public LegacyServerHandler(ServerHandler serverHandler)
    {
        this.serverHandler = serverHandler;

        initNetwork();
    }

    private void initNetwork()
    {
        PayloadTypeRegistry.playC2S().register(V2MorphVersionChannelPayload.id, V2MorphVersionChannelPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(V2MorphCommandPayload.id, V2MorphCommandPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(V1V2MorphInitChannelPayload.id, V1V2MorphInitChannelPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(V1MorphVersionChannelPayload.id, V1MorphVersionChannelPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(V1MorphCommandPayload.id, V1MorphCommandPayload.CODEC);

        // Receiver

        ClientPlayNetworking.registerGlobalReceiver(V1V2MorphInitChannelPayload.id, this::handleInitV1V2);

        ClientPlayNetworking.registerGlobalReceiver(V2MorphVersionChannelPayload.id, this::handleVersionV2);
        ClientPlayNetworking.registerGlobalReceiver(V2MorphCommandPayload.id, this::handleCommandV2);

        ClientPlayNetworking.registerGlobalReceiver(V1MorphVersionChannelPayload.id, this::handleVersionV1);
        ClientPlayNetworking.registerGlobalReceiver(V1MorphCommandPayload.id, this::handleCommandV1);
    }

    public void sendInitializeV2(List<String> clientFeatures, int clientApi)
    {
        V2ProtocolHandler.INSTANCE.sendInitializeRequest(new ClientInitializeRecordV3(clientFeatures, clientApi, true));
    }

    private void handleInitV1V2(V1V2MorphInitChannelPayload payload, ClientPlayNetworking.Context context)
    {
        ServerHandler.logPacket(false, SharedValues.initializeChannelV1V2, payload.message());

        var v2Handle = V2ProtocolHandler.INSTANCE.handleInitializeRespond(payload);
        if (v2Handle.apiVersion() != -1) // -1: 没有检测到新协议的标识
        {
            FeatherMorphClient.LOGGER.info("Server is using V2 packets");

            serverHandler.setProtocolHandler(V2ProtocolHandler.INSTANCE);
            V2ProtocolHandler.INSTANCE.sendVersion(Constants.ApiLevel.ANIMATION.protocolVersion);
            return;
        }

        FeatherMorphClient.LOGGER.info("Server is possibly using V1 packets");
        serverHandler.setProtocolHandler(V1ProtocolHandler.INSTANCE);
        V1ProtocolHandler.INSTANCE.sendVersion(Constants.ApiLevel.ANIMATION.protocolVersion);
    }

    private void handleVersionV2(V2MorphVersionChannelPayload payload, ClientPlayNetworking.Context context)
    {
        ServerHandler.logPacket(false, SharedValues.versionChannelV2, "" + payload.protocolVersion());

        var record = new InitializeRespondV3(List.of(SharedValues.newProtocolIdentify), payload.protocolVersion());
        serverHandler.handleServerInitRespond(record);
    }

    private void handleCommandV2(V2MorphCommandPayload payload, ClientPlayNetworking.Context context)
    {
        ServerHandler.logPacket(false, SharedValues.commandChannelV2, payload.content());

        var handleResult = V2ProtocolHandler.INSTANCE.handleCommandInput(payload);
        if (!handleResult.success()) return;

        serverHandler.handleCommand(handleResult.result());
    }

    private void handleVersionV1(V1MorphVersionChannelPayload payload, ClientPlayNetworking.Context context)
    {
        ServerHandler.logPacket(false, SharedValues.versionChannelV1, "" + payload.getProtocolVersion());

        var record = new InitializeRespondV3(List.of(), payload.protocolVersion());
        serverHandler.handleServerInitRespond(record);
    }

    private void handleCommandV1(V1MorphCommandPayload payload, ClientPlayNetworking.Context context)
    {
        ServerHandler.logPacket(false, SharedValues.commandChannelV1, payload.content());

        var handleResult = V1ProtocolHandler.INSTANCE.handleCommandInput(payload);
        if (!handleResult.success()) return;

        serverHandler.handleCommand(handleResult.result());
    }
}
