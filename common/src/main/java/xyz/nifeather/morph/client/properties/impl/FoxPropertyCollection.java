package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.fox.Fox;
import xyz.nifeather.morph.client.mixin.accessors.FoxAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class FoxPropertyCollection extends LivingEntityPropertyCollection<Fox>
{
    public final ClientProperty<Fox.Variant, FoxAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.FOX_VARIANT, Fox.Variant.DEFAULT, FoxAccessor.class)
                    .inputHandle(this::readVariant)
                    .entityHandle(FoxAccessor::callSetVariant)
                    .build();

    public final ClientProperty<FoxStatus, Fox> FOX_STATUS =
            ClientProperty.builder(PropertyNames.FOX_STATUS, FoxStatus.STANDING, Fox.class)
                    .inputHandle(s -> CommonInputHandles.readEnum(FoxStatus.values(), s))
                    .outputHandle(CommonOutputHandles::writeEnum)
                    .entityHandle((fox, status) ->
                    {
                        var accessor = (FoxAccessor) fox;
                        fox.setSitting(false);
                        accessor.callSetSleeping(false);

                        if (status == FoxStatus.SITTING)
                            fox.setSitting(true);
                        else if (status == FoxStatus.SLEEPING)
                            accessor.callSetSleeping(true);
                    })
                    .build();

    public FoxPropertyCollection()
    {
        register(VARIANT, FOX_STATUS);
    }

    private Optional<Fox.Variant> readVariant(String string)
    {
        return Optional.of(string.equalsIgnoreCase("snow") ? Fox.Variant.SNOW : Fox.Variant.DEFAULT);
    }

    public enum FoxStatus
    {
        STANDING,
        SITTING,
        SLEEPING
    }
}
