package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.goat.Goat;
import xyz.nifeather.morph.client.entities.IMorphGoat;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class GoatPropertyCollection extends EntityPropertyCollection<Goat>
{
    public final ClientProperty<Boolean, IMorphGoat> HAS_LEFT_HORN =
            ClientProperty.builder(PropertyNames.GOAT_HAS_LEFT_HORN, false, IMorphGoat.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(IMorphGoat::morphclient$setHasLeftHorn)
                    .build();

    public final ClientProperty<Boolean, IMorphGoat> HAS_RIGHT_HORN =
            ClientProperty.builder(PropertyNames.GOAT_HAS_RIGHT_HORN, false, IMorphGoat.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(IMorphGoat::morphclient$setHasRightHorn)
                    .build();

    public GoatPropertyCollection()
    {
        register(HAS_LEFT_HORN, HAS_RIGHT_HORN);
    }
}
