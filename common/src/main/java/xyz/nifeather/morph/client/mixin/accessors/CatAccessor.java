package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Cat.class)
public interface CatAccessor
{
    @Invoker
    public void callSetVariant(Holder<CatVariant> variant);
}
