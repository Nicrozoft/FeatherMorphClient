package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.monster.Shulker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Shulker.class)
public interface ShulkerEntityAccessor
{
    @Invoker
    public void callSetRawPeekAmount(int peek);
}
