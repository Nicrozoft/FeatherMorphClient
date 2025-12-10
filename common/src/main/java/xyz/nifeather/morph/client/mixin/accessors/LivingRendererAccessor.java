package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingRendererAccessor
{
    @Invoker
    public RenderType callGetRenderType(LivingEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline);
}
