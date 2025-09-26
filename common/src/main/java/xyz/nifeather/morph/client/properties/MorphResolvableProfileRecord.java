package xyz.nifeather.morph.client.properties;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record MorphResolvableProfileRecord(boolean isDynamic, @Nullable UUID uuid, @Nullable String name, String data)
{
}
