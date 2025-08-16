package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TropicalFish.class)
public interface TropicalFishAccessor
{
    @Invoker
    public void callSetPattern(TropicalFish.Pattern pattern);

    @Invoker
    public void callSetBaseColor(DyeColor color);

    @Invoker
    public void callSetPatternColor(DyeColor color);
}
