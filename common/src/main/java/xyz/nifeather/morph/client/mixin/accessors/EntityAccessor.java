package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    void setWasTouchingWater(boolean newVal);

    @Accessor
    SynchedEntityData getEntityData();
}
