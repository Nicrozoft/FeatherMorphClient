package xyz.nifeather.morph.client.properties.struct;

import org.jetbrains.annotations.Nullable;

public record MorphProfileProperty(String name, String value, @Nullable String signature)
{
}
