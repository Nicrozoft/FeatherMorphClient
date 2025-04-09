package xyz.nifeather.morph.client.graphics;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import xyz.nifeather.morph.client.*;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;
import xyz.nifeather.morph.client.mixin.accessors.DragonEntityRendererAccessor;
import xyz.nifeather.morph.client.mixin.accessors.LivingRendererAccessor;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Exceptions.NullDependencyException;

import java.util.List;
import java.util.Map;

public class PlayerRenderHelper extends MorphClientObject
{
    private static PlayerRenderHelper instance;

    public static PlayerRenderHelper instance()
    {
        if (instance == null) instance = new PlayerRenderHelper();

        return instance;
    }

    public PlayerRenderHelper()
    {
    }

    @Initializer
    private void load(ClientMorphManager morphManager)
    {
        morphManager.currentIdentifier.onValueChanged((o, n) ->
        {
            this.allowRender = true;
        });
    }

    @Resolved
    private DisguiseInstanceTracker instanceTracker;

    public boolean shouldHideLabel(@Nullable AbstractClientPlayer player)
    {
        if (player == null) return false;

        var localSyncer = ClientDisguiseSyncer.getCurrentInstance();
        return localSyncer != null && player == localSyncer.getDisguiseInstance();
    }

    private void onRenderException(Exception exception)
    {
        allowRender = false;
        exception.printStackTrace();

        var syncer = ClientDisguiseSyncer.getCurrentInstance();
        if (syncer == null)
            throw new NullDependencyException("Render Exception with null Syncer ?!");

        var entity = syncer.getDisguiseInstance();

        if (entity != null)
        {
            try
            {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
            catch (Exception ee)
            {
                LoggerFactory.getLogger("MorphClient").error("无法移除实体：" + ee.getMessage());
                ee.printStackTrace();
            }
        }

        var clientPlayer = Minecraft.getInstance().player;
        assert clientPlayer != null;

        clientPlayer.displayClientMessage(Component.translatable("text.morphclient.error.render_disguise1"), false);
        clientPlayer.displayClientMessage(Component.translatable("text.morphclient.error.render_disguise2"), false);
    }

    @ApiStatus.Internal
    public boolean skipRender = false;

    private Camera camera()
    {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    /**
     * 在玩家位置渲染通向 {@link ClientDisguiseSyncer#getBeamTarget()} 的光柱
     * @param tickCounter tickCounter
     * @param matrixStack {@link PoseStack}
     * @param vertexConsumerProvider {@link MultiBufferSource}
     * @param light 光照等级
     */
    public void renderCrystalBeam(DeltaTracker tickCounter, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light)
    {
        DisguiseSyncer abstractSyncer = instanceTracker.getSyncerFor(Minecraft.getInstance().player);

        if (!(abstractSyncer instanceof ClientDisguiseSyncer syncer)) return;

        var connectedCrystal = syncer.getBeamTarget();

        if (connectedCrystal == null) return;

        matrixStack.pushPose();

        var cameraPos = camera().getPosition();

        //相机XYZ
        var cameraX = cameraPos.x;
        var cameraY = cameraPos.y;
        var cameraZ = cameraPos.z;

        var player = Minecraft.getInstance().player;
        assert player != null;

        var tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);
        //通过插值的方式获取玩家XYZ可以避免让渲染出来的光柱看起来非常卡顿
        var lerpPlayerX = Mth.lerp(tickDelta, player.xo, player.getX());
        var lerpPlayerY = Mth.lerp(tickDelta, player.yo, player.getY());
        var lerpPlayerZ = Mth.lerp(tickDelta, player.zo, player.getZ());

        //光柱目标的Y轴位移，数值越大最终的位置相较于相机越低
        var yOffset = 1f;

        //相对位置，光柱在这里结束
        var relativeX = (float)(connectedCrystal.getX() - lerpPlayerX);
        var relativeY = (float)(connectedCrystal.getY() - lerpPlayerY) + yOffset;
        var relativeZ = (float)(connectedCrystal.getZ() - lerpPlayerZ);

        //对matrixStack进行位移，将其中心设定在玩家处
        //光柱在这里开始
        //玩家位置 - 相机位置 = 目标位移
        matrixStack.translate(lerpPlayerX - cameraX, lerpPlayerY - cameraY - yOffset, lerpPlayerZ - cameraZ);

        //渲染光柱
        EnderDragonRenderer.renderCrystalBeams(relativeX,
                relativeY + getCrystalYOffsetCopy(connectedCrystal, tickDelta),
                relativeZ,
                player.tickCount + tickDelta, matrixStack, vertexConsumerProvider, light);

        matrixStack.popPose();
    }

    private float getCrystalYOffsetCopy(Entity entity, float tickDelta)
    {
        var age = entity instanceof EndCrystal endCrystalEntity ? endCrystalEntity.time : 0;

        float f = age + tickDelta;
        float g = Mth.sin(f * 0.2f) / 2.0f + 0.5f;
        g = (g * g + g) * 0.4f;
        return g - 1.4f;
    }

    private boolean allowRender = true;

    public boolean renderingLeftPart;

    private final Map<EntityType<?>, ModelInfo> typeModelPartMap = new Object2ObjectOpenHashMap<>();

    public record ModelInfo(@Nullable ModelPart left, @Nullable ModelPart right, Vec3 offset, Vec3 scale)
    {
        @Nullable
        public ModelPart getPart(boolean isLeftArm)
        {
            return isLeftArm ? left : right;
        }
    }

    public ModelInfo tryGetModel(EntityType<?> type, @Nullable EntityModel<?> sourceModel)
    {
        if (sourceModel == null) return new ModelInfo(null, null, Vec3dUtils.of(0), Vec3dUtils.of(1));

        var map = typeModelPartMap.getOrDefault(type, null);

        if (map != null)
            return map;

        ModelPart model = null;

        //尝试获取对应的模型
        //有些模型变换会影响全局渲染，所以我们需要创建一个新的模型（比方说雪傀儡和铁傀儡的手臂模型）
        var targetEntry = LayerDefinitions.createRoots().entrySet().stream()
                .filter(e -> e.getKey().model().equals(EntityType.getKey(type))).findFirst().orElse(null);

        if (targetEntry != null)
            model = targetEntry.getValue().bakeRoot();

        ModelPart leftPart = null;
        ModelPart rightPart = null;
        Vec3 offset = Vec3dUtils.of(0);
        Vec3 scale = Vec3dUtils.ONE();

        if (model != null)
        {
            var leftPartNames = List.of(
                    PartNames.LEFT_ARM,
                    PartNames.LEFT_LEG,
                    PartNames.LEFT_FRONT_LEG,
                    PartNames.LEFT_HIND_LEG,
                    PartNames.LEFT_FOOT,
                    PartNames.LEFT_FRONT_FOOT,
                    PartNames.LEFT_HIND_FOOT,
                    "part9"
            );

            var rightPartNames = List.of(
                    PartNames.RIGHT_ARM,
                    PartNames.RIGHT_LEG,
                    PartNames.RIGHT_FRONT_LEG,
                    PartNames.RIGHT_HIND_LEG,
                    PartNames.RIGHT_FOOT,
                    PartNames.RIGHT_FRONT_FOOT,
                    PartNames.RIGHT_HIND_FOOT,
                    "part9"
            );

            if (sourceModel instanceof HumanoidModel<?> bipedEntityModel)
            {
                leftPart = bipedEntityModel.leftArm;
                rightPart = bipedEntityModel.rightArm;
            }
            else
            {
                leftPart = this.tryGetChild(model, leftPartNames);
                rightPart = this.tryGetChild(model, rightPartNames);

                var meta = ModelWorkarounds.getInstance().apply(type, leftPart, rightPart);

                offset = meta.offset();
                scale = meta.scale();
            }
        }

        map = new ModelInfo(leftPart, rightPart, offset, scale);
        typeModelPartMap.put(type, map);

        return map;
    }

    private ModelPart tryGetChild(ModelPart modelPart, String childName)
    {
        //From SinglePartEntityModel#getChild(String name)
        return modelPart.getAllParts().filter(part -> part.hasChild(childName)).findFirst().map(part -> part.getChild(childName)).orElse(null);
    }

    private ModelPart tryGetChild(ModelPart modelPart, List<String> childNames)
    {
        ModelPart part = null;

        for (var s : childNames)
        {
            part = tryGetChild(modelPart, s);

            if (part != null) break;
        }

        return part;
    }

    private final RenderType dragonLayer = RenderType.entityCutoutNoCull(ResourceLocation.parse("textures/entity/enderdragon/dragon.png"));

    /**
     * @return Whether rendered disguise instance
     */
    @SuppressWarnings("rawtypes")
    public boolean onArmDrawCall(PoseStack matrices, MultiBufferSource vertexConsumers, int light)
    {
        //logger.info("On Arms!");
        if (!allowRender) return false;

        try
        {
            var syncer = ClientDisguiseSyncer.getCurrentInstance();

            if (syncer == null || syncer.disposed()) return false;
            var disguiseEntity = syncer.getDisguiseInstance();

            if (disguiseEntity == null) return false;

            EntityRenderer<?, ?> disguiseRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(disguiseEntity);

            ModelPart targetArm;
            ModelInfo modelInfo;
            RenderType layer = null;
            EntityModel model = null;

            if (disguiseRenderer instanceof EnderDragonRenderer enderDragonEntityRenderer)
            {
                model = ((DragonEntityRendererAccessor) enderDragonEntityRenderer).getModel();
                layer = dragonLayer;
            }

            if (disguiseRenderer instanceof LivingEntityRenderer livingEntityRenderer)
            {
                model = livingEntityRenderer.getModel();

                if (disguiseEntity instanceof MorphLocalPlayer localPlayer)
                {
                    var renderer = (PlayerRenderer) livingEntityRenderer;

                    if (renderingLeftPart)
                        renderer.renderLeftHand(matrices, vertexConsumers, light, localPlayer.getSkin().texture(), true);
                    else
                        renderer.renderRightHand(matrices, vertexConsumers, light, localPlayer.getSkin().texture(), true);

                    return true;
                }

                var renderState = (LivingEntityRenderState) livingEntityRenderer.createRenderState();
                livingEntityRenderer.extractRenderState(disguiseEntity, renderState, 0);
                layer = ((LivingRendererAccessor) livingEntityRenderer).callGetRenderType(renderState, true, false, true);
            }

            if (model != null)
                model.resetPose();

            modelInfo = tryGetModel(disguiseEntity.getType(), model);
            targetArm = modelInfo.getPart(renderingLeftPart);

            if (targetArm != null)
            {
                layer = layer == null ? RenderType.solid() : layer;

                targetArm.visible = true;
                //targetArm.resetTransform();

                var scale = modelInfo.scale;
                matrices.scale((float)scale.x(), (float)scale.y(), (float)scale.z());

                var offset = modelInfo.offset;
                matrices.translate(offset.x(), offset.y(), offset.z());

                light = (disguiseEntity.getType() == EntityType.ALLAY || disguiseEntity.getType() == EntityType.VEX)
                        ? LightTexture.FULL_BRIGHT
                        : light;

                targetArm.xRot = 0;
                targetArm.render(matrices, vertexConsumers.getBuffer(layer), light, OverlayTexture.NO_OVERLAY);

                return true;
            }
        }
        catch (Exception e)
        {
            onRenderException(e);
        }

        return false;
    }
}
