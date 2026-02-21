package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class HappyGhastPropertyHandler extends EntityPropertyHandler<HappyGhast>
{
    public final ClientProperty<Boolean, HappyGhast> IS_GHASTLING =
            ClientProperty.builder(PropertyNames.HAPPY_GHAST_IS_GHASTLING, false, HappyGhast.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(AgeableMob::setBaby)
                    .build();

    public HappyGhastPropertyHandler()
    {
        register(IS_GHASTLING);
    }
}
