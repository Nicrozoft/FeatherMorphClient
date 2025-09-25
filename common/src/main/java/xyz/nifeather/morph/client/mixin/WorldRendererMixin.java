package xyz.nifeather.morph.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.graphics.PlayerRenderHelper;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin
{
    @Shadow @Final private RenderBuffers renderBuffers;

    @Inject(
            method = "submitEntities",
            at = @At("TAIL")
    )
    private void onRenderEntities(PoseStack poseStack, LevelRenderState levelRenderState,
                                  SubmitNodeCollector submitNodeCollector, CallbackInfo ci)
    {
        var featherMorph$vertex = this.renderBuffers.bufferSource();
        PlayerRenderHelper.instance().submitCrystalBeamIfPossible(Minecraft.getInstance().getDeltaTracker(), poseStack,
                featherMorph$vertex, submitNodeCollector, LightTexture.FULL_BRIGHT);
    }
}
