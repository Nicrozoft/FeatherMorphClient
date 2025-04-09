package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.ServerHandler;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin
{
    @Shadow
    @Nullable
    public ClientInput input;

    @Unique
    @Nullable
    private Boolean morphclient$inputLastValue;

    @Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
    private void onSneakingCall(CallbackInfoReturnable<Boolean> cir)
    {
        var serverSideSneaking = ServerHandler.serverSideSneaking;

        //如果input的下蹲状态发生变化，则重置服务器状态并返回input的当前状态
        if (input != null && (morphclient$inputLastValue == null || input.keyPresses.shift() != morphclient$inputLastValue))
        {
            morphclient$inputLastValue = input.keyPresses.shift();

            cir.setReturnValue(input.keyPresses.shift());
            ServerHandler.serverSideSneaking = serverSideSneaking = null;
            return;
        }

        //否则返回服务器状态
        if (serverSideSneaking != null)
            cir.setReturnValue(serverSideSneaking);
    }
}
