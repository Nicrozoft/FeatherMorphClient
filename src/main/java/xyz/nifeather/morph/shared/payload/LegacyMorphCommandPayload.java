package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

public record LegacyMorphCommandPayload(String content) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, LegacyMorphCommandPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> BufferUtils.writeCommandBuf(value.content, buf),
            buf -> new LegacyMorphCommandPayload(BufferUtils.readCommandBuf(buf))
    );

    public static final Type<LegacyMorphCommandPayload> id = new Type<>(SharedValues.commandChannelIdentifierLegacy);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
