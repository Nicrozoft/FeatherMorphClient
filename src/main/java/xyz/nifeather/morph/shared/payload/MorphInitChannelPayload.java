package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

public record MorphInitChannelPayload(String message) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, MorphInitChannelPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> BufferUtils.writeInitBuf(value.message(), buf),
            buf -> new MorphInitChannelPayload(BufferUtils.readInitBuf(buf))
    );

    public static final CustomPacketPayload.Type<MorphInitChannelPayload> id = new Type<>(SharedValues.initializeChannelIdentifier);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
