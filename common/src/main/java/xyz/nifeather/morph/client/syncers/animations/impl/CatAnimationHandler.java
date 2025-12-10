package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Cat;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class CatAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Cat cat))
            throw new IllegalArgumentException("Entity not a Cat!");

        switch (animationId)
        {
            case AnimationNames.LAY_START -> cat.setLying(true);
            case AnimationNames.STANDUP ->
            {
                cat.setLying(false);
                cat.setOrderedToSit(false);
                cat.setInSittingPose(false);
            }
            case AnimationNames.SIT ->
            {
                cat.setOrderedToSit(true);
                cat.setInSittingPose(true);
            }
        }
    }
}
