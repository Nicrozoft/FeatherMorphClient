package xyz.nifeather.morph.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;Z)V")
    )
    private void morphclient$renderArmHoldingItem_left(PlayerRenderer instance, PoseStack matrices, MultiBufferSource vertexConsumers, int light, ResourceLocation skinTexture, boolean sleeveVisible)
    {
        if (morphclient$canRender())
            morphclient$renderLeftArm(matrices, vertexConsumers, light);
        else
            instance.renderLeftHand(matrices, vertexConsumers, light, skinTexture, this.minecraft.player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE));
    }

    @Redirect(
            method = "renderPlayerArm",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;Z)V")
    )
    private void morphclient$renderArmHoldingItem_right(PlayerRenderer instance, PoseStack matrices, MultiBufferSource vertexConsumers, int light, ResourceLocation skinTexture, boolean sleeveVisible)
    {
        if (morphclient$canRender())
            morphclient$renderRightArm(matrices, vertexConsumers, light);
        else
            instance.renderRightHand(matrices, vertexConsumers, light, skinTexture, this.minecraft.player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
    }

    @Redirect(
            method = "renderMapHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;Z)V")
    )
    private void morphclient$renderArmHoldingItem_right_alt(PlayerRenderer instance, PoseStack matrices, MultiBufferSource vertexConsumers, int light, ResourceLocation skinTexture, boolean sleeveVisible)
    {
        if (morphclient$canRender())
            morphclient$renderRightArm(matrices, vertexConsumers, light);
        else
            instance.renderRightHand(matrices, vertexConsumers, light, skinTexture, this.minecraft.player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
    }

    @Redirect(
            method = "renderMapHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/resources/ResourceLocation;Z)V")
    )
    private void morphclient$renderArmHoldingItem_left_alt(PlayerRenderer instance, PoseStack matrices, MultiBufferSource vertexConsumers, int light, ResourceLocation skinTexture, boolean sleeveVisible)
    {
        if (morphclient$canRender())
            morphclient$renderLeftArm(matrices, vertexConsumers, light);
        else
            instance.renderLeftHand(matrices, vertexConsumers, light, skinTexture, this.minecraft.player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE));
    }

    @Unique
    private void morphclient$renderLeftArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light)
    {
        morphclient$rendererHelper.renderingLeftPart = true;

        this.morphclient$onArmRender(matrices, vertexConsumers, light);
    }

    @Unique
    private void morphclient$renderRightArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light)
    {
        morphclient$rendererHelper.renderingLeftPart = false;

        this.morphclient$onArmRender(matrices, vertexConsumers, light);
    }

    @Unique
    private boolean morphclient$canRender()
    {
        var clientSyncer = ClientDisguiseSyncer.getCurrentInstance();
        return clientSyncer != null && !clientSyncer.disposed() && FeatherMorphClientBootstrap.getInstance().getModConfigData().clientViewVisible();
    }

    @Unique
    private void morphclient$onArmRender(PoseStack matrices, MultiBufferSource vertexConsumers,
                                         int light)
    {
        morphclient$rendererHelper.onArmDrawCall(
                matrices, vertexConsumers,
                light
        );
    }
}
