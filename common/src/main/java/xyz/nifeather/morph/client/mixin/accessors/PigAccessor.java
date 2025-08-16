package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PigVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Pig.class)
public interface PigAccessor
{
    @Invoker
    public void callSetVariant(Holder<PigVariant> variant);
}
