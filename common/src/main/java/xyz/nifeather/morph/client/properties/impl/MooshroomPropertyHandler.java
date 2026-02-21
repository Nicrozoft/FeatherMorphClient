package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import xyz.nifeather.morph.client.mixin.accessors.MushroomCowAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class MooshroomPropertyHandler extends EntityPropertyHandler<MushroomCow>
{
    public final ClientProperty<MushroomCow.Variant> VARIANT = ClientProperty.of(PropertyNames.MOOSHROOM_VARIANT, this::readMushroomCowVariant);

    private Optional<MushroomCow.Variant> readMushroomCowVariant(String string)
    {
        return MushroomCow.Variant.BROWN.getSerializedName().equalsIgnoreCase(string)
               ? Optional.of(MushroomCow.Variant.BROWN)
               : Optional.of(MushroomCow.Variant.RED);
    }

    public MooshroomPropertyHandler()
    {
        register(VARIANT);
    }

    @Override
    public Optional<MushroomCow> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof MushroomCow mushroomCow ? mushroomCow : null);
    }

    @Override
    protected <X> void applyToEntity(MushroomCow entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, syncer, property, value);

        if (property.equals(VARIANT))
            ((MushroomCowAccessor)entity).callSetVariant((MushroomCow.Variant) value);
    }
}
