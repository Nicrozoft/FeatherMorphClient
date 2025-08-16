package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Fox.class)
public interface FoxAccessor
{
    @Invoker
    public void callSetVariant(Fox.Variant variant);
}
