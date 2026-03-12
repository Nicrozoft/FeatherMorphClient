package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.piglin.Piglin;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class PiglinPropertyCollection extends LivingEntityPropertyCollection<Piglin>
{
    public final ClientProperty<Boolean, Piglin> DANCING =
            ClientProperty.builder(PropertyNames.PIGLIN_DANCING, false, Piglin.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(Piglin::setDancing)
                    .build();

    public PiglinPropertyCollection()
    {
        register(DANCING);
    }
}
