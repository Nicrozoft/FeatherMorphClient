package xyz.nifeather.morph.client.properties;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.utilties.actions.BiConsumerActions;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class ClientPropertyHolder
{
    private final Map<ClientProperty<?>, Object> propertyMap = new ConcurrentHashMap<>();
    private final Map<String, ClientProperty<?>> validProperties = new ConcurrentHashMap<>();

    protected final BiConsumerActions<ClientProperty<?>, Object> actions = new BiConsumerActions<>();
    public <X> void hookOnPropertyWrite(BiConsumer<ClientProperty<X>, X> consumer)
    {
        actions.hook((BiConsumer) consumer);
    }

    public void registerFromPropertyCollection(AbstractPropertyHandler<?> properties)
    {
        validProperties.putAll(properties.getRegisteredProperties());
    }

    public Map<String, String> toNetworkProperties()
    {
        Map<String, String> map = new ConcurrentHashMap<>();

        try
        {
            for (Map.Entry<ClientProperty<?>, Object> entry : this.propertyMap.entrySet())
            {
                var property = (ClientProperty<Object>) entry.getKey();

                var value = entry.getValue();

                map.put(property.identifier(), property.handleOutput(value));
            }
        }
        catch (Exception ignored)
        {
        }

        return map;
    }

    /**
     * Add property as a valid property for this handler
     * Kept for external use, so that if anyone wants to add their own property, they can call this method!
     */
    public void addProperty(ClientProperty<?> property)
    {
        validProperties.put(property.identifier(), property);
    }

    public void updateFromPropertiesInput(Map<String, String> input)
    {
        var parsedResults = new ConcurrentHashMap<ClientProperty<?>, Object>();

        for (Map.Entry<String, String> entry : input.entrySet())
        {
            var key = entry.getKey();
            var value = entry.getValue();

            var property = (ClientProperty<Object>) this.validProperties.getOrDefault(key, null);
            if (property == null)
                continue;

            var val = property.handleInput(value).orElse(null);
            if (val == null) continue;

            //property.validateInput(val, inputSource, validationFlags);
            parsedResults.put(property, val);
            this.writeGeneric(property, val);
        }
    }

    public void reset()
    {
        this.validProperties.clear();
        clearProperties();
    }

    public void clearProperties()
    {
        propertyMap.clear();
    }

    private void writeGeneric(ClientProperty<?> property, Object value)
    {
        //if (!property.type().isInstance(value))
         //   throw new IllegalArgumentException("Incompatible value for id '%s', excepted for '%s', but got '%s'".formatted(property.id(), property.defaultVal().getClass(), value.getClass()));

        set((ClientProperty<Object>)property, value);
    }

    /**
     * @throws NullPointerException If the given value is NULL
     */
    public <X> void set(ClientProperty<X> property, @NotNull X value) throws NullPointerException
    {
        Objects.requireNonNull(value, "Null values are not accepted");

        var existing = getOptional(property).orElse(null);

        if (!value.equals(existing))
        {
            X diffIfPossible;
            if (existing instanceof ISupportDiffs<?> existingDiff)
                diffIfPossible = ((ISupportDiffs<X>)existingDiff).diff(value);
            else
                diffIfPossible = value;

            propertyMap.put(property, value);
            this.actions.invoke(BiConsumerActions.pair(property, diffIfPossible));
        }
    }

    public boolean contains(ClientProperty<?> property)
    {
        return propertyMap.containsKey(property);
    }

    public boolean contains(String propertyName)
    {
        return propertyMap.keySet().stream().anyMatch(sp -> sp.identifier().equals(propertyName));
    }

    @Nullable
    public <X> X get(ClientProperty<X> property)
    {
        return this.getOr(property, null);
    }

    public <X> Optional<X> getOptional(ClientProperty<X> property)
    {
        return Optional.ofNullable(getOr(property, null));
    }

    @Nullable
    @Contract("_, null -> _; _, !null -> !null")
    public <X> X getOr(ClientProperty<X> property, X defaultVal)
    {
        return (X) propertyMap.getOrDefault(property, defaultVal);
    }

    @Nullable
    @Contract("_, null -> _; _, !null -> !null")
    public <X> X getOr(String propertyName, @Nullable X defaultVal)
    {
        var property = validProperties.getOrDefault(propertyName, null);
        if (property == null) return defaultVal;

        return (X) getOr((ClientProperty<Object>) property, defaultVal);
    }
    public Map<ClientProperty<?>, ?> getAll()
    {
        return new Object2ObjectArrayMap<>(propertyMap);
    }

    /**
     * Execute a simple copy which copies our value to the given PropertyHandler
     */
    public void copyTo(ClientPropertyHolder other)
    {
        other.validProperties.putAll(this.validProperties);
        this.propertyMap.forEach((k, v) -> other.set((ClientProperty<Object>) k, v));
    }

    public void dispose()
    {
        actions.clear();
    }
}
