package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class EnderDragonPropertyCollection extends LivingEntityPropertyCollection<EnderDragon>
{
    public final ClientProperty<Integer, EnderDragon> DRAGON_PHASE =
            ClientProperty.builder(PropertyNames.ENDER_DRAGON_DRAGON_PHASE, 0, EnderDragon.class)
                    .inputHandle(CommonInputHandles::intOrEmpty)
                    .entityHandle((dragon, phase) -> dragon.getPhaseManager().setPhase(EnderDragonPhase.getById(phase)))
                    .build();

    public EnderDragonPropertyCollection()
    {
        register(DRAGON_PHASE);
    }
}
