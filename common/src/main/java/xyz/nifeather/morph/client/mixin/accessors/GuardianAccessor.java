package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Guardian.class)
public interface GuardianAccessor
{
    @Invoker
    public void callSetActiveAttackTarget(int entityID);
}
