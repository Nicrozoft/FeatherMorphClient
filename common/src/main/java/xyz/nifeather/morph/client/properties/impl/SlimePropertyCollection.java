package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.cubemob.Slime;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class SlimePropertyCollection extends LivingEntityPropertyCollection<Slime>
{
    public final ClientProperty<Integer, Slime> SIZE =
            ClientProperty.builder(PropertyNames.SLIME_MAGMA_SIZE, 0, Slime.class)
                    .inputHandle(CommonInputHandles::intOrEmpty)
                    .entityHandle((slime, size) -> slime.setSize(size, false))
                    .build();

    public SlimePropertyCollection()
    {
        register(SIZE);
    }
}
