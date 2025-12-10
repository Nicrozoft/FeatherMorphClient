package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.panda.Panda;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class PandaAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if(!(entity instanceof Panda panda))
            throw new IllegalArgumentException("Entity not a Panda!");

        switch (animationId)
        {
            case AnimationNames.SIT -> panda.sit(true);
            case AnimationNames.STANDUP -> panda.sit(false);
        }
    }
}
