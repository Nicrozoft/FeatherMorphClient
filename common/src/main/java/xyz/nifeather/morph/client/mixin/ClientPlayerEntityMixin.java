package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.entities.IMorphLocalPlayer;
import xyz.nifeather.morph.client.network.commands.frog.S2CEntityAnimateCommand;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin implements IMorphLocalPlayer
{
    @Shadow
    @Nullable
    public ClientInput input;

    //@Shadow private boolean wasShiftKeyDown;

    @Unique
    @Nullable
    private Boolean morphclient$inputLastValue;

    @Unique
    @Nullable
    private Boolean morphclient$serverSneaking;

    @Inject(method = "swing", at = @At("HEAD"))
    public void onSwing(InteractionHand interactionHand, CallbackInfo ci)
    {
        var asEntity = (LocalPlayer)(Object)this;
        var tracker = DisguiseInstanceTracker.getInstance().getSyncerFor(asEntity);

        String animateName = interactionHand == InteractionHand.MAIN_HAND
                ? S2CEntityAnimateCommand.ANIM_SWING_MAINHAND
                : S2CEntityAnimateCommand.ANIM_SWING_OFFHAND;

        if (tracker != null
                && tracker.getDisguiseInstance() instanceof LivingEntity living
                && !tracker.isEntityAnimationMasked(animateName))
        {
            living.swing(interactionHand);
        }
    }

    @Inject(method = "isShiftKeyDown", at = @At("HEAD"), cancellable = true)
    private void onSneakingCall(CallbackInfoReturnable<Boolean> cir)
    {
        var serverSideSneaking = morphclient$serverSneaking;

        //如果input的下蹲状态发生变化，则重置服务器状态并返回input的当前状态
        if (input != null && (morphclient$inputLastValue == null || input.keyPresses.shift() != morphclient$inputLastValue))
        {
            morphclient$inputLastValue = input.keyPresses.shift();

            cir.setReturnValue(input.keyPresses.shift());
            morphclient$serverSneaking = null;
            return;
        }

        //否则返回服务器状态
        if (serverSideSneaking != null)
            cir.setReturnValue(serverSideSneaking);
    }

/*
    @Inject(
            method = "sendShiftKeyState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V")
    )
    public void aa(CallbackInfo ci)
    {
        FeatherMorphClient.LOGGER.info("SendShift!");
    }
*/

    @Override
    public void morphclient$overrideSneaking(boolean sneaking)
    {
        morphclient$serverSneaking = sneaking;
        //this.wasShiftKeyDown = sneaking;
    }
}