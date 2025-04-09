package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

public record MorphCommandPayload(String content) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, MorphCommandPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> BufferUtils.writeCommandBuf(value.content, buf),
            buf -> new MorphCommandPayload(BufferUtils.readCommandBuf(buf))
    );

    public static final CustomPacketPayload.Type<MorphCommandPayload> id = new Type<>(SharedValues.commandChannelIdentifier);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
