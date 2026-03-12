package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.golem.SnowGolem;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class SnowGolemPropertyCollection extends EntityPropertyCollection<SnowGolem>
{
    public final ClientProperty<Boolean, SnowGolem> HAS_PUMPKIN =
            ClientProperty.builder(PropertyNames.SNOW_GOLEM_HAS_PUMPKIN, false, SnowGolem.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(SnowGolem::setPumpkin)
                    .build();

    public SnowGolemPropertyCollection()
    {
        register(HAS_PUMPKIN);
    }
}
