package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.rabbit.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Rabbit.class)
public interface RabbitAccessor
{
    @Invoker
    public void callSetVariant(Rabbit.Variant variant);
}
