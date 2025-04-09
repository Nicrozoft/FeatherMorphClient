package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class WolfAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Wolf wolf))
            throw new IllegalArgumentException("Entity not a Wolf!");

        switch (animationId)
        {
            case AnimationNames.SIT -> wolf.setInSittingPose(true);
            case AnimationNames.STANDUP -> wolf.setInSittingPose(false);
        }
    }
}
