package xyz.nifeather.morph.client.properties;

import java.util.Optional;

public class CommonOutputHandles
{
    public static <X> Optional<String> noOp(X x)
    {
        return Optional.empty();
    }

    public static Optional<String> writeBoolean(boolean bl)
    {
        return Optional.of(Boolean.toString(bl).toLowerCase());
    }
}
