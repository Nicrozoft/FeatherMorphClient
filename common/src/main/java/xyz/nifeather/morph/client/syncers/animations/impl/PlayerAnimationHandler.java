package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.client.entities.IMorphLivingEntity;
import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class PlayerAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof IMorphClientEntity asMorphClientEntity))
            throw new IllegalArgumentException("The LocalPlayer is not a IMorphClientEntity!");

        if (!(entity instanceof IMorphLivingEntity asMorphLiving))
            throw new IllegalArgumentException("Given entity is not an instance of IMorphLivingEntity");

        asMorphLiving.morphclient$overrideSleepingPos(null);

        switch (animationId)
        {
            case AnimationNames.LAY ->
            {
                // This will need to change when animation support comes for fabric multiplayer
                var blockPos = Minecraft.getInstance().player.blockPosition();

                // Only set BedPos when we're on a bed
                BlockState blockState = entity.level().getBlockState(blockPos);
                if (blockState.getBlock() instanceof BedBlock)
                    asMorphLiving.morphclient$overrideSleepingPos(blockPos);

                asMorphClientEntity.featherMorph$overridePose(Pose.SLEEPING);
            }

            case AnimationNames.CRAWL -> asMorphClientEntity.featherMorph$overridePose(Pose.SWIMMING);
            case AnimationNames.STANDUP -> asMorphClientEntity.featherMorph$overridePose(null);
        }
    }
}
