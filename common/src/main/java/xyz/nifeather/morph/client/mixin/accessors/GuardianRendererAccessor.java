package xyz.nifeather.morph.client.mixin.accessors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuardianRenderer.class)
public interface GuardianRendererAccessor
{
    @Invoker
    public void callRenderBeam(PoseStack poseStack, VertexConsumer vertexConsumer, Vec3 vec3, float f, float g, float h);
}
