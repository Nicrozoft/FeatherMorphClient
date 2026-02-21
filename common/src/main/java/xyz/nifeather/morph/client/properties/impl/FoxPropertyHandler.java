package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.fox.Fox;
import xyz.nifeather.morph.client.mixin.accessors.FoxAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class FoxPropertyHandler extends EntityPropertyHandler<Fox>
{
    public final ClientProperty<Fox.Variant, FoxAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.FOX_VARIANT, Fox.Variant.DEFAULT, FoxAccessor.class)
                    .inputHandle(this::readVariant)
                    .entityHandle(FoxAccessor::callSetVariant)
                    .build();

    public FoxPropertyHandler()
    {
        register(VARIANT);
    }

    private Optional<Fox.Variant> readVariant(String string)
    {
        return Optional.of(string.equalsIgnoreCase("snow") ? Fox.Variant.SNOW : Fox.Variant.DEFAULT);
    }
}
