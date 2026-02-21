package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.parrot.Parrot;
import xyz.nifeather.morph.client.mixin.accessors.ParrotAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Arrays;
import java.util.Optional;

public class ParrotPropertyHandler extends EntityPropertyHandler<Parrot>
{
    public final ClientProperty<Parrot.Variant, ParrotAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.PARROT_VARIANT, Parrot.Variant.DEFAULT, ParrotAccessor.class)
                    .inputHandle(this::readParrotVariant)
                    .entityHandle(ParrotAccessor::callSetVariant)
                    .build();

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
}
