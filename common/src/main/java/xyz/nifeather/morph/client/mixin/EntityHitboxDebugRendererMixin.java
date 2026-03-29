package xyz.nifeather.morph.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;

@Mixin(EntityHitboxDebugRenderer.class)
public abstract class EntityHitboxDebugRendererMixin
{
    @Inject(method = "showHitboxes", at = @At("HEAD"), cancellable = true)
    private void morphclient$onShowHitboxes(CallbackInfo ci, @Local(name = "entity") Entity entity)
    {
        if (entity instanceof IMorphClientEntity iMorphClientEntity && iMorphClientEntity.featherMorph$isDisguiseEntity())
            ci.cancel();
    }
}
