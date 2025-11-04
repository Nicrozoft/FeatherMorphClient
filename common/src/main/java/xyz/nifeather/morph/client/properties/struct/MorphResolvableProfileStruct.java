package xyz.nifeather.morph.client.properties.struct;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MorphResolvableProfileStruct(@Nullable UUID id, @Nullable String name, List<String> properties,
                                           @Nullable String cape, @Nullable String elytra, @Nullable String model, @Nullable String bodyTexture,
                                           boolean dynamic)
{
}