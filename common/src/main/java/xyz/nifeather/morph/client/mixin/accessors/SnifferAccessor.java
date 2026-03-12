package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.animal.sniffer.Sniffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sniffer.class)
public interface SnifferAccessor
{
    @Invoker
    public Sniffer callSetState(Sniffer.State state);
}
