package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

public record V3MorphInitChannelPayload(String message) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, V3MorphInitChannelPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> buf.writeUtf(value.message),
            buf -> new V3MorphInitChannelPayload(buf.readUtf())
    );

    public static final CustomPacketPayload.Type<V3MorphInitChannelPayload> id = new Type<>(SharedValues.initializeChannelV3);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
