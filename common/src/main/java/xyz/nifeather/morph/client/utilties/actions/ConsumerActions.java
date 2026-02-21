package xyz.nifeather.morph.client.utilties.actions;

import java.util.function.Consumer;

public class ConsumerActions<X> extends CallableActions<X, Consumer<X>>
{
    @Override
    public void invoke(X value)
    {
        hooks.forEach(c -> c.accept(value));
    }
}
