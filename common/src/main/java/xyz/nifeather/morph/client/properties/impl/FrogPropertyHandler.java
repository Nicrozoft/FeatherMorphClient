package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import xyz.nifeather.morph.client.mixin.accessors.FrogAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class FrogPropertyHandler extends EntityPropertyHandler<Frog>
{
    public final ClientProperty<Holder<FrogVariant>> VARIANT = ClientProperty.of(PropertyNames.FROG_VARIANT, s -> CommonInputHandles.readVariantHolder(Registries.FROG_VARIANT, s));

    public FrogPropertyHandler()
    {
        register(VARIANT);
    }

    @Override
    public Optional<Frog> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Frog frog ? frog : null);
    }

    @Override
    protected <X> void applyToEntity(Frog entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(VARIANT))
            ((FrogAccessor)entity).callSetVariant((Holder<FrogVariant>) value);
    }
}
