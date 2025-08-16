package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Parrot.class)
public interface ParrotAccessor
{
    @Invoker
    public void callSetVariant(Parrot.Variant variant);
}
