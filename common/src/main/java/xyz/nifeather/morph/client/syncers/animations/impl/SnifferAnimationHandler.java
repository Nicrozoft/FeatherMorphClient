package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class SnifferAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Sniffer sniffer))
            throw new IllegalArgumentException("Entity not a Sniffer!");

        switch (animationId)
        {
            case AnimationNames.SNIFF ->
            {
                sniffer.transitionTo(Sniffer.State.IDLING);
                sniffer.transitionTo(Sniffer.State.SNIFFING);
            }
        }
    }
}
