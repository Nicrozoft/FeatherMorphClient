package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.Creeper;
import xyz.nifeather.morph.client.entities.IMorphCreeper;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class CreeperPropertyCollection extends LivingEntityPropertyCollection<Creeper>
{
    public final ClientProperty<Boolean, IMorphCreeper> CHARGED =
            ClientProperty.builder(PropertyNames.CREEPER_CHARGED, false, IMorphCreeper.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(IMorphCreeper::morphclient$setPowered)
                    .build();

    public CreeperPropertyCollection()
    {
        register(CHARGED);
    }
}
