package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class PufferfishAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Pufferfish pufferfish))
            throw new IllegalArgumentException("Entity not a Pufferfish!");

        switch (animationId)
        {
            case AnimationNames.INFLATE -> pufferfish.setPuffState(Pufferfish.STATE_FULL);
            case AnimationNames.DEFLATE -> pufferfish.setPuffState(Pufferfish.STATE_SMALL);
        }
    }
}
