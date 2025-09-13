package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.CatAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;
import java.util.UUID;

public class CatPropertyHandler extends EntityPropertyHandler<Cat>
{
    public final ClientProperty<Holder<CatVariant>> VARIANT = ClientProperty.of(PropertyNames.CAT_VARIANT, s -> CommonInputHandles.readVariantHolder(Registries.CAT_VARIANT, s));
    public final ClientProperty<UUID> OWNER = ClientProperty.of(PropertyNames.CAT_OWNER, CommonInputHandles::uuid);
    public final ClientProperty<DyeColor> COLLAR_COLOR = ClientProperty.of(PropertyNames.CAT_COLLAR_COLOR, CommonInputHandles::readDyeColor);

    public CatPropertyHandler()
    {
        register(VARIANT, OWNER, COLLAR_COLOR);
    }

    @Override
    public Optional<Cat> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Cat cat ? cat : null);
    }

    @Override
    protected <X> void applyToEntity(Cat entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);
        var accessor = (CatAccessor)entity;

        switch (property.identifier())
        {
            case PropertyNames.CAT_VARIANT -> accessor.callSetVariant((Holder<CatVariant>) value);
            case PropertyNames.CAT_OWNER -> entity.setOwnerReference(new EntityReference<>((UUID) value));
            case PropertyNames.CAT_COLLAR_COLOR -> accessor.callSetCollarColor((DyeColor) value);
        }
    }
}
