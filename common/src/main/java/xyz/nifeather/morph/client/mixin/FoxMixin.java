package xyz.nifeather.morph.client.mixin;

import net.minecraft.world.entity.animal.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nifeather.morph.client.entities.IFox;

@Mixin(Fox.class)
public abstract class FoxMixin implements IFox
{
    @Shadow abstract void setSleeping(boolean sleeping);

    @Override
    public void morphclient$forceSetSleeping(boolean sleeping)
    {
        this.setSleeping(sleeping);
    }
}
