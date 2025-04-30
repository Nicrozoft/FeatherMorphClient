package xyz.nifeather.morph.client.network.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.llamalad7.mixinextras.sugar.Share;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.client.network.ServerHandler;
import xyz.nifeather.morph.client.network.handlers.record.CommandHandleResult;
import xyz.nifeather.morph.client.network.handlers.record.VersionHandleResult;
import xyz.nifeather.morph.network.commands.C2S.AbstractC2SCommand;
import xyz.nifeather.morph.network.commands.C2S.C2SCommandRecord;
import xyz.nifeather.morph.network.commands.C2S.ClientInitializeRecordV3;
import xyz.nifeather.morph.network.commands.S2C.InitializeRespondV3;
import xyz.nifeather.morph.network.commands.S2C.S2CCommandRecord;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.payload.V3MorphCommandPayload;
import xyz.nifeather.morph.shared.payload.V3MorphInitChannelPayload;

public class V3ProtocolHandler implements IProtocolHandler
{
    public static final V3ProtocolHandler INSTANCE = new V3ProtocolHandler();

    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public void sendCommand(AbstractC2SCommand<?> command)
    {
        var record = C2SCommandRecord.fromC2SCommand(command);
        var cmd = gson.toJson(record);

        ServerHandler.logPacket(true, SharedValues.commandChannelV3, cmd);
        ClientPlayNetworking.send(new V3MorphCommandPayload(cmd));
    }

    @Override
    public void sendInitializeRequest(ClientInitializeRecordV3 initializeRecordV3)
    {
        var str = gson.toJson(initializeRecordV3);
        ServerHandler.logPacket(true, SharedValues.initializeChannelV3, str);

        ClientPlayNetworking.send(new V3MorphInitChannelPayload(str));
    }

    @Override
    public void sendVersion(int clientVersion)
    {
        throw new RuntimeException("V3 protocol does not have a version payload");
    }

    @Override
    public InitializeRespondV3 handleInitializeRespond(CustomPacketPayload customPayload)
    {
        if (!(customPayload instanceof V3MorphInitChannelPayload(String message)))
            throw new RuntimeException("Given payload is not an instance of V3MorphInitChannelPayload");

        return gson.fromJson(message, InitializeRespondV3.class);
    }

    @Override
    public CommandHandleResult handleCommandInput(CustomPacketPayload customPayload)
    {
        if (!(customPayload instanceof V3MorphCommandPayload(String content)))
        {
            FeatherMorphClient.LOGGER.error("Can't handle command input: Given payload is not an instance of V3MorphCommandPayload");
            return CommandHandleResult.fail();
        }

        var record = gson.fromJson(content, S2CCommandRecord.class);
        return CommandHandleResult.from(record);
    }

    @Override
    public VersionHandleResult handleServerVersionInput(CustomPacketPayload customPayload)
    {
        throw new RuntimeException("V3 protocol does not have a version payload");
    }
}
