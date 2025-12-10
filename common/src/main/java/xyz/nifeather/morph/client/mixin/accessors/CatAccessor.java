package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Cat.class)
public interface CatAccessor
{
    @Invoker
    public void callSetVariant(Holder<CatVariant> variant);

    @Invoker
    public void callSetCollarColor(DyeColor dyeColor);
}
