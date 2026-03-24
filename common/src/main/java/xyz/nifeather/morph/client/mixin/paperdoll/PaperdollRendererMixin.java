package xyz.nifeather.morph.client.mixin.paperdoll;

import dev.tr7zw.paperdoll.PaperDollRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;

@Mixin(PaperDollRenderer.class)
public class PaperdollRendererMixin
{
    @ModifyVariable(
            method = "render",
            at = @At("STORE"),
            name = "playerEntity")
    public Entity morphclient$modifyEntity(Entity value)
    {
        var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(value);
        if (syncer == null || !syncer.getDisguiseInstance().isAlive()) return value;

        return syncer.getDisguiseInstance();
    }
}
