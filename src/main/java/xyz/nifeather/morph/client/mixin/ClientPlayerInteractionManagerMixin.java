package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin
{
    /*
    @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    private void feathermorph$onGetReachDistance(CallbackInfoReturnable<Float> cir)
    {
        if (ServerHandler.reach > 0)
            cir.setReturnValue(ServerHandler.reach);
    }
    */
}
