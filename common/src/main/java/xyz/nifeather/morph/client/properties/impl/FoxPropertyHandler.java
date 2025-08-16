package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Fox;
import xyz.nifeather.morph.client.mixin.accessors.FoxAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class FoxPropertyHandler extends LivingEntityPropertyHandler<Fox>
{
    public final ClientProperty<Fox.Variant> VARIANT = ClientProperty.of(PropertyNames.FOX_VARIANT, this::readVariant);

    public FoxPropertyHandler()
    {
        register(VARIANT);
    }

    private Optional<Fox.Variant> readVariant(String string)
    {
        return Optional.of(string.equalsIgnoreCase("snow") ? Fox.Variant.SNOW : Fox.Variant.DEFAULT);
    }

    @Override
    public Optional<Fox> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Fox fox ? fox : null);
    }

    @Override
    protected <X> void applyToEntity(Fox entity, ClientProperty<X> property, X value)
    {
        if (property.equals(VARIANT))
            ((FoxAccessor)entity).callSetVariant((Fox.Variant) value);
    }
}
