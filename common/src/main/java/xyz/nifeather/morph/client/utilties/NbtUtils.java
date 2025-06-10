package xyz.nifeather.morph.client.utilties;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;

public class NbtUtils
{
    private static final Logger logger = FeatherMorphClientBootstrap.LOGGER;

    public static CompoundTag parseOrThrow(@Nullable String snbt) throws Throwable
    {
        //MinecraftClient.getInstance().world.getRegistryManager()

        //var ops = MinecraftClient.getInstance().world.getRegistryManager().getOps(NbtOps.INSTANCE);

        if (TagParser.parseCompoundFully(snbt) instanceof CompoundTag compound)
            return compound;

        throw new RuntimeException("Unable to read compound! The return value of StringNbtReader.read was not an NbtCompound");
    }

    @Nullable
    public static CompoundTag parseSNbt(@Nullable String snbt)
    {
        if (snbt == null || snbt.isEmpty())
            return null;

        try
        {
            return parseOrThrow(snbt.replace("\\u003d", "="));
        }
        catch (Throwable t)
        {
            logger.warn("Unable to parse SNBT (%s): %s".formatted(t.getMessage(), snbt));
            t.printStackTrace();
        }

        return null;
    }
}