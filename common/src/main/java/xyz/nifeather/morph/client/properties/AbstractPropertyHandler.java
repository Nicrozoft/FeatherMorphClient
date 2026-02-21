package xyz.nifeather.morph.client.properties;

import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Collection;
import java.util.List;
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

    public Map<String, ClientProperty<?>> getRegisteredProperties()
    {
        return Map.copyOf(propertyMap);
    }

    protected abstract <X> void applyToEntity(E entity, DisguiseSyncer syncer, ClientProperty<X> property, X value);

    protected boolean acceptNullOptional()
    {
        return false;
    }

    public final void handle(Map<String, String> input, DisguiseSyncer syncer)
    {
        var entity = this.tryCast(syncer.getDisguiseInstance()).orElse(null);
        if (entity == null) return;

        input.forEach((key, value) ->
        {
            var clientProperty = propertyMap.getOrDefault(key, null);
            if (clientProperty == null)
                return;

            var cast = (ClientProperty<Object>) clientProperty;

            var handleResult = cast.handleInput(value);

            if (handleResult.isEmpty() && this.acceptNullOptional())
                this.applyToEntity(entity, syncer, cast, null);
            else
                cast.handleInput(value).ifPresent(result -> this.applyToEntity(entity, syncer, cast, result));
        });
    }
}
