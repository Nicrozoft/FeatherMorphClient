package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderDragonRenderer.class)
public interface DragonEntityRendererAccessor
{
    @Accessor
    EnderDragonModel getModel();
}
