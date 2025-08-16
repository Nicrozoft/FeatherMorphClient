package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.CowVariant;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class CowPropertyHandler extends LivingEntityPropertyHandler<Cow>
{
    public final ClientProperty<Holder<CowVariant>> VARIANT = ClientProperty.of(PropertyNames.COW_VARIANT, s -> CommonInputHandles.readVariantHolder(Registries.COW_VARIANT, s));

    public CowPropertyHandler()
    {
        register(VARIANT);
    }

    @Override
    public Optional<Cow> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Cow cow ? cow : null);
    }

    @Override
    protected <X> void applyToEntity(Cow entity, ClientProperty<X> property, X value)
    {
        if (property.identifier().equals(PropertyNames.COW_VARIANT))
            entity.setVariant((Holder<CowVariant>) value);
    }
}
