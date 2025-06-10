package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;

@Mixin(PlayerRenderer.class)
public class PlayerEntityRendererMixin
{
    @Inject(
            method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V",
            at = @At("RETURN")
    )
    public void onUpdateRenderState(AbstractClientPlayer abstractClientPlayerEntity, PlayerRenderState playerEntityRenderState, float f, CallbackInfo ci)
    {
        if (abstractClientPlayerEntity instanceof MorphLocalPlayer localPlayer
                && !localPlayer.shouldShowName())
        {
            playerEntityRenderState.nameTag = null;
        }
    }
}
