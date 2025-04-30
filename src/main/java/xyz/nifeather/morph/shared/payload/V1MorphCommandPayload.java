package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

import java.nio.charset.StandardCharsets;

public record V1MorphCommandPayload(String content) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, V1MorphCommandPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> buf.writeBytes(value.content().getBytes(StandardCharsets.UTF_8)),
            buf -> new V1MorphCommandPayload(BufferUtils.readCommandBufLegacy(buf))
    );

    public static final Type<V1MorphCommandPayload> id = new Type<>(SharedValues.commandChannelV1);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
