package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

public record V3MorphCommandPayload(String content) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, V3MorphCommandPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> buf.writeUtf(value.content),
            buf -> new V3MorphCommandPayload(buf.readUtf())
    );

    public static final Type<V3MorphCommandPayload> id = new Type<>(SharedValues.commandChannelV3);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
