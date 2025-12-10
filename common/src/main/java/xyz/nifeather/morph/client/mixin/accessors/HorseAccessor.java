package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Horse.class)
public interface HorseAccessor
{
    @Invoker
    public void callSetVariantAndMarkings(Variant variant, Markings markings);
}
