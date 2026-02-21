package xyz.nifeather.morph.client.properties;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ClientProperty<X, E>(String identifier,
                                                  X defaultValue, Class<X> type,
                                                  Class<E> appliableClass,
                                                  IInputHandle<X> inputHandle,
                                                  IOutputHandle<X> outputHandle,
                                                  IEntityHandle<X, E> entityHandle)
{
    public Optional<X> handleInput(String input)
    {
        return inputHandle.handle(input);
    }

    @NotNull
    public Optional<String> handleOutput(X value) throws Exception
    {
        return outputHandle.handle(value);
    }

    public Optional<E> tryCastEntity(Object obj)
    {
        if (!appliableClass.isInstance(obj))
            return Optional.empty();

        return Optional.of((E) obj);
    }

    public Optional<X> tryCastValue(Object obj)
    {
        if (!type.isInstance(obj))
            return Optional.empty();

        return Optional.of((X) obj);
    }

    public void apply(E e, X x)
    {
        tryCastEntity(e).ifPresent(entity -> tryCastValue(x).ifPresent(value -> entityHandle.handle(entity, value)));
    }

    public static <X, E> Builder<X, E> builder(String identifier, X value, Class<X> type, Class<E> appliableClass)
    {
        return new Builder<>(identifier, value, type, appliableClass);
    }

    public static <X, E> Builder<X, E> builder(String identifier, X value, Class<E> appliableClass)
    {
        return new Builder<>(identifier, value, (Class<X>) value.getClass(), appliableClass);
    }

    public static final class Builder<X, E>
    {
        private static void noOp(Object o1, Object o) {}

        private final String identifier;
        private final Class<E> appliableClass;
        private final X defaultValue;
        private final Class<X> type;
        private IInputHandle<X> inputHandle = CommonInputHandles::noOp;
        private IOutputHandle<X> outputHandle = CommonOutputHandles::noOp;
        private IEntityHandle<X, E> entityHandle = (o, o1) -> noOp(o1, o);

        public Builder(String identifier,
                       X defaultValue, Class<X> type,
                       Class<E> appliableClass)
        {
            this.identifier = identifier;
            this.appliableClass = appliableClass;

            this.defaultValue = defaultValue;
            this.type = type;
        }

        public Builder<X, E> inputHandle(IInputHandle<X> inputHandle)
        {
            this.inputHandle = inputHandle;
            return this;
        }

        public Builder<X, E> outputHandle(IOutputHandle<X> outputHandle)
        {
            this.outputHandle = outputHandle;
            return this;
        }

        public Builder<X, E> entityHandle(IEntityHandle<X, E> entityHandle)
        {
            this.entityHandle = entityHandle;
            return this;
        }

        public ClientProperty<X, E> build()
        {
            return new ClientProperty<>(
                    identifier,
                    defaultValue, type,
                    appliableClass,
                    inputHandle,
                    outputHandle,
                    entityHandle
            );
        }
    }
}
