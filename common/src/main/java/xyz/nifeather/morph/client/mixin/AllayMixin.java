package xyz.nifeather.morph.client.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nifeather.morph.client.entities.IAllay;

@Mixin(Allay.class)
public abstract class AllayMixin extends Entity implements IAllay
{
    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_DANCING;

    public AllayMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public void morphclient$forceSetDancing(boolean dancing)
    {
        this.entityData.set(DATA_DANCING, dancing);
    }
}