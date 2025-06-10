package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandEntityAccessor
{
    @Invoker
    void callSetShowArms(boolean showArms);
}
