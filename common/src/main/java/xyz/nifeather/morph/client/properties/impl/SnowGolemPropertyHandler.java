package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class SnowGolemPropertyHandler extends EntityPropertyHandler<SnowGolem>
{
    public final ClientProperty<Boolean, SnowGolem> HAS_PUMPKIN =
            ClientProperty.builder(PropertyNames.SNOW_GOLEM_HAS_PUMPKIN, false, SnowGolem.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(SnowGolem::setPumpkin)
                    .build();

    public SnowGolemPropertyHandler()
    {
        register(HAS_PUMPKIN);
    }
}
