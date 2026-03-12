package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.cow.MushroomCow;
import xyz.nifeather.morph.client.mixin.accessors.MushroomCowAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class MooshroomPropertyCollection extends EntityPropertyCollection<MushroomCow>
{
    public final ClientProperty<MushroomCow.Variant, MushroomCowAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.MOOSHROOM_VARIANT, MushroomCow.Variant.RED, MushroomCowAccessor.class)
                    .inputHandle(this::readMushroomCowVariant)
                    .entityHandle(MushroomCowAccessor::callSetVariant)
                    .build();

    private Optional<MushroomCow.Variant> readMushroomCowVariant(String string)
    {
        return MushroomCow.Variant.BROWN.getSerializedName().equalsIgnoreCase(string)
               ? Optional.of(MushroomCow.Variant.BROWN)
               : Optional.of(MushroomCow.Variant.RED);
    }

    public MooshroomPropertyCollection()
    {
        register(VARIANT);
    }
}
