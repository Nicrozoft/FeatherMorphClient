package xyz.nifeather.morph.client.properties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPropertyCollection
{
    private final Map<String, ClientProperty<?, ?>> propertyMap = new ConcurrentHashMap<>();

    protected void register(ClientProperty<?, ?>... properties)
    {
        for (ClientProperty<?, ?> property : properties)
            register(property);
    }

    protected void register(ClientProperty<?, ?> property)
    {
        propertyMap.put(property.identifier(), property);
    }

    public Map<String, ClientProperty<?, ?>> getRegisteredProperties()
    {
        return Map.copyOf(propertyMap);
    }
}
