package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Axolotl.class)
public interface AxolotlAccessor
{
    @Invoker
    public void callSetVariant(Axolotl.Variant variant);
}
