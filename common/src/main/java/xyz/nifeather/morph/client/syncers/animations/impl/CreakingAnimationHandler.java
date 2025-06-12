package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class CreakingAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Creaking creaking))
            throw new IllegalArgumentException("Entity not a Creaking!");

        if (animationId.equals(AnimationNames.MAKE_ACTIVE))
            creaking.setIsActive(true);

        switch (animationId)
        {
            case AnimationNames.MAKE_ACTIVE -> creaking.setIsActive(true);
            case AnimationNames.MAKE_INACTIVE -> creaking.setIsActive(false);
        }
    }
}
