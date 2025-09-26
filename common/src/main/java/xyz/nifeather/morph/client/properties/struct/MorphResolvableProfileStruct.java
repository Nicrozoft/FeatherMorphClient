package xyz.nifeather.morph.client.properties.struct;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record MorphResolvableProfileStruct(boolean isDynamic, @Nullable UUID uuid, @Nullable String name, String data)
{
}
