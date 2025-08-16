package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.ChickenVariant;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ChickenPropertyHandler extends LivingEntityPropertyHandler<Chicken>
{
    public final ClientProperty<Holder<ChickenVariant>> VARIANT = ClientProperty.of(PropertyNames.CHICKEN_VARIANT, s -> CommonInputHandles.readVariantHolder(Registries.CHICKEN_VARIANT, s));

    public ChickenPropertyHandler()
    {
        register(VARIANT);
    }

    @Override
    public Optional<Chicken> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Chicken chicken ? chicken : null);
    }

    @Override
    protected <X> void applyToEntity(Chicken entity, ClientProperty<X> property, X value)
    {
        if (property.identifier().equals(PropertyNames.CHICKEN_VARIANT))
            entity.setVariant((Holder<ChickenVariant>) value);
    }
}
