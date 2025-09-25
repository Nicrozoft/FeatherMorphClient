package xyz.nifeather.morph.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.graphics.EntityRendererHelper;
import xyz.nifeather.morph.client.graphics.PlayerRenderHelper;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin
{
    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;

    @Inject(method = "extractEntity", at = @At("HEAD"), cancellable = true)
    public void morphclient$overrideEntityRenderState(Entity entity, float f, CallbackInfoReturnable<EntityRenderState> cir)
    {
        if (PlayerRenderHelper.instance().skipRender)
            return;

        if (!(entity instanceof IMorphClientEntity iMorphClientEntity))
            return;

        if (iMorphClientEntity.featherMorph$bypassesDispatcherRedirect())
            return;

        var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(entity);
        if (syncer == null) return;

        var disguiseInstance = syncer.getDisguiseInstance();
        if (disguiseInstance == null) return;

        syncer.onRenderSetup();
        var state = entityRenderDispatcher.extractEntity(disguiseInstance, f);
        syncer.onEarlyEntityRender(state);

        cir.setReturnValue(state);
    }

    @Inject(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    public void morphclient$postEntitySubmit(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, CallbackInfo ci,
                                       @Local EntityRenderState entityRenderState)
    {
        if (entityRenderState instanceof IDisguiseRenderState asDisguiseRenderState)
        {
            var syncer = asDisguiseRenderState.morphclient$getDisguiseSyncer();

            if (syncer != null)
                syncer.postEntityRender();
        }
    }

    @Inject(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V", shift = At.Shift.AFTER))
    public void morphclient$postSubmitEntities(GpuBufferSlice gpuBufferSlice, LevelRenderState levelRenderState,
                                               ProfilerFiller profilerFiller, Matrix4f matrix4f, ResourceHandle resourceHandle,
                                               ResourceHandle resourceHandle2, boolean bl, Frustum frustum, ResourceHandle resourceHandle3,
                                               ResourceHandle resourceHandle4, CallbackInfo ci,
                                               @Local PoseStack poseStack)
    {
        profilerFiller.popPush("morphclient_render_tag");
        EntityRendererHelper.instance.submitRevealNames(poseStack, this.submitNodeStorage, levelRenderState.cameraRenderState);
    }
}
