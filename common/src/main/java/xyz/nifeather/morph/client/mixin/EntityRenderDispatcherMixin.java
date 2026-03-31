package xyz.nifeather.morph.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import xyz.nifeather.morph.client.graphics.PlayerRenderHelper;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin
{
    @WrapMethod(method = "extractEntity")
    public EntityRenderState morphclient$overrideRender(Entity entity, float partialTicks, Operation<EntityRenderState> original)
    {
        return PlayerRenderHelper.instance().useDisguiseRenderStateIfPossible(entity, partialTicks, original);
    }
}
