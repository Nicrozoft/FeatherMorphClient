package xyz.nifeather.morph.client.properties;

@FunctionalInterface
public interface IEntityHandle<X, E>
{
    void handle(E entity, X value);
}
