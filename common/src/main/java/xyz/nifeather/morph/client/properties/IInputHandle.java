package xyz.nifeather.morph.client.properties;

import java.util.Optional;

@FunctionalInterface
public interface IInputHandle<X>
{
    Optional<X> handle(String input);
}
