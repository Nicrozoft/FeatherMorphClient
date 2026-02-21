package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zoglin;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class ZoglinPropertyHandler extends EntityPropertyHandler<Zoglin>
{
    public final ClientProperty<Boolean, Zoglin> IS_BABY =
            ClientProperty.builder(PropertyNames.ZOGLIN_IS_BABY, false, Zoglin.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(Zoglin::setBaby)
                    .build();

    public ZoglinPropertyHandler()
    {
        register(IS_BABY);
    }
}
