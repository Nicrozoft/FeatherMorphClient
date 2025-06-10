package xyz.nifeather.morph.shared.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import xyz.nifeather.morph.shared.SharedValues;

public record V1V2MorphInitChannelPayload(String message) implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, V1V2MorphInitChannelPayload> CODEC = StreamCodec.ofMember(
            (value, buf) -> buf.writeUtf(value.message()),
            buf -> new V1V2MorphInitChannelPayload(BufferUtils.tryReadUtfOrEmpty(buf))
    );

    public static final Type<V1V2MorphInitChannelPayload> id = new Type<>(SharedValues.initializeChannelV1V2);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return id;
    }
}
