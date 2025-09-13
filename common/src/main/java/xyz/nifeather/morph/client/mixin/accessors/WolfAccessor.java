package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Wolf.class)
public interface WolfAccessor
{
    @Invoker
    public void callSetVariant(Holder<WolfVariant> variant);

    @Invoker
    public void callSetCollarColor(DyeColor dyeColor);
}
