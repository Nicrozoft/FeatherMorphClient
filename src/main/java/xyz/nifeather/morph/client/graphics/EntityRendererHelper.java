package xyz.nifeather.morph.client.graphics;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.graphics.color.ColorUtils;
import xyz.nifeather.morph.client.graphics.color.MaterialColors;

import java.util.Map;

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

                renderState.morphclient$setMasterPosition(masterEntity.getPos());
            }
        }

        var entrySet = getRevealNameEntry(id);
        if (entrySet == null)
            return;

        String revealName = entrySet.getValue();
        var disguiseEntityName = renderingEntity.getName().getString();

        String text = "%s(%s)".formatted(disguiseEntityName, revealName);

        renderState.morphclient$setRevealName(text);
        renderState.morphclient$setClientPlayer(renderingEntity == MinecraftClient.getInstance().player);
    }

    public final void renderRevealNameIfPossible(EntityRenderDispatcher dispatcher,
                                                 EntityRenderState state, TextRenderer textRenderer,
                                           MatrixStack matrices, VertexConsumerProvider vertexConsumers)
    {
        if (!doRenderRealName) return;

        if (!(state instanceof IDisguiseRenderState asDisguiseRenderState))
            return;

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

    public void renderLabelOnTop(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                 TextRenderer textRenderer,
                                 EntityRenderState renderState, EntityRenderDispatcher dispatcher,
                                 String textToRender,
                                 @Nullable Vec3d anchorPosition)
    {
        matrices.push();

        Vec3d labelRelativePosition = renderState.nameLabelPos;

        if (labelRelativePosition == null)
            labelRelativePosition = new Vec3d(0, 0.25, 0);

        labelRelativePosition.add(renderState.height);

        matrices.translate(labelRelativePosition.x, labelRelativePosition.y + 0.5f, labelRelativePosition.z);

        matrices.multiply(dispatcher.getRotation());
        matrices.scale(0.025F, -0.025F, 0.025F);

        if (FeatherMorphClient.getInstance().getModConfigData().scaleNameTag && anchorPosition != null)
        {
            var labelWorldPosition = anchorPosition.add(labelRelativePosition);
            var distance = dispatcher.camera.getPos().distanceTo(labelWorldPosition);
            var scale = Math.max(1, (float)distance / 7.5f);
            matrices.scale(scale, scale, scale);
        }

        float clientBackgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
        int finalColor = (int)(clientBackgroundOpacity * 255.0f) << 24;

        var positionMatrix = matrices.peek().getPositionMatrix();
        var x = textRenderer.getWidth(textToRender) / -2f;

        //背景+文字
        textRenderer.draw(textToRender, x, 0,
                textColorTransparent, false,
                positionMatrix, vertexConsumers,
                TextRenderer.TextLayerType.SEE_THROUGH, finalColor, LightmapTextureManager.MAX_LIGHT_COORDINATE);

        //文字
        textRenderer.draw(textToRender, x, 0,
                textColor, false,
                positionMatrix, vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);

        matrices.pop();
    }
}
