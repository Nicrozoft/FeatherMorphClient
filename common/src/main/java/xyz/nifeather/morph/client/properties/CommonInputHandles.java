package xyz.nifeather.morph.client.properties;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.mixin.accessors.ResolvableProfileAccessor;
import xyz.nifeather.morph.client.network.commands.ClientSetEquipCommand;
import xyz.nifeather.morph.client.properties.struct.MorphEquipmentStruct;
import xyz.nifeather.morph.client.properties.struct.MorphProfileProperty;
import xyz.nifeather.morph.client.properties.struct.MorphResolvableProfileStruct;
import xyz.nifeather.morph.client.utilties.NbtHelperCopy;
import xyz.nifeather.morph.client.utilties.NbtUtils;
import xyz.nifeather.morph.network.utils.ProtocolEquipmentSlot;

import java.util.*;
import java.util.function.Function;

public class CommonInputHandles
{
    public static final Function<String, Optional<Boolean>> BOOLEAN = input -> Optional.of(Boolean.valueOf(input));

    public static <X> Optional<X> noOp(String s)
    {
        return Optional.empty();
    }

    public static Optional<Boolean> readBoolean(String input)
    {
        return Optional.of(Boolean.valueOf(input));
    }

    public static <E extends Enum<?>> Optional<E> readEnum(E[] array, String input)
    {
        return Arrays.stream(array).filter(e -> e.name().equalsIgnoreCase(input)).findFirst();
    }

    public static <V> Optional<Holder<V>> readVariantHolder(ResourceKey<Registry<V>> registryKey, String input)
    {
        try
        {
            var world = Minecraft.getInstance().level;
            var registry = world.registryAccess().lookupOrThrow(registryKey);

            Identifier resourceLocation = Identifier.parse(input);
            var val = registry.getValue(resourceLocation);

            if (val == null) return Optional.empty();

            return Optional.of(registry.wrapAsHolder(val));
        }
        catch (Throwable t)
        {
            FeatherMorphClientBootstrap.LOGGER.error("Failed to read variant from input '%s' in registry '%s'".formatted(input, registryKey), t);
        }

        return Optional.empty();
    }

    public static Optional<DyeColor> readDyeColor(String input)
    {
        return Arrays.stream(DyeColor.values())
                .filter(c -> c.name().equalsIgnoreCase(input))
                .findFirst();
    }

    public static Optional<UUID> uuid(String str)
    {
        try
        {
            return Optional.of(UUID.fromString(str));
        }
        catch (Throwable t)
        {
            FeatherMorphClientBootstrap.LOGGER.error("Failed to read UUID from input '%s'".formatted(str), t);
        }

        return Optional.empty();
    }

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static Optional<Component> component(String input)
    {
        try
        {
            // https://docs.fabricmc.net/develop/text-and-translations#deserializing-text
            var json = gson.fromJson(input, JsonElement.class);
            var component = ComponentSerialization.CODEC.decode(JsonOps.INSTANCE, json)
                    .getOrThrow()
                    .getFirst();

            return Optional.ofNullable(component);
        }
        catch (Throwable ignored)
        {
            return Optional.of(Component.literal("<Component deserialization failed>"));
        }
    }

    public static Optional<String> string(String input)
    {
        return Optional.of(input);
    }

    public static <X> Optional<X> empty(String str)
    {
        return Optional.empty();
    }

    public static Optional<Integer> intOrEmpty(String str)
    {
        try
        {
            return Optional.of(Integer.parseInt(str));
        }
        catch (Throwable ignored)
        {
        }

        return Optional.empty();
    }

    public static Optional<ResolvableProfile> resolvableProfile(String input)
    {
        var record = gson.fromJson(input, MorphResolvableProfileStruct.class);
        return record.dynamic()
               ? resolvableProfileDynamic(record)
               : resolvableProfileStatic(record);
    }

    public static Optional<ResolvableProfile> resolvableProfileDynamic(MorphResolvableProfileStruct record)
    {
        if (record.id() != null)
            return Optional.of(ResolvableProfile.createUnresolved(record.id()));
        else if (record.name() != null)
            return Optional.of(ResolvableProfile.createUnresolved(record.name()));

        FeatherMorphClientBootstrap.LOGGER.error("Unable to create dynamic ResolvableProfile: Either UUID and name is NULL");

        return Optional.empty();
    }

    public static Optional<ResolvableProfile> resolvableProfileStatic(MorphResolvableProfileStruct record)
    {
        // literally, Minecraft allows static ResolvableProfile with both ID and Name null
        String skinName = record.name() == null ? "" : record.name();
        UUID profileID = record.id() == null ? UUID.randomUUID() : record.id();

        // properties
        ImmutableMultimap.Builder<String, Property> propertiesBuilder = ImmutableMultimap.builder();
        for (String propertyJson : record.properties())
        {
            var struct = gson.fromJson(propertyJson, MorphProfileProperty.class);
            propertiesBuilder.put(struct.name(), new Property(struct.name(), struct.value(), struct.signature()));
        }

        var profile = new GameProfile(profileID, skinName, new PropertyMap(propertiesBuilder.build()));
        var skinPatch = new PlayerSkin.Patch(
                readNullableResourceTexture(record.bodyTexture()),
                readNullableResourceTexture(record.cape()),
                readNullableResourceTexture(record.elytra()),
                readEnum(PlayerModelType.values(), record.model())
        );

        // we have no way but doing this, or we have to use NMS on both side!
        ResolvableProfile resolvableProfile = ResolvableProfile.createResolved(profile);
        ((ResolvableProfileAccessor)(Object)resolvableProfile).setSkinPatch(skinPatch);

        return Optional.of(resolvableProfile);
    }

