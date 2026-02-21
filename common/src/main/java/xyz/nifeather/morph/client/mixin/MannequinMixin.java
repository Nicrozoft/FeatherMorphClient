package xyz.nifeather.morph.client.mixin;

import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Mannequin.class)
public class MannequinMixin extends Avatar
{
    protected MannequinMixin(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();
        updateSwingTime();
    }
}
