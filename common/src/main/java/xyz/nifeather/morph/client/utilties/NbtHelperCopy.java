package xyz.nifeather.morph.client.utilties;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
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
    public static GameProfile toGameProfile(CompoundTag compound)
    {
        String name = "NIL";
        if (compound.contains("Name"))
            name = compound.getString("Name").orElseThrow();

        UUID uuid = Util.NIL_UUID;
        if (compound.contains("Id"))
        {
            var tag = compound.get("Id");
            var readUUID = readUUID(tag);

            if (readUUID != null)
                uuid = readUUID;
        }

        if (!compound.contains("Properties")) return new GameProfile(uuid, name);

        try
        {
            var propertiesCompound = compound.getCompound("Properties").orElseThrow();
            ImmutableMultimap.Builder<String, Property> propertiesBuilder = ImmutableMultimap.builder();

            propertiesCompound.forEach((key, tag) ->
            {
                var list = propertiesCompound.getListOrEmpty(key);

                for (int i = 0; i < list.size(); i++)
                {
                    var childCompound = list.getCompound(i).orElse(null);
                    if (childCompound == null) continue;

                    var value = childCompound.getString("Value").orElseThrow();

                    if (childCompound.contains("Signature"))
                        propertiesBuilder.put(key, new Property(key, value, childCompound.getString("Signature").orElseThrow()));
                    else
                        propertiesBuilder.put(key, new Property(key, value));
                }
            });

            return new GameProfile(uuid, name, new PropertyMap(propertiesBuilder.build()));
        }
        catch (Throwable t)
        {
            FeatherMorphClientBootstrap.LOGGER.warn("Can't parse profile properties", t);

            return null;
        }
    }

}