package xyz.nifeather.morph.client.network.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.fmccl.converter.C2SCommandConverter;
import xyz.nifeather.fmccl.converter.S2CCommandConverter;
import xyz.nifeather.fmccl.processor.S2CCommandProcessor;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.network.ServerHandler;
import xyz.nifeather.morph.client.network.handlers.command.ClientS2CCommandConverter;
import xyz.nifeather.morph.client.network.handlers.command.ClientS2CCommandProcessor;
import xyz.nifeather.morph.client.network.handlers.record.CommandHandleResult;
import xyz.nifeather.morph.client.network.handlers.record.VersionHandleResult;
import xyz.nifeather.morph.network.commands.C2S.AbstractC2SCommand;
import xyz.nifeather.morph.network.commands.C2S.ClientInitializeRecordV3;
import xyz.nifeather.morph.network.commands.S2C.InitializeRespondV3;
import xyz.nifeather.morph.network.commands.S2C.S2CCommandRecord;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.payload.V1V2MorphInitChannelPayload;
import xyz.nifeather.morph.shared.payload.V2MorphCommandPayload;
import xyz.nifeather.morph.shared.payload.V2MorphVersionChannelPayload;
import xyz.nifeather.morph.shared.platform.Services;

import java.util.Arrays;
import java.util.List;

public class V2ProtocolHandler implements IProtocolHandler
{
    public static final V2ProtocolHandler INSTANCE = new V2ProtocolHandler();

    private final C2SCommandConverter c2sConverter = new C2SCommandConverter();
    private final S2CCommandConverter s2cConverter = new ClientS2CCommandConverter();
    private final S2CCommandProcessor legacyS2CProcessor = new ClientS2CCommandProcessor();

    @Override
    public void sendCommand(AbstractC2SCommand<?> command)
    {
        var netherite = c2sConverter.toNetheriteCommand(command);
        var cmd = netherite.buildCommand();

        ServerHandler.logPacket(true, SharedValues.commandChannelV1, cmd);
        Services.PLATFORM.sendNetworkPacket(new V2MorphCommandPayload(cmd));
    }

    @Override
    public void sendInitializeRequest(ClientInitializeRecordV3 initializeRecordV3)
    {
        ServerHandler.logPacket(true, SharedValues.initializeChannelV1V2, "<???>");
        Services.PLATFORM.sendNetworkPacket(new V1V2MorphInitChannelPayload(SharedValues.newProtocolIdentify));
    }

    @Override
    public void sendVersion(int clientVersion)
    {
        ServerHandler.logPacket(true, SharedValues.versionChannelV1, "<???> " + clientVersion);
        Services.PLATFORM.sendNetworkPacket(new V2MorphVersionChannelPayload(clientVersion));
    }

    @Override
    public InitializeRespondV3 handleInitializeRespond(CustomPacketPayload customPayload)
    {
        if (!(customPayload instanceof V1V2MorphInitChannelPayload(String message)))
            throw new RuntimeException("Given payload is not an instance of V1V2MorphInitChannelPayload");

        if (!message.contains(SharedValues.newProtocolIdentify))
            return new InitializeRespondV3(List.of(), -1);

        return new InitializeRespondV3(Arrays.stream(message.split(" ")).toList(), 0);
    }

    @Override
    public CommandHandleResult handleCommandInput(CustomPacketPayload customPayload)
    {
        try
        {
            if (!(customPayload instanceof V2MorphCommandPayload(String message)))
                throw new RuntimeException("Given payload is not an instance of V2MorphCommandPayload");

            var legacyCommand = legacyS2CProcessor.processLegacyCommandLine(message);
            var modern = s2cConverter.fromNetheriteCommand(legacyCommand);

            return CommandHandleResult.from(S2CCommandRecord.fromS2CCommand(modern));
        }
        catch (Throwable t)
        {
            FeatherMorphClientBootstrap.LOGGER.error("Failed to handle command from server: %s".formatted(t.getMessage()));
            t.printStackTrace();

            return CommandHandleResult.fail();
        }
    }

    @Override
    public VersionHandleResult handleServerVersionInput(CustomPacketPayload customPayload)
    {
        if (!(customPayload instanceof V2MorphVersionChannelPayload(int protocolVersion)))
            throw new RuntimeException("Given payload is not an instance of V2MorphVersionChannelPayload");

        return VersionHandleResult.from(protocolVersion);
    }
}
