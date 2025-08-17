package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PigVariant;
import xyz.nifeather.morph.client.mixin.accessors.PigAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class PigPropertyHandler extends EntityPropertyHandler<Pig>
{
    public final ClientProperty<Holder<PigVariant>> VARIANT = ClientProperty.of(PropertyNames.PIG_VARIANT, s -> CommonInputHandles.readVariantHolder(Registries.PIG_VARIANT, s));

    public PigPropertyHandler()
    {
        register(VARIANT);
    }

    @Override
    public Optional<Pig> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Pig pig ? pig : null);
    }

    @Override
    protected <X> void applyToEntity(Pig entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.identifier().equals(PropertyNames.PIG_VARIANT))
            ((PigAccessor)entity).callSetVariant((Holder<PigVariant>) value);
    }
}
