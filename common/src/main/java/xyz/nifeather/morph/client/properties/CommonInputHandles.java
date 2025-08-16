package xyz.nifeather.morph.client.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;

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
}
