package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

public record V2MorphCommandPayload(String content) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, V2MorphCommandPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> buf.writeUtf(value.content),
            buf -> new V2MorphCommandPayload(buf.readUtf())
    );

    public static final CustomPacketPayload.Type<V2MorphCommandPayload> id = new Type<>(SharedValues.commandChannelV2);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
