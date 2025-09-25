package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.WolfAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;
import java.util.UUID;

public class WolfPropertyHandler extends EntityPropertyHandler<Wolf>
{
    public final ClientProperty<Holder<WolfVariant>> VARIANT = ClientProperty.of(PropertyNames.WOLF_VARIANT, s -> CommonInputHandles.readVariantHolder(Registries.WOLF_VARIANT, s));
    public final ClientProperty<UUID> OWNER = ClientProperty.of(PropertyNames.WOLF_OWNER, CommonInputHandles::uuid);
    public final ClientProperty<DyeColor> COLLAR_COLOR = ClientProperty.of(PropertyNames.WOLF_COLLAR_COLOR, CommonInputHandles::readDyeColor);

    public WolfPropertyHandler()
    {
        register(VARIANT, OWNER, COLLAR_COLOR);
    }

    @Override
    public Optional<Wolf> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Wolf wolf ? wolf : null);
    }

    @Override
    protected <X> void applyToEntity(Wolf entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        switch (property.identifier())
        {
            case PropertyNames.WOLF_VARIANT -> ((WolfAccessor)entity).callSetVariant((Holder<WolfVariant>) value);
            case PropertyNames.WOLF_OWNER ->
            {
                entity.setOwnerReference(EntityReference.of((UUID) value));
                writeTamed(entity);
            }

            case PropertyNames.WOLF_COLLAR_COLOR ->
            {
                ((WolfAccessor)entity).callSetCollarColor((DyeColor) value);
                writeTamed(entity);
            }
        }
    }

    private void writeTamed(TamableAnimal entity)
    {
        entity.setTame(true, true);
    }
}
