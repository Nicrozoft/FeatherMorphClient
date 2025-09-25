package xyz.nifeather.morph.client.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.pattern.TextRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import xyz.nifeather.morph.client.ClientMorphManager;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.graphics.color.ColorUtils;
import xyz.nifeather.morph.client.graphics.color.Colors;
import xyz.nifeather.morph.client.graphics.color.MaterialColors;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

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
        renderState.morphclient$setDisguiseSyncer(null);

        // then do setup

        int id = renderingEntity.getId();
        Entity masterEntity = null;

        // client renderer
        if (renderingEntity instanceof IMorphClientEntity iMorphEntity)
        {
            // 如果被我们的Syncer标记为了伪装实体，那么从syncer读取主实体位置
            if (iMorphEntity.featherMorph$isDisguiseEntity())
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
            else
            {
                // 否则，该实体的位置就是主实体的位置
                renderState.morphclient$setMasterPosition(renderingEntity.position());
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

    public final void submitRevealNames(PoseStack ignored, SubmitNodeCollector collector, CameraRenderState cameraRenderState)
    {
        if (!doRenderRealName) return;

        // apply rotation
        var camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
        if (camera == null)
            return;

        float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);

        var poseStack = new PoseStack();
        for (DisguiseSyncer syncer : DisguiseInstanceTracker.getInstance().getAllSyncer())
        {
            var disguiseInstance = syncer.getDisguiseInstance();
            if (disguiseInstance == null) continue;

            poseStack.pushPose();

            // Set position, then create disguise's render state
            Vec3 originalPos = disguiseInstance.position();
            disguiseInstance.setPos(syncer.getBindingPlayer().position());
            var renderState = Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(disguiseInstance, 0f);
            disguiseInstance.setPos(originalPos);

            if (!(renderState instanceof IDisguiseRenderState disguiseRenderState)) continue;

            var revealName = disguiseRenderState.morphclient$getRevealName();
            if (revealName == null) revealName = "<client unknown>(%s)".formatted(syncer.getBindingPlayer().getPlainTextName());

            var bindingPlayer = syncer.getBindingPlayer();
            var anchorPosition = Mth.lerp(tickDelta, bindingPlayer.oldPosition(), bindingPlayer.position());

            // apply position relative to the camera
            // Nametag offset
            var labelOffset = renderState.nameTagAttachment != null
                              ? renderState.nameTagAttachment
                              : new Vec3(0, renderState.boundingBoxHeight, 0);

            if (renderState.nameTag != null)
                labelOffset = labelOffset.add(0, 0.3, 0);

            var positionDiff = anchorPosition.subtract(camera.position()).add(labelOffset);
            poseStack.translate(positionDiff);

            // If tag should scale on distance
            if (FeatherMorphClientBootstrap.getInstance().getModConfigData().scaleNameTag)
            {
                var distance = camera.position().distanceTo(anchorPosition);
                var scale = Math.max(1, (float)distance / 7.5f);
                poseStack.scale(scale, scale, scale);
            }

            collector.submitNameTag(poseStack, Vec3.ZERO, 0, Component.literal(revealName).withColor(textColor), false, LightTexture.FULL_BRIGHT, 0, cameraRenderState);
            poseStack.popPose();
        }
    }
}