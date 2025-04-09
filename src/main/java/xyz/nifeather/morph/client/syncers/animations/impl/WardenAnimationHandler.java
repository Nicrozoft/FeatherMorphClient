package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.warden.Warden;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class WardenAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Warden warden))
            throw new IllegalArgumentException("Entity not a Warden!");

        var mixinWarden = (IMorphClientEntity) warden;

        switch (animationId)
        {
            case AnimationNames.ROAR ->
            {
                mixinWarden.featherMorph$overridePose(Pose.ROARING);
                mixinWarden.featherMorph$setNoAcceptSetPose(true);
            }
            case AnimationNames.SNIFF ->
            {
                mixinWarden.featherMorph$overridePose(Pose.SNIFFING);
                mixinWarden.featherMorph$setNoAcceptSetPose(true);
            }
            case AnimationNames.DIGDOWN ->
            {
                mixinWarden.featherMorph$overridePose(Pose.DIGGING);
                mixinWarden.featherMorph$setNoAcceptSetPose(true);
            }
            case AnimationNames.VANISH -> mixinWarden.featherMorph$overrideInvisibility(true);
            case AnimationNames.APPEAR ->
            {
                mixinWarden.featherMorph$overrideInvisibility(false);
                warden.diggingAnimationState.stop();

                mixinWarden.featherMorph$setNoAcceptSetPose(false);
                mixinWarden.featherMorph$overridePose(Pose.EMERGING);
                mixinWarden.featherMorph$setNoAcceptSetPose(true);
            }
            case AnimationNames.TRY_RESET, AnimationNames.RESET ->
            {
                mixinWarden.featherMorph$overridePose(null);
                mixinWarden.featherMorph$setNoAcceptSetPose(false);
            }
        }
    }
}
