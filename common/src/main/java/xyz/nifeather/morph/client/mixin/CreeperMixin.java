package xyz.nifeather.morph.client.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.nifeather.morph.client.entities.IMorphCreeper;
import xyz.nifeather.morph.client.mixin.accessors.EntityAccessor;

@Mixin(Creeper.class)
public class CreeperMixin implements IMorphCreeper
{
    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    @Unique
    @Override
    public void morphclient$setPowered(boolean powered)
    {
        ((EntityAccessor)this).getEntityData().set(DATA_IS_POWERED, powered);
    }
}
