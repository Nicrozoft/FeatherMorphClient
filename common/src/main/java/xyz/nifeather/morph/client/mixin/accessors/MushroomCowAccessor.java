package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.cow.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor
{
    @Invoker
    public void callSetVariant(MushroomCow.Variant variant);
}
