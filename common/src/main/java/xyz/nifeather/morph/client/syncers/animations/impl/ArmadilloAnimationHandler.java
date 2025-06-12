package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class ArmadilloAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Armadillo armadillo))
            throw new IllegalArgumentException("Entity not an Armadillo!");

        switch (animationId)
        {
            case AnimationNames.PANIC_ROLLING -> armadillo.switchToState(Armadillo.ArmadilloState.ROLLING);
            case AnimationNames.PANIC_SCARED -> armadillo.switchToState(Armadillo.ArmadilloState.SCARED);
            case AnimationNames.PANIC_UNROLLING -> armadillo.switchToState(Armadillo.ArmadilloState.UNROLLING);
            case AnimationNames.PANIC_IDLE -> armadillo.switchToState(Armadillo.ArmadilloState.IDLE);
        }
    }
}
