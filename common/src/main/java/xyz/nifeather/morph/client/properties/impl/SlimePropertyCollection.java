package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.Slime;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class SlimePropertyCollection extends EntityPropertyCollection<Slime>
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
