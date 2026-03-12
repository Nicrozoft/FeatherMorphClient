package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.Zoglin;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class ZoglinPropertyCollection extends EntityPropertyCollection<Zoglin>
{
    public final ClientProperty<Boolean, Zoglin> IS_BABY =
            ClientProperty.builder(PropertyNames.ZOGLIN_IS_BABY, false, Zoglin.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(Zoglin::setBaby)
                    .build();

    public ZoglinPropertyCollection()
    {
        register(IS_BABY);
    }
}
