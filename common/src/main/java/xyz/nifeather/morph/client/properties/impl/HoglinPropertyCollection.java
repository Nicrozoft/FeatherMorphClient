package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.hoglin.Hoglin;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class HoglinPropertyCollection extends EntityPropertyCollection<Hoglin>
{
    public final ClientProperty<Boolean, Hoglin> IS_BABY = 
            ClientProperty.builder(PropertyNames.HOGLIN_IS_BABY, false, Hoglin.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle((hoglin, baby) -> hoglin.setAge(baby ? Integer.MIN_VALUE : 1))
                    .build();

    public HoglinPropertyCollection()
    {
        register(IS_BABY);
    }
}
