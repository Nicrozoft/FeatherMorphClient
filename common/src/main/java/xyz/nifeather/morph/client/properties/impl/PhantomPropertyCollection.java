package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.Phantom;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class PhantomPropertyCollection extends EntityPropertyCollection<Phantom>
{
    public final ClientProperty<Integer, Phantom> SIZE =
            ClientProperty.builder(PropertyNames.PHANTOM_SIZE, 0, Phantom.class)
                    .inputHandle(CommonInputHandles::intOrEmpty)
                    .entityHandle(Phantom::setPhantomSize)
                    .build();

    public PhantomPropertyCollection()
    {
        register(SIZE);
    }
}
