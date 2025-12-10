package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.parrot.Parrot;
import xyz.nifeather.morph.client.mixin.accessors.ParrotAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Arrays;
import java.util.Optional;

public class ParrotPropertyHandler extends EntityPropertyHandler<Parrot>
{
    public final ClientProperty<Parrot.Variant> VARIANT = ClientProperty.of(PropertyNames.PARROT_VARIANT, this::readParrotVariant);

    private Optional<Parrot.Variant> readParrotVariant(String string)
    {
        return Arrays.stream(Parrot.Variant.values())
                .filter(v -> v.getSerializedName().equalsIgnoreCase(string))
                .findFirst();
    }

    public ParrotPropertyHandler()
    {
        register(VARIANT);
    }

    @Override
    public Optional<Parrot> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Parrot parrot ? parrot : null);
    }

    @Override
    protected <X> void applyToEntity(Parrot entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(VARIANT))
            ((ParrotAccessor)entity).callSetVariant((Parrot.Variant) value);
    }
}
