package xyz.nifeather.morph.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.graphics.PlayerRenderHelper;

import java.util.List;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin
{
    @Shadow @Final private RenderBuffers renderBuffers;

    @Inject(
            method = "renderEntities",
            at = @At("TAIL")
    )
    private void onRenderEntities(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource,
                                  Camera camera, DeltaTracker deltaTracker, List<Entity> list, CallbackInfo ci)
    {
        var featherMorph$vertex = this.renderBuffers.bufferSource();
        PlayerRenderHelper.instance().renderCrystalBeam(deltaTracker, poseStack, featherMorph$vertex, LightTexture.FULL_BRIGHT);
    }
}
