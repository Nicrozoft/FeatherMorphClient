package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import xyz.nifeather.morph.client.mixin.accessors.CatAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;
import java.util.UUID;

public class CatPropertyHandler extends LivingEntityPropertyHandler<Cat>
{
    public final ClientProperty<Holder<CatVariant>> VARIANT = ClientProperty.of(PropertyNames.CAT_VARIANT, s -> CommonInputHandles.readVariantHolder(Registries.CAT_VARIANT, s));
    public final ClientProperty<UUID> OWNER = ClientProperty.of(PropertyNames.CAT_OWNER, CommonInputHandles::uuid);

    public CatPropertyHandler()
    {
        register(VARIANT, OWNER);
    }

    @Override
    public Optional<Cat> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Cat cat ? cat : null);
    }

    @Override
    protected <X> void applyToEntity(Cat entity, ClientProperty<X> property, X value)
    {
        switch (property.identifier())
        {
            case PropertyNames.CAT_VARIANT -> ((CatAccessor)entity).callSetVariant((Holder<CatVariant>) value);
            case PropertyNames.CAT_OWNER -> entity.setOwnerReference(new EntityReference<>((UUID) value));
        }
    }
}
