package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.zombie.Zombie;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class ZombiePropertyhandler extends LivingEntityPropertyCollection<Zombie>
{
    public final ClientProperty<Boolean, Zombie> IS_BABY =
            ClientProperty.builder(PropertyNames.ZOMBIE_IS_BABY, false, Zombie.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(Zombie::setBaby)
                    .build();

    public ZombiePropertyhandler()
    {
        register(IS_BABY);
    }
}
