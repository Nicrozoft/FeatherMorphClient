package xyz.nifeather.morph.client.syncers.animations.impl;

import xyz.nifeather.morph.shared.AnimationNames;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Shulker;
import xyz.nifeather.morph.client.mixin.accessors.ShulkerEntityAccessor;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;

public class ShulkerAnimationHandler extends AnimationHandler
{
    @Override
    public void play(Entity entity, String animationId)
    {
        if (!(entity instanceof Shulker shulker))
            throw new IllegalArgumentException("Entity not a Shulker!");

        var asAccessor = (ShulkerEntityAccessor) shulker;

        switch (animationId)
        {
            case AnimationNames.PEEK_START -> asAccessor.callSetRawPeekAmount(30);
            case AnimationNames.OPEN_START -> asAccessor.callSetRawPeekAmount(100);
            case AnimationNames.PEEK_STOP, AnimationNames.OPEN_STOP -> asAccessor.callSetRawPeekAmount(0);
        }
    }
}
