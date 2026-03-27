package xyz.nifeather.morph.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;
import xyz.nifeather.morph.client.graphics.EntityRendererHelper;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin
{
    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;

    @Inject(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    public void morphclient$postEntitySubmit(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, CallbackInfo ci,
                                       @Local(name = "state") EntityRenderState entityRenderState)
    {
        var profilerFiller = Profiler.get();
        profilerFiller.push("morphclient_post_submit");

        if (entityRenderState instanceof IDisguiseRenderState)
        {
            profilerFiller.push("submit_reveal_name");
            EntityRendererHelper.instance.submitRevealName(poseStack, entityRenderState, this.submitNodeStorage, levelRenderState.cameraRenderState);
            profilerFiller.pop();
        }

        profilerFiller.pop();
    }
}
