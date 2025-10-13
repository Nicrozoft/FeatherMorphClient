package xyz.nifeather.morph.client.entities;

import net.minecraft.core.BlockPos;

public interface IMorphLivingEntity
{
    void morphclient$setOverrideArrowCount(int override);

    void morphclient$overrideSleepingPos(BlockPos blockPos);
}
