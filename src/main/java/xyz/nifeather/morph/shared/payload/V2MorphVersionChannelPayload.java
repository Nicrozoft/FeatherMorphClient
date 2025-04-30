package xyz.nifeather.morph.shared.payload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public record V2MorphVersionChannelPayload(int protocolVersion) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, V2MorphVersionChannelPayload> CODEC  = StreamCodec.ofMember(
            (value, buf) -> buf.writeInt(value.protocolVersion()),
            buf -> new V2MorphVersionChannelPayload(buf.readInt())
    );

    public int getProtocolVersion()
    {
        return protocolVersion;
    }

    public static int parseInt(String input)
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch (Throwable t)
        {
            SharedValues.LOGGER.error("Failed to parse protocol version from input: " + t.getMessage());
        }

        return 1;
    }

    public static int parseBuf(FriendlyByteBuf buf)
    {
        //System.out.println("Buf is '" + buf.toString(StandardCharsets.UTF_8) + "' :: with hashCode" + buf.hashCode());
        int read = -1;

        try
        {
            // If from a bukkit server
            read = buf.readInt();
        }
        catch (Throwable ignored)
        {
        }

        // Kept for legacy servers.
        if (read == -1)
        {
            try
            {
                // If from a fabric server
                var str = buf.toString(StandardCharsets.UTF_8);
                read = Integer.parseInt(str);
            }
            catch (Throwable t)
            {
                SharedValues.LOGGER.error("Error parsing protocol version!");
            }
        }

        buf.clear();
        return read;
    }

    public static final Type<V2MorphVersionChannelPayload> id = new Type<>(SharedValues.versionChannelV2);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
