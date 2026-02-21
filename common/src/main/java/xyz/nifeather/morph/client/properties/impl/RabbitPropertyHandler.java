package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import xyz.nifeather.morph.client.mixin.accessors.RabbitAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class RabbitPropertyHandler extends EntityPropertyHandler<Rabbit>
{
    public final ClientProperty<Rabbit.Variant, RabbitAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.RABBIT_VARIANT, Rabbit.Variant.BROWN, RabbitAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readEnum(Rabbit.Variant.values(), s))
                    .entityHandle(RabbitAccessor::callSetVariant)
                    .build();

    public RabbitPropertyHandler()
    {
        register(VARIANT);
    }
}
