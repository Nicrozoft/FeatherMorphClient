package xyz.nifeather.morph.client.properties;

import java.util.Optional;
import java.util.function.Function;

public class ClientProperty<X>
{
    protected final String identifier;
    protected final Function<String, Optional<X>> inputHandle;

    public ClientProperty(String id, Function<String, Optional<X>> inputHandle)
    {
        this.identifier = id;
        this.inputHandle = inputHandle;
    }

    public String identifier()
    {
        return identifier;
    }

    public Optional<X> handleInput(String input)
    {
        return inputHandle.apply(input);
    }

    public static <T> ClientProperty<T> of(String id, Function<String, Optional<T>> inputHandle)
    {
        return new ClientProperty<>(id, inputHandle);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof ClientProperty<?> other)) return false;

        return other.identifier.equals(this.identifier) && other.inputHandle.equals(this.inputHandle);
    }
}
