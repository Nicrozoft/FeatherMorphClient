package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Rabbit;
import xyz.nifeather.morph.client.mixin.accessors.RabbitAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class RabbitPropertyHandler extends EntityPropertyHandler<Rabbit>
{
    public final ClientProperty<Rabbit.Variant> VARIANT = ClientProperty.of(PropertyNames.RABBIT_VARIANT, s -> CommonInputHandles.readEnum(Rabbit.Variant.values(), s));

    public RabbitPropertyHandler()
    {
        register(VARIANT);
    }

    @Override
    public Optional<Rabbit> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Rabbit rabbit ? rabbit : null);
    }

    @Override
    protected <X> void applyToEntity(Rabbit entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(VARIANT))
            ((RabbitAccessor)entity).callSetVariant((Rabbit.Variant) value);
    }
}
