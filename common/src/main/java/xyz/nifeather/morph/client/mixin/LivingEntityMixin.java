package xyz.nifeather.morph.client.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.EntityTickHandler;

@Mixin(value = LivingEntity.class, priority = 9999)
public class LivingEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void featherMorph$onTick(CallbackInfo ci) {
        if (((LivingEntity) (Object) this).level().isClientSide())
            EntityTickHandler.cancelIfIsDisguiseAndNotSyncing(ci, this);
    }
}
