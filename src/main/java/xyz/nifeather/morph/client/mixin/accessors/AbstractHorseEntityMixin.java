package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractHorse.class)
public interface AbstractHorseEntityMixin
{
    @Invoker
    void callSetFlag(int bitmask, boolean flag);

    @Accessor
    public SimpleContainer getInventory();
}
