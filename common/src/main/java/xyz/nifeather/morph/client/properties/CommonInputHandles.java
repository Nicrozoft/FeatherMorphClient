package xyz.nifeather.morph.client.properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.ResolvableProfile;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.utilties.NbtHelperCopy;
import xyz.nifeather.morph.client.utilties.NbtUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class CommonInputHandles
{
    public static final Function<String, Optional<Boolean>> BOOLEAN = input -> Optional.of(Boolean.valueOf(input));

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

            ResourceLocation resourceLocation = ResourceLocation.parse(input);
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
        var record = gson.fromJson(input, MorphResolvableProfileRecord.class);
        return record.isDynamic()
               ? resolvableProfileDynamic(record)
               : resolvableProfileStatic(record);
    }

    public static Optional<ResolvableProfile> resolvableProfileDynamic(MorphResolvableProfileRecord record)
    {
        if (record.uuid() != null)
            return Optional.of(ResolvableProfile.createUnresolved(record.uuid()));
        else if (record.name() != null)
            return Optional.of(ResolvableProfile.createUnresolved(record.name()));

        FeatherMorphClientBootstrap.LOGGER.error("Unable to create dynamic ResolvableProfile: Either UUID and name is NULL");

        return Optional.empty();
    }

    public static Optional<ResolvableProfile> resolvableProfileStatic(MorphResolvableProfileRecord record)
    {
        var profile = gameProfile(record.data());
        if (profile.isEmpty())
        {
            FeatherMorphClientBootstrap.LOGGER.error("Unable to create static ResolvableProfile, not a valid profile string");
            return Optional.empty();
        }

        return Optional.of(ResolvableProfile.createResolved(profile.get()));
    }

    public static Optional<GameProfile> gameProfile(String input)
    {
        var compound = NbtUtils.parseSNbt(input);
        return Optional.ofNullable(compound == null ? null : NbtHelperCopy.toGameProfile(compound));
    }
}
