package xyz.nifeather.morph.client.properties;

import java.util.Optional;

@FunctionalInterface
public interface IOutputHandle<X>
{
    Optional<String> handle(X value);
}
