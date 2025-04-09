package xyz.nifeather.morph.shared.payload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.morph.shared.SharedValues;

import java.nio.charset.StandardCharsets;
import net.minecraft.network.FriendlyByteBuf;

public class BufferUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger("MorphClient$BufferUtils");

    //region Write

    // int
    public static void writeVersionBufAuto(int content, FriendlyByteBuf buf)
    {
        if (SharedValues.client_UseNewPacketSerializeMethod)
            writeIntBuf(content, buf);
        else
            writeVersionBufLegacy(content, buf);
    }

    public static void writeIntBuf(int content, FriendlyByteBuf buf)
    {
        buf.writeInt(content);
    }

    public static void writeVersionBufLegacy(int content, FriendlyByteBuf buf)
    {
        var str = "" + content;
        var bytes = str.getBytes(StandardCharsets.UTF_8);

        buf.writeBytes(bytes);
    }

    // string
    public static void writeCommandBuf(String content, FriendlyByteBuf buf)
    {
        if (SharedValues.client_UseNewPacketSerializeMethod)
            writeBuf(content, buf);
        else
            writeBufLegacy(content, buf);
    }

    public static void writeInitBuf(String content, FriendlyByteBuf buf)
    {
        writeBuf(content, buf);
    }

    public static void writeBuf(String content, FriendlyByteBuf buf)
    {
        buf.writeUtf(content);
    }

    public static void writeBufLegacy(String content, FriendlyByteBuf buf)
    {
        buf.writeBytes(content.getBytes(StandardCharsets.UTF_8));
    }

    //endregion Write

    //region Read

    // int
    public static int readVersionBuf(FriendlyByteBuf buf)
    {
        try
        {
            return buf.readInt();
        }
        catch (Throwable t)
        {
            LOGGER.error("Can't read version from legacy buf: " + t.getMessage());
            return 1;
        }
    }

    // string

    public static String readInitBuf(FriendlyByteBuf buf)
    {
        try
        {
            return readBuf(buf);
        }
        catch (Throwable t)
        {
            LOGGER.info("Can't read buffer with readBuf(), trying legacy method...");
            return readBufLegacy(buf);
        }
    }

    public static String readCommandBuf(FriendlyByteBuf buf)
    {
        if (SharedValues.client_UseNewPacketSerializeMethod)
            return readBuf(buf);
        else
            return readBufLegacy(buf);
    }

    public static String readBuf(FriendlyByteBuf buf)
    {
        return buf.readUtf();
    }

    public static String readBufLegacy(FriendlyByteBuf buf)
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
