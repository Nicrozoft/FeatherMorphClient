package xyz.nifeather.morph.client.network.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.client.network.handlers.record.CommandHandleResult;
import xyz.nifeather.morph.client.network.handlers.record.VersionHandleResult;
import xyz.nifeather.morph.network.commands.C2S.AbstractC2SCommand;
import xyz.nifeather.morph.network.commands.C2S.ClientInitializeRecordV3;
import xyz.nifeather.morph.network.commands.S2C.InitializeRespondV3;

public interface IProtocolHandler {
    void sendCommand(AbstractC2SCommand<?> command) throws Exception;

    void sendInitializeRequest(ClientInitializeRecordV3 initializeRecordV3);

    void sendVersion(int clientVersion);

    InitializeRespondV3 handleInitializeRespond(CustomPacketPayload customPayload);

    CommandHandleResult handleCommandInput(CustomPacketPayload customPayload);

    VersionHandleResult handleServerVersionInput(CustomPacketPayload customPayload);
}
