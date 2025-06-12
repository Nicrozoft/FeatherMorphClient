package xyz.nifeather.morph.shared.payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import net.minecraft.network.FriendlyByteBuf;

public class BufferUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger("MorphClient$BufferUtils");

    //region Write

    public static void writeVersionBufLegacy(int content, FriendlyByteBuf buf)
    {
        var str = Integer.toString(content);
        var bytes = str.getBytes(StandardCharsets.UTF_8);

        buf.writeBytes(bytes);
    }

    //endregion Write

    //region Read

    // string

    public static String tryReadUtfOrEmpty(FriendlyByteBuf buf)
    {
        try
        {
            return buf.readUtf();
        }
        catch (Throwable t)
        {
            LOGGER.info("Can't read buffer with readUtf(), returning empty...");
            return "";
        }
    }

    public static String readCommandBufLegacy(FriendlyByteBuf buf)
    {
        var directBuffer = buf.readBytes(buf.readableBytes());
        var dst = new byte[directBuffer.capacity()];
        directBuffer.getBytes(0, dst);

        buf.clear();
        directBuffer.clear();
        return new String(dst, StandardCharsets.UTF_8);
    }

    //endregion Read
}
