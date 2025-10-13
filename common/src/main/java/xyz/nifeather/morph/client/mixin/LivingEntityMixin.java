package xyz.nifeather.morph.client.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.EntityTickHandler;
import xyz.nifeather.morph.client.entities.IMorphLivingEntity;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements IMorphLivingEntity
{
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void featherMorph$onTick(CallbackInfo ci)
    {
        if (((LivingEntity) (Object) this).level().isClientSide())
            EntityTickHandler.cancelIfIsDisguiseAndNotSyncing(ci, this);
    }

    @Unique
    private int morphclient$overrideArrowCount = -1;

    @Unique
    @Override
    public void morphclient$setOverrideArrowCount(int override)
    {
        morphclient$overrideArrowCount = override;
    }

    @Inject(method = "getArrowCount", at = @At("HEAD"), cancellable = true)
    private void morphclient$onGetArrowCount(CallbackInfoReturnable<Integer> cir)
    {
        if (morphclient$overrideArrowCount > 0)
            cir.setReturnValue(morphclient$overrideArrowCount);
    }

    @Unique
    private BlockPos morphclient$sleepingPos = null;

    @Override
    public void morphclient$overrideSleepingPos(@Nullable BlockPos blockPos)
    {
        this.morphclient$sleepingPos = blockPos;
    }

    @Inject(method = "getSleepingPos", at = @At("HEAD"), cancellable = true)
    private void morphclient$onGetSleepingPos(CallbackInfoReturnable<Optional<BlockPos>> cir)
    {
        if (morphclient$sleepingPos != null)
            cir.setReturnValue(Optional.of(morphclient$sleepingPos));
    }
}
