package xyz.nifeather.morph.client.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IValueChangeListener<T, V>
{
    void invoke(T which, @Nullable V oldValue, @NotNull V newValue);
}
