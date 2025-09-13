package xyz.nifeather.morph.client.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.entities.IHasOverrideGlowing;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements IMorphClientEntity, IHasOverrideGlowing
{
    @Unique
    private boolean featherMorph$isDisguiseEntity;

    @Unique
    private int featherMorph$masterId = -1;

    @Override
    public void featherMorph$setIsDisguiseEntity(int masterId)
    {
        this.featherMorph$masterId = masterId;
        this.featherMorph$isDisguiseEntity = true;
    }

    @Override
    public boolean featherMorph$isDisguiseEntity()
    {
        return this.featherMorph$isDisguiseEntity;
    }

    @Override
    public int featherMorph$getMasterEntityId()
    {
        return this.featherMorph$masterId;
    }

    @Unique
    private boolean morphclient$overrideGlowing = false;

    @Override
    public void morphclient$overrideClientGlowing(boolean glowing)
    {
        morphclient$overrideGlowing = glowing;
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    public void morphclient$overrideGlowing(CallbackInfoReturnable<Boolean> cir)
    {
        if (morphclient$overrideGlowing)
            cir.setReturnValue(true);
    }
}