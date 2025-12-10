package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.parrot.Parrot;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class ParrotAnimationHandler extends AnimationHandler
{
    private static final BlockPos bPos = new BlockPos(0, 0, 0);

    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Parrot parrot))
            throw new IllegalArgumentException("Entity not a Parrot!");

        switch (animationId)
        {
            case AnimationNames.DANCE_START -> parrot.setRecordPlayingNearby(bPos, true);
            case AnimationNames.STOP -> parrot.setRecordPlayingNearby(bPos, false);
        }
    }
}
