package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface LimbAnimatorAccessor
{
    @Accessor
    public void setSpeedOld(float prevSpd);

    @Accessor
    public void setSpeed(float spd);

    @Accessor
    public void setPosition(float pos);

    @Accessor
    public float getSpeedOld();
}
