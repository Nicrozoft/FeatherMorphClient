package xyz.nifeather.morph.client.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.animal.goat.Goat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.nifeather.morph.client.entities.IMorphGoat;
import xyz.nifeather.morph.client.mixin.accessors.EntityAccessor;

@Mixin(Goat.class)
public class GoatMixin implements IMorphGoat
{
    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_HAS_LEFT_HORN;

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_HAS_RIGHT_HORN;

    @Unique
    @Override
    public void morphclient$setHasLeftHorn(boolean value)
    {
        ((EntityAccessor)this).getEntityData().set(DATA_HAS_LEFT_HORN, value);
    }

    @Unique
    @Override
    public void morphclient$setHasRightHorn(boolean value)
    {
        ((EntityAccessor)this).getEntityData().set(DATA_HAS_RIGHT_HORN, value);
    }
}
