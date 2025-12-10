package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.equine.Llama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Llama.class)
public interface LlamaAccessor
{
    @Invoker
    public void callSetVariant(Llama.Variant variant);
}
