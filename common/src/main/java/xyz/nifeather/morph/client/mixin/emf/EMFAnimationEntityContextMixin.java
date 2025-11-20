package xyz.nifeather.morph.client.mixin.emf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;

@Mixin(EMFAnimationEntityContext.class)
public abstract class EMFAnimationEntityContextMixin
{
    @Inject(method = "getLODFactorOfEntity", remap = false, at = @At(value = "INVOKE",
            target = "Ltraben/entity_model_features/models/animation/EMFAnimationEntityContext;emfEntity()Ltraben/entity_model_features/utils/EMFEntity;"),
            cancellable = true
    )
    private static void morphclient$modifyLODFactor(CallbackInfoReturnable<Integer> cir)
    {
        if (EMFAnimationEntityContext.getEMFEntity() instanceof IMorphClientEntity morphClientEntity)
        {
            if (morphClientEntity.featherMorph$isDisguiseEntity())
                cir.setReturnValue(0);
        }
    }
}
