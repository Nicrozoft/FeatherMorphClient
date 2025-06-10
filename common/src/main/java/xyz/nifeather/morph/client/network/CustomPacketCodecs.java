package xyz.nifeather.morph.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.string.StringDecoder;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;

public class CustomPacketCodecs
{
    /**
     * {@return a codec for a string value with maximum length {@code maxLength}}
     *
     * @see #STRING
     * @see net.minecraft.network.FriendlyByteBuf#readUtf(int)
     * @see net.minecraft.network.FriendlyByteBuf#writeUtf(String, int)
     */
    public static StreamCodec<ByteBuf, String> string(final int maxLength) {
        return new StreamCodec<>(){

            @Override
            public String decode(ByteBuf byteBuf)
            {
                System.out.println("~DECODE PACKET WITH MAX LENGTH " + maxLength);
                return Utf8String.read(byteBuf, maxLength);
            }

            @Override
            public void encode(ByteBuf byteBuf, String string)
            {
                System.out.println("~ENCODE PACKET WITH CONTENT '%s'".formatted(string));
                Utf8String.write(byteBuf, string, maxLength);
            }
        };
    }
}
