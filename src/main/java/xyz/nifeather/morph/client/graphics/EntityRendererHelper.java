package xyz.nifeather.morph.client.graphics;

import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.graphics.color.ColorUtils;
import xyz.nifeather.morph.client.graphics.color.MaterialColors;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityRendererHelper
{
    public EntityRendererHelper()
    {
        instance = this;
    }

    public static EntityRendererHelper instance;

    public static boolean doRenderRealName = false;

    private final int textColor = MaterialColors.Orange500.getColor();
    public final int textColorTransparent = ColorUtils.forOpacity(MaterialColors.Orange500, 0).getColor();

    @Nullable
    public final Map.Entry<Integer, String> getRevealNameEntry(Integer id)
    {
        return DisguiseInstanceTracker.getInstance().playerMap.entrySet().stream()
                .filter(set -> id.equals(set.getKey()))
                .findFirst().orElse(null);
    }

    public final void setupEntityState(Entity renderingEntity, IDisguiseRenderState renderState)
    {
        // Reset render state
        renderState.morphclient$setClientPlayer(false);
        renderState.morphclient$setRevealName(null);
        renderState.morphclient$setMasterPosition(null);
        renderState.morphclient$setDisguiseSyncer(null);

        // then do setup

        int id = renderingEntity.getId();
        Entity masterEntity = null;

        // client renderer
        if (renderingEntity instanceof IMorphClientEntity iMorphEntity && iMorphEntity.featherMorph$isDisguiseEntity())
        {
            var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(iMorphEntity.featherMorph$getMasterEntityId());

            if (syncer != null)
            {
                masterEntity = syncer.getBindingPlayer();
                id = syncer.getBindingPlayer().getId();

                renderState.morphclient$setDisguiseSyncer(syncer);
                renderState.morphclient$setMasterPosition(masterEntity.position());

                syncer.onEntityRenderStateSetup((EntityRenderState)renderState, renderState);
            }
        }

        var entrySet = getRevealNameEntry(id);
        if (entrySet == null)
            return;

        String revealName = entrySet.getValue();
        var disguiseEntityName = renderingEntity.getName().getString();

        String text = "%s(%s)".formatted(disguiseEntityName, revealName);

        renderState.morphclient$setRevealName(text);
        renderState.morphclient$setClientPlayer(renderingEntity == Minecraft.getInstance().player);
    }

    public final void renderRevealNameIfPossible(EntityRenderDispatcher dispatcher,
                                                 EntityRenderState state, Font textRenderer,
                                           PoseStack matrices, MultiBufferSource vertexConsumers)
    {
        if (!doRenderRealName) return;

        if (!(state instanceof IDisguiseRenderState asDisguiseRenderState))
            return;
/*
        if (((IDisguiseRenderState) state).morphclient$getDisguiseSyncer() != null)
        {
            var syncer = ((IDisguiseRenderState) state).morphclient$getDisguiseSyncer();
            var bindingPlayer = syncer.getBindingPlayer();

            var distance = MinecraftClient.getInstance().gameRenderer.getCamera().getPos().squaredDistanceTo(
                    state.x, state.y, state.z
            );

            MinecraftClient.getInstance().player.sendMessage(Text.literal("State XYZ: %s, %s, %s".formatted(
                    state.x, state.y, state.z
            )), false);
        }
*/
        // 服务器发送来的揭示数据是 玩家ID <-> 玩家名 的格式
        // 因此当客户端玩家有伪装时，渲染其本体也会显示揭示标签
        // 但我们不想这样，所以跳过此实体的渲染
        if (asDisguiseRenderState.morphclient$isClientPlayer())
            return;

        if (asDisguiseRenderState.morphclient$getRevealName() == null)
            return;

        renderLabelOnTop(matrices, vertexConsumers, textRenderer, state, dispatcher,
                asDisguiseRenderState.morphclient$getRevealName(), asDisguiseRenderState.morphclient$masterPosition());
    }

    public void renderLabelOnTop(PoseStack matrices, MultiBufferSource vertexConsumers,
                                 Font textRenderer,
                                 EntityRenderState renderState, EntityRenderDispatcher dispatcher,
                                 String textToRender,
                                 @Nullable Vec3 anchorPosition)
    {
        matrices.pushPose();

        Vec3 labelRelativePosition = renderState.nameTagAttachment;

        if (labelRelativePosition == null)
            labelRelativePosition = new Vec3(0, 0.25, 0);

        labelRelativePosition.add(renderState.boundingBoxHeight);

        matrices.translate(labelRelativePosition.x, labelRelativePosition.y + 0.5f, labelRelativePosition.z);

        matrices.mulPose(dispatcher.cameraOrientation());
        matrices.scale(0.025F, -0.025F, 0.025F);

        if (FeatherMorphClient.getInstance().getModConfigData().scaleNameTag && anchorPosition != null)
        {
            var labelWorldPosition = anchorPosition.add(labelRelativePosition);
            var distance = dispatcher.camera.getPosition().distanceTo(labelWorldPosition);
            var scale = Math.max(1, (float)distance / 7.5f);
            matrices.scale(scale, scale, scale);
        }

        float clientBackgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int finalColor = (int)(clientBackgroundOpacity * 255.0f) << 24;

        var positionMatrix = matrices.last().pose();
        var x = textRenderer.width(textToRender) / -2f;

        //背景+文字
        textRenderer.drawInBatch(textToRender, x, 0,
                textColorTransparent, false,
                positionMatrix, vertexConsumers,
                Font.DisplayMode.SEE_THROUGH, finalColor, LightTexture.FULL_BRIGHT);

        //文字
        textRenderer.drawInBatch(textToRender, x, 0,
                textColor, false,
                positionMatrix, vertexConsumers,
                Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);

        matrices.popPose();
    }
}
