package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.fish.Pufferfish;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class PufferfishPropertyCollection extends LivingEntityPropertyCollection<Pufferfish>
{
    public ClientProperty<PufferfishState, Pufferfish> PUFFERFISH_STATE =
            ClientProperty.builder(PropertyNames.PUFFERFISH_PUFF_STATE, PufferfishState.SMALL, Pufferfish.class)
                    .inputHandle(s -> CommonInputHandles.readEnum(PufferfishState.values(), s))
                    .outputHandle(CommonOutputHandles::writeEnum)
                    .entityHandle((pufferfish, state) -> pufferfish.setPuffState(state.ordinal()))
                    .build();

    public PufferfishPropertyCollection()
    {
        register(PUFFERFISH_STATE);
    }

    public enum PufferfishState
    {
        SMALL,
        MID,
        LARGE
    }
}
