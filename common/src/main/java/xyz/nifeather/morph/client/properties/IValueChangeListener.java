package xyz.nifeather.morph.client.properties;

@FunctionalInterface
public interface IValueChangeListener<T, V>
{
    void invoke(T which, V oldValue, V newValue);
}
