package xyz.nifeather.morph.client.utilties;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;

import java.util.UUID;

public class NbtHelperCopy
{
    public static UUID readUUID(@Nullable Tag element)
    {
        if (element == null)
            return null;

        if (element.getType() != IntArrayTag.TYPE)
        {
            FeatherMorphClientBootstrap.LOGGER.warn("Given element is not a int array, can't convert to UUID");
            return null;
        }

        int[] is = ((IntArrayTag)element).getAsIntArray();

        if (is.length != 4)
        {
            FeatherMorphClientBootstrap.LOGGER.warn("Given int array is not of length 4, can't convert to UUID");
            return null;
        }

        return UUIDUtil.uuidFromIntArray(is);
    }

    @Nullable
    public static GameProfile toGameProfile(CompoundTag nbt)
    {
        UUID uuid = readUUID(nbt.get("Id"));
        if (uuid == null)
            uuid = Util.NIL_UUID;

        String name = nbt.getString("Name").orElse(null);
        if (name == null)
        {
            FeatherMorphClientBootstrap.LOGGER.warn("Given NBT does not contain a name, can't convert to GameProfile");
            return null;
        }

        try
        {
            GameProfile gameProfile = new GameProfile(uuid, name);

            if (!nbt.contains("Properties"))
                return gameProfile;

            CompoundTag nbtCompound = nbt.getCompound("Properties").orElseThrow();

            for (String subKey : nbtCompound.keySet())
            {
                ListTag nbtList = nbtCompound.getList(subKey).orElseThrow();

                for (int i = 0; i < nbtList.size(); ++i)
                {
                    CompoundTag nbtCompound2 = nbtList.getCompound(i).orElseThrow();

                    String base64Url = nbtCompound2.getString("Value").orElse("");

                    if (nbtCompound2.contains("Signature"))
                        gameProfile.getProperties().put(subKey, new Property(subKey, base64Url, nbtCompound2.getString("Signature").orElseThrow()));
                    else
                        gameProfile.getProperties().put(subKey, new Property(subKey, base64Url));
                }
            }

            return gameProfile;
        }
        catch (Throwable var11)
        {
            FeatherMorphClientBootstrap.LOGGER.warn("Failed parsing compound to GameProfile: " + var11.getMessage());
            var11.printStackTrace();

            return null;
        }
    }

}