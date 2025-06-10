package xyz.nifeather.morph.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.graphics.EntityRendererHelper;
import xyz.nifeather.morph.client.graphics.PlayerRenderHelper;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
    @Shadow @Final private Font font;

    @ModifyVariable(
            method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;getRenderer(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;",
                    shift = At.Shift.AFTER
            ),
            index = 11,
            argsOnly = true
    )
    private int morphclient$overrideLight(int value, @Local(argsOnly = true) Entity entity)
    {
        if (!(entity instanceof IMorphClientEntity iMorphClientEntity))
            return value;

        if (!iMorphClientEntity.featherMorph$isDisguiseEntity())
            return value;

        var type = entity.getType();

        if (type == EntityType.ALLAY
                || type == EntityType.BLAZE
                || type == EntityType.MAGMA_CUBE
                || type == EntityType.VEX)
        {
            return LightTexture.FULL_BRIGHT;
        }

        return value;
    }

    @ModifyVariable(
            method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            index = 1,
            argsOnly = true,
            order = 1100)
    public Entity morphclient$modifyEntityToRender(Entity source)
    {
        if (PlayerRenderHelper.instance().skipRender)
            return source;

        if (!(source instanceof IMorphClientEntity iMorphClientEntity))
            return source;

        if (iMorphClientEntity.featherMorph$bypassesDispatcherRedirect())
            return source;

        var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(source);
        if (syncer == null) return source;

        //syncer.onEarlyEntityRender();

        var morphclient$instance = syncer.getDisguiseInstance();
        return morphclient$instance == null ? source : morphclient$instance;
    }

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(
                    value = "HEAD"
            )
    )
    public <S extends EntityRenderState>  void onRenderBegin(S state, double x, double y, double z, PoseStack matrices, MultiBufferSource vertexConsumers, int light, EntityRenderer<?, S> renderer, CallbackInfo ci)
    {
        if (state instanceof IDisguiseRenderState asDisguiseRenderState)
        {
            var syncer = asDisguiseRenderState.morphclient$getDisguiseSyncer();

            if (syncer != null)
                syncer.onEarlyEntityRender();
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(
                    value = "TAIL"
            )
    )
    public <S extends EntityRenderState>  void onRenderEnd(S state, double x, double y, double z, PoseStack matrices, MultiBufferSource vertexConsumers, int light, EntityRenderer<?, S> renderer, CallbackInfo ci)
    {
        if (state instanceof IDisguiseRenderState asDisguiseRenderState)
        {
            var syncer = asDisguiseRenderState.morphclient$getDisguiseSyncer();

            if (syncer != null)
                syncer.postEntityRender();
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
    )
    public <S extends EntityRenderState> void morphclient$tryRenderRevealName(S entityRenderState,
                                                                              double x, double y, double z,
                                                                              PoseStack matrices, MultiBufferSource vertexConsumerProvider,
                                                                              int light,
                                                                              EntityRenderer<?, S> entityRenderer,
                                                                              CallbackInfo ci)
    {
        EntityRendererHelper.instance.renderRevealNameIfPossible((EntityRenderDispatcher)(Object) this, entityRenderState, font, matrices, vertexConsumerProvider);
    }
}