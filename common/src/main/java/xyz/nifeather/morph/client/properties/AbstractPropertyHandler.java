package xyz.nifeather.morph.client.properties;

import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPropertyHandler<E extends Entity>
{
    public abstract Optional<E> tryCast(Entity entity);

    private final Map<String, ClientProperty<?>> propertyMap = new ConcurrentHashMap<>();

    protected void register(ClientProperty<?>... properties)
    {
        for (ClientProperty<?> property : properties)
            register(property);
    }

    protected void register(ClientProperty<?> property)
    {
        propertyMap.put(property.identifier, property);
    }

    protected abstract <X> void applyToEntity(E entity, ClientProperty<X> property, X value);

    protected boolean acceptNullOptional()
    {
        return false;
    }

    public final void handle(Map<String, String> input, E entity)
    {
        input.forEach((key, value) ->
        {
            var clientProperty = propertyMap.getOrDefault(key, null);
            if (clientProperty == null)
                return;

            var cast = (ClientProperty<Object>) clientProperty;

            var handleResult = cast.handleInput(value);

            if (handleResult.isEmpty() && this.acceptNullOptional())
                this.applyToEntity(entity, cast, null);
            else
                cast.handleInput(value).ifPresent(result -> this.applyToEntity(entity, cast, result));
        });
    }
}