    private static Optional<ClientAsset.ResourceTexture> readNullableResourceTexture(@Nullable String input)
    {
        if (input == null) return Optional.empty();

        Identifier location = Identifier.tryParse(input);
        if (location == null) return Optional.empty();

        return Optional.of(new ClientAsset.ResourceTexture(location));
    }

    public static Optional<GameProfile> gameProfile(String input)
    {
        var compound = NbtUtils.parseSNbt(input);
        return Optional.ofNullable(compound == null ? null : NbtHelperCopy.toGameProfile(compound));
    }

    public static Optional<DisguiseEquipment> equipment(String input)
    {
        var struct = gson.fromJson(input, MorphEquipmentStruct.class);
        int dataVersion = struct.dataVersion();

        var builder = DisguiseEquipment.builder(Map.of());

        struct.equipmentData().forEach((slotName, snbt) ->
        {
            var protocolSlot = ProtocolEquipmentSlot.valueOf(slotName.toUpperCase());
            var item = ClientSetEquipCommand.jsonToStack(snbt, dataVersion);

            EquipmentSlot slot = switch (protocolSlot)
            {
                case MAINHAND -> EquipmentSlot.MAINHAND;
                case OFF_HAND -> EquipmentSlot.OFFHAND;

                case HELMET -> EquipmentSlot.HEAD;
                case CHESTPLATE -> EquipmentSlot.CHEST;
                case LEGGINGS ->  EquipmentSlot.LEGS;
                case BOOTS -> EquipmentSlot.FEET;

                case BODY -> EquipmentSlot.BODY;
                case SADDLE -> EquipmentSlot.SADDLE;
            };

            if (item != null)
                builder.forSlot(slot, item);
        });

        return Optional.of(builder.build());
    }

    public static Optional<Float> readFloat(String input)
    {
        try
        {
            return Optional.of(Float.parseFloat(input));
        }
        catch (NumberFormatException e)
        {
            return Optional.empty();
        }
    }

    public static Optional<Vector3f> readVector3fRelaxed(String input)
    {
        if (input.startsWith("["))
            return readVector3fJson(input);
        else
            return readVector3fHandwrite(input);
    }

    public static Optional<Vector3f> readVector3fHandwrite(String input)
    {
        String[] split = input.split(",");

        if (split.length == 1)
        {
            String size = split[0];
            float floatSize = readFloat(size).orElse(Float.NaN);
            if (Float.isNaN(floatSize))
                return Optional.empty();

            return Optional.of(new Vector3f(floatSize));
        }

        if (split.length != 3)
            return Optional.empty();

        float x = readFloat(split[0]).orElseThrow();
        float y = readFloat(split[1]).orElseThrow();
        float z = readFloat(split[2]).orElseThrow();

        return Optional.of(new Vector3f(x, y, z));
    }

    public static Optional<Vector3f> readVector3fJson(String input)
    {
        List<Float> list;

        try
        {
            list = gson.fromJson(input, new TypeToken<List<Float>>(){});
        }
        catch (JsonParseException e)
        {
            return Optional.empty();
        }

        if (list.size() == 1)
        {
            var value = list.getFirst();
            return Optional.of(new Vector3f(value));
        }

        if (list.size() != 3)
        {
            return Optional.empty();
        }

        float x = list.getFirst();
        float y = list.get(1);
        float z = list.get(2);

        return Optional.of(new Vector3f(x, y, z));
    }

    public static Optional<Integer> readHexColor(String input)
    {
        String colorCodeString = input.replaceFirst("#", "");
        if (colorCodeString.length() > 6)
        {
            return Optional.empty();
        }

        int integerColor;
        try
        {
            integerColor = Integer.parseInt(colorCodeString, 16);
        }
        catch (NumberFormatException e)
        {
            return Optional.empty();
        }

        return Optional.of(integerColor);
    }

    public static Optional<Integer> readLight(String input)
    {
        int sky = 15;
        int block = 15;

        if (input.startsWith("["))
        {
            List<Integer> list;

            try
            {
                list = gson.fromJson(input, new TypeToken<List<Integer>>(){});
            }
            catch (JsonParseException e)
            {
                return Optional.empty();
            }

            if (!list.isEmpty())
                block = list.getFirst();

            if (list.size() >= 2)
                sky = list.get(1);
        }
        else
        {
            try
            {

                block = Integer.parseInt(input);
            }
            catch (NumberFormatException e)
            {
                return Optional.empty();
            }
        }

        int light = Brightness.pack(block, sky);

        return Optional.of(light);
    }
}
