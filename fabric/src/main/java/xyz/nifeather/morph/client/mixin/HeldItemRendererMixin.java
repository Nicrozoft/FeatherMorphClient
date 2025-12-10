package xyz.nifeather.morph.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.graphics.PlayerRenderHelper;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin
{
    @Unique
    private static final PlayerRenderHelper morphclient$rendererHelper = PlayerRenderHelper.instance();
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(
            method = "renderPlayerArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V")
    )
    private void morphclient$renderArmHoldingItem_left(AvatarRenderer<?> instance, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, Identifier resourceLocation, boolean bl)
    {
        if (morphclient$canRender())
            morphclient$renderLeftArm(poseStack, submitNodeCollector, light);
        else
            instance.renderLeftHand(poseStack, submitNodeCollector, light, resourceLocation, bl);
    }

    @Redirect(
            method = "renderPlayerArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V")
    )
    private void morphclient$renderArmHoldingItem_right(AvatarRenderer instance, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, Identifier resourceLocation, boolean bl)
    {
        if (morphclient$canRender())
            morphclient$renderRightArm(poseStack, submitNodeCollector, light);
        else
            instance.renderRightHand(poseStack, submitNodeCollector, light, resourceLocation, bl);
    }

    @Redirect(
            method = "renderMapHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V")
    )
    private void morphclient$renderArmHoldingItem_right_alt(AvatarRenderer instance, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, Identifier resourceLocation, boolean bl)
    {
        if (morphclient$canRender())
            morphclient$renderRightArm(poseStack, submitNodeCollector, light);
        else
            instance.renderRightHand(poseStack, submitNodeCollector, light, resourceLocation, bl);
    }

    @Redirect(
            method = "renderMapHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V")
    )
    private void morphclient$renderArmHoldingItem_left_alt(AvatarRenderer instance, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, Identifier resourceLocation, boolean bl)
    {
        if (morphclient$canRender())
            morphclient$renderLeftArm(poseStack, submitNodeCollector, light);
        else
            instance.renderLeftHand(poseStack, submitNodeCollector, light, resourceLocation, bl);
    }

    @Unique
    private void morphclient$renderLeftArm(PoseStack matrices, SubmitNodeCollector submitNodeCollector, int light)
    {
        morphclient$rendererHelper.renderingLeftPart = true;

        this.morphclient$onArmRender(matrices, submitNodeCollector, light);
    }

    @Unique
    private void morphclient$renderRightArm(PoseStack matrices, SubmitNodeCollector submitNodeCollector, int light)
    {
        morphclient$rendererHelper.renderingLeftPart = false;

        this.morphclient$onArmRender(matrices, submitNodeCollector, light);
    }

    @Unique
    private boolean morphclient$canRender()
    {
        var clientSyncer = ClientDisguiseSyncer.getCurrentInstance();
        return clientSyncer != null && !clientSyncer.disposed() && FeatherMorphClientBootstrap.getInstance().getModConfigData().clientViewVisible();
    }

    @Unique
    private void morphclient$onArmRender(PoseStack matrices, SubmitNodeCollector submitNodeCollector,
                                         int light)
    {
        morphclient$rendererHelper.onArmDrawCall(
                matrices, submitNodeCollector,
                light
        );
    }
}
