package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.frog.Frog;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class FrogAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Frog frog))
            throw new IllegalArgumentException("Entity not a Frog!");

        var mixinFrog = (IMorphClientEntity) frog;

        switch (animationId)
        {
            case AnimationNames.EAT -> mixinFrog.featherMorph$overridePose(Pose.USING_TONGUE);
            case AnimationNames.RESET -> mixinFrog.featherMorph$overridePose(null);
        }
    }
}
