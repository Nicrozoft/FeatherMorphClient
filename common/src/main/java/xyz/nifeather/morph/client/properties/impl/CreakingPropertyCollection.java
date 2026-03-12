package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.creaking.Creaking;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class CreakingPropertyCollection extends LivingEntityPropertyCollection<Creaking>
{
    public final ClientProperty<Boolean, Creaking> EYES_GLOWING =
            ClientProperty.builder(PropertyNames.CREAKING_EYES_GLOWING, false, Creaking.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(Creaking::setIsActive)
                    .build();

    public CreakingPropertyCollection()
    {
        register(EYES_GLOWING);
    }
}
