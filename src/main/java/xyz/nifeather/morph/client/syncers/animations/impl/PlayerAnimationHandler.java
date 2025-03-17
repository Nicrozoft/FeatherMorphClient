package xyz.nifeather.morph.client.syncers.animations.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import xyz.nifeather.morph.client.AnimationNames;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class PlayerAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof MorphLocalPlayer))
            throw new IllegalArgumentException("Entity not a Local Player!");

        if (!(entity instanceof IMorphClientEntity asMorphClientEntity))
            throw new IllegalArgumentException("The LocalPlayer is not a IMorphClientEntity!");

        switch (animationId)
        {
            case AnimationNames.LAY -> asMorphClientEntity.featherMorph$overridePose(EntityPose.SLEEPING);
            case AnimationNames.CRAWL -> asMorphClientEntity.featherMorph$overridePose(EntityPose.SWIMMING);
            case AnimationNames.STANDUP -> asMorphClientEntity.featherMorph$overridePose(null);
        }
    }
}
