package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.armadillo.Armadillo;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class ArmadilloPropertyCollection extends LivingEntityPropertyCollection<Armadillo>
{
    public final ClientProperty<Armadillo.ArmadilloState, Armadillo> ARMADILLO_STATE =
            ClientProperty.builder(PropertyNames.ARMADILLO_STATE, Armadillo.ArmadilloState.IDLE, Armadillo.ArmadilloState.class, Armadillo.class)
                    .inputHandle(s -> CommonInputHandles.readEnum(Armadillo.ArmadilloState.values(), s))
                    .outputHandle(CommonOutputHandles::writeEnum)
                    .entityHandle(Armadillo::switchToState)
                    .build();

    public ArmadilloPropertyCollection()
    {
        register(ARMADILLO_STATE);
    }
}
