package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    void setWasTouchingWater(boolean newVal);

    @Accessor
    SynchedEntityData getEntityData();

    @Invoker
    void callSetSharedFlag(int flag, boolean value);
}
