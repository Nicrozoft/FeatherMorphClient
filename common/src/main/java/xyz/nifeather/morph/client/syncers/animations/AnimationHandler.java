package xyz.nifeather.morph.client.syncers.animations;

import net.minecraft.world.entity.Entity;

public abstract class AnimationHandler
{
    public abstract void play(Entity entity, String animationId);
}
