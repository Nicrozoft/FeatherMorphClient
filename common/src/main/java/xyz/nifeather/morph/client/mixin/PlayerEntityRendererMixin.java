package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;

@Mixin(AvatarRenderer.class)
public class PlayerEntityRendererMixin
{
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("RETURN")
    )
    public void onUpdateRenderState(Avatar avatar, AvatarRenderState playerEntityRenderState, float f, CallbackInfo ci)
    {
        if (avatar instanceof MorphLocalPlayer localPlayer
                && !localPlayer.shouldShowName())
        {
            playerEntityRenderState.nameTag = null;
        }
    }
}
