package xyz.nifeather.morph.client.syncers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.slf4j.LoggerFactory;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.MorphClientObject;
import xyz.nifeather.morph.client.entities.*;
import xyz.nifeather.morph.client.mixin.accessors.AbstractHorseEntityMixin;
import xyz.nifeather.morph.client.mixin.accessors.EntityAccessor;
import xyz.nifeather.morph.client.mixin.accessors.LimbAnimatorAccessor;
import xyz.nifeather.morph.client.mixin.accessors.LivingEntityAccessor;
import xyz.nifeather.morph.client.properties.*;
import xyz.nifeather.morph.client.syncers.animations.AnimationHandler;
import xyz.nifeather.morph.client.utilties.ClientItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DisguiseSyncer extends MorphClientObject
{
    @NotNull
    protected final Entity disguiseInstance;

    @NotNull
    public Entity getDisguiseInstance()
    {
        return disguiseInstance;
    }

    @NotNull
    protected AbstractClientPlayer bindingPlayer;

    protected final String disguiseId;
    public String disguiseIdentifier()
    {
        return disguiseId;
    }

    protected final int bindingNetworkId;

    protected final ClientPropertyHolder propertyHolder = new ClientPropertyHolder();

    public ClientPropertyHolder propertyHolder()
    {
        return propertyHolder;
    }

    @NotNull
    public AbstractClientPlayer getBindingPlayer()
    {
        return bindingPlayer;
    }

    public DisguiseSyncer(@NotNull AbstractClientPlayer bindingPlayer,
                          String morphId, int networkId,
                          @NonNull Entity disguiseEntity)
    {
        this.bindingPlayer = bindingPlayer;
        this.disguiseId = morphId;
        this.bindingNetworkId = networkId;
        this.disguiseInstance = disguiseEntity;

        propertyHolder.hookOnPropertyWrite(this::onPropertyWrite);
        propertyHolder.hookOnPropertyDiscard(this::onPropertyDiscard);
        propertyHolder.hookOnTemporaryPropertyDiscard(this::onTemporaryPropertyDiscard);

        if (disguiseEntity instanceof IMorphClientEntity iMorphClientEntity)
            iMorphClientEntity.featherMorph$setIsDisguiseEntity(networkId);
    }

    private <X, E> void onTemporaryPropertyDiscard(ClientProperty<X, E> clientProperty)
    {
        var existing = propertyHolder.getOr(clientProperty, null);
        if (existing != null)
        {
            clientProperty.apply((E) disguiseInstance, existing);
            return;
        }

        if (clientProperty.restoreDefaultsBeforeDiscard())
        {
            clientProperty.tryCastEntity(disguiseInstance)
                    .ifPresent(e -> clientProperty.entityHandle().handle(e, clientProperty.defaultValue()));
        }
    }

    private <X, E> void onPropertyDiscard(ClientProperty<X, E> clientProperty)
    {
        if (clientProperty.restoreDefaultsBeforeDiscard())
        {
            clientProperty.tryCastEntity(disguiseInstance)
                    .ifPresent(e -> clientProperty.entityHandle().handle(e, clientProperty.defaultValue()));
        }

        switch (clientProperty.identifier())
        {
            case PropertyNames.ENTITY_STATIC_YAW -> lockedYaw = null;
            case PropertyNames.ENTITY_STATIC_PITCH -> lockedPitch = null;
        }
    }

    private void onPropertyWrite(ClientProperty<?, ?> property, @Nullable Object oldValue, @NotNull Object newValue)
    {
        switch (property.identifier())
        {
            case PropertyNames.ENTITY_EQUIPMENT ->
            {
                if (oldValue == null) return;
                mergeEquipment((DisguiseEquipment) oldValue, (DisguiseEquipment) newValue);
            }

            case PropertyNames.ENTITY_STATIC_YAW ->
            {
                this.lockedYaw = (Float) newValue;
            }

            case PropertyNames.ENTITY_STATIC_PITCH ->
            {
                this.lockedPitch = (Float) newValue;
            }
        }
    }

    private void mergeEquipment(@NotNull DisguiseEquipment existing, DisguiseEquipment newEquipment)
    {
        var property = propertyHolder().getProperty(PropertyNames.ENTITY_EQUIPMENT);
        if (property == null) return;

        var builder = DisguiseEquipment.builder();
        for (EquipmentSlot slot : EquipmentSlot.values())
        {
            var upcoming = newEquipment.getItemOrNull(slot);
            builder.forSlot(slot, Objects.requireNonNullElseGet(upcoming, () -> existing.getItem(slot)));
        }

        propertyHolder().set((ClientProperty<? super DisguiseEquipment, ?>) property, builder.build());
    }

    private AnimationHandler animHandler;

    public void setAnimationHandler(AnimationHandler handler)
    {
        this.animHandler = handler;
    }

    public void playAnimation(String animation)
    {
        if (animHandler == null)
        {
            logger.warn("No animation handler for disguise '%s'!".formatted(disguiseId));
            return;
        }

        animHandler.play(disguiseInstance, animation);
    }

    private int crystalId;

    protected Entity beamTarget;

    private void scheduleCrystalSearch()
    {
        if (beamTarget != null || crystalId == 0) return;

        this.addSchedule(this::scheduleCrystalSearch, 10);

        this.beamTarget = findCrystalBy(crystalId);
    }

    @Nullable
    private Entity findCrystalBy(int id)
    {
        if (id == 0) return null;

        return bindingPlayer.level().getEntity(id);
    }

    public void playAttackAnimation()
    {
        disguiseInstance.handleEntityEvent(EntityEvent.START_ATTACKING);
    }

    private static final ItemStack air = new ItemStack(Items.AIR);

    protected void mergeNbt(CompoundTag nbtCompound)
    {
        var entity = disguiseInstance;

        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(entity.problemPath(), FeatherMorphClientBootstrap.LOGGER))
        {
            entity.load(TagValueInput.create(scopedCollector, entity.registryAccess(), nbtCompound));
        }

        if (entity instanceof Horse horse)
        {
            var haveSaddle = nbtCompound.contains("SaddleItem");

            if (haveSaddle)
            {
                ItemStack itemStack = ClientItemUtils.fromCompound(bindingPlayer.level().registryAccess(), nbtCompound.getCompound("SaddleItem").orElseThrow())
                        .orElse(air);

                var isSaddle = itemStack.is(Items.SADDLE);

                ((AbstractHorseEntityMixin) horse).callSetFlag(4, isSaddle);
            }

            //Doesn't work for unknown reason
            if (nbtCompound.contains("ArmorItem"))
            {
                ItemStack armorItem = ClientItemUtils.fromCompound(bindingPlayer.level().registryAccess(), nbtCompound.getCompound("ArmorItem").orElseThrow())
                        .orElse(air);

                horse.setBodyArmorItem(armorItem);
            }
        }

        bindingPlayer.refreshDimensions();

        var crystalPosition = nbtCompound.getInt("BeamTarget").orElse(-1);
        crystalId = crystalPosition;
        this.beamTarget = findCrystalBy(crystalPosition);

        if (beamTarget == null)
            this.scheduleCrystalSearch();
    }

    //region DisguiseSyncing

    private final AtomicBoolean isSyncing = new AtomicBoolean(false);

    protected void markSyncing()
    {
        isSyncing.set(true);
    }

    protected void markNotSyncing()
    {
        isSyncing.set(false);
    }

    public boolean isSyncing()
    {
        return isSyncing.get();
    }

    private boolean allowTick = true;

    protected void onTickError()
    {
    }

    private void onSyncError(Exception e)
    {
        allowTick = false;
        markNotSyncing();

        logger.error(e.getMessage());
        e.printStackTrace();

        try
        {
            disposeEntity(disguiseInstance);
        }
        catch (Exception ee)
        {
            LoggerFactory.getLogger("MorphClient").error("Unable to remove entity:" + ee.getMessage());
            ee.printStackTrace();
        }

        onTickError();

        var clientPlayer = Minecraft.getInstance().player;
        assert clientPlayer != null;

        clientPlayer.displayClientMessage(Component.literal(this + "Sync Failed!"), false);
    }

    public void onGameTick()
    {
        if (!allowTick) return;

        try
        {
            var clientPlayer = Minecraft.getInstance().player;
            assert clientPlayer != null;

            syncTick();
        }
        catch (Exception e)
        {
            onSyncError(e);
        }
    }

    private record PositionRecord(
            Vec3 pos, float xRot, float yRot,
            Vec3 oldPos, float xRotOld, float yRotOld,
            Vec3 motion
    )
    {
        public static PositionRecord fromEntity(Entity entity)
        {
            return new PositionRecord(
                    entity.position(), entity.getXRot(), entity.getYRot(),
                    entity.oldPosition(), entity.xRotO, entity.yRotO,
                    entity.getDeltaMovement()
            );
        }

        public void applyToEntity(Entity entity)
        {
            entity.snapTo(pos, yRot, xRot);
            entity.setOldPosAndRot(oldPos, yRotOld, xRotOld);
            entity.setDeltaMovement(motion);
        }
    }

    @Nullable
    private PositionRecord disguiseLastPositionSaving;

    public void preRenderStateSetup()
    {
        if (!allowTick) return;

        var xRot = disguiseInstance.getXRot();
        var xRotO = disguiseInstance.xRotO;

        var yRot = disguiseInstance.getYRot();
        var yRotO = disguiseInstance.yRotO;

        disguiseLastPositionSaving = PositionRecord.fromEntity(disguiseInstance);

        disguiseInstance.snapTo(bindingPlayer.position(), yRot, xRot);
        disguiseInstance.setOldPosAndRot(bindingPlayer.oldPosition(), yRotO, xRotO);
        disguiseInstance.setDeltaMovement(bindingPlayer.getDeltaMovement());
    }

    public void modifyRenderState(EntityRenderState renderState)
    {
        if (!allowTick) return;

        if (disguiseInstance.getType() != EntityType.PLAYER && disguiseInstance.hasCustomName())
        {
            renderState.nameTag = disguiseInstance.getName();
            renderState.nameTagAttachment = disguiseInstance.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, disguiseInstance.getYRot(1));
        }

        if (renderState instanceof GuardianRenderState guardianRenderState)
            guardianRenderState.eyePosition = new Vec3(bindingPlayer.xo, bindingPlayer.yo, bindingPlayer.zo);

        if (renderState instanceof AvatarRenderState avatarRenderState)
        {
            float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
            var playerState = (AvatarRenderState) Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(bindingPlayer, tickDelta);

            avatarRenderState.capeFlap = playerState.capeFlap;
            avatarRenderState.capeLean = playerState.capeLean;
            avatarRenderState.capeLean2 = playerState.capeLean2;
        }
    }

    public void postRenderStateSetup()
    {
        if (!allowTick) return;

        if (disguiseLastPositionSaving != null)
        {
            disguiseLastPositionSaving.applyToEntity(disguiseInstance);
            disguiseLastPositionSaving = null;
        }
    }

    public void updateSkin(GameProfile profile)
    {
        if (disposed.get())
        {
            logger.warn("Trying to update skin for a disposed DisguiseSyncer " + this);
            Thread.dumpStack();
            return;
        }

        if (!RenderSystem.isOnRenderThread())
            throw new RuntimeException("May not invoke updateSkin() while not on the render thread.");

        if (!(disguiseInstance instanceof MorphLocalPlayer localPlayer))
        {
            FeatherMorphClientBootstrap.LOGGER.warn(this + " Received a GameProfile while current disguise is not a player! Current instance is %s".formatted(disguiseInstance));
            return;
        }

        localPlayer.updateSkin(profile);
    }

    public abstract void syncTick();

    public void onEntityRenderStateSetup(EntityRenderState renderState, IDisguiseRenderState asDisguiseRenderState)
    {
    }

    protected abstract void initialSync();

    @Nullable
    protected Float lockedYaw;

    @Nullable
    protected Float lockedPitch;

    protected void syncYawPitch()
    {
        var player = bindingPlayer;

        if (lockedPitch != null)
        {
            disguiseInstance.setXRot(lockedPitch);
        }
        else
        {
            BlockPos sleepingPos = null;
            if (disguiseInstance instanceof LivingEntity livingEntity)
                sleepingPos = livingEntity.getSleepingPos().orElse(null);

            // 幻翼的pitch需要倒转
            // Don't sync pitch when sleeping position is present -- Match plugin behavior (maybe?)
            if (sleepingPos == null && !(disguiseInstance instanceof Panda panda && panda.isSitting())) // Fix: Panda lock themselves pitch to zero when sitting
            {
                if (disguiseInstance.getType() == EntityType.PHANTOM)
                    disguiseInstance.setXRot(-player.getXRot());
                else
                    disguiseInstance.setXRot(player.getXRot());
            }
        }

        if (lockedYaw != null)
        {
            disguiseInstance.setYRot(lockedYaw);
            disguiseInstance.setYHeadRot(lockedYaw);
            disguiseInstance.setYBodyRot(lockedYaw);
        }
        else
        {
            //末影龙的Yaw和玩家是反的
            if (disguiseInstance.getType() == EntityType.ENDER_DRAGON)
                disguiseInstance.setYRot(180 + player.getYRot());
            else
                disguiseInstance.setYRot(player.getYRot());

            if (disguiseInstance instanceof LivingEntity livingEntity)
            {
                livingEntity.yHeadRot = player.yHeadRot;
                livingEntity.yHeadRotO = player.yHeadRotO;

                if (livingEntity.getType() == EntityType.ARMOR_STAND)
                {
                    livingEntity.yBodyRot = player.yHeadRot;
                    livingEntity.yBodyRotO = player.yHeadRotO;
                }
            }
        }
    }

    private static final DisguiseEquipment emptyEquipment = DisguiseEquipment.empty();

    public void syncEquipment()
    {
        if (!(disguiseInstance instanceof LivingEntity livingEntity))
            return;

        var shouldDisplayDisguiseEquipment = propertyHolder.getOr(PropertyNames.ENTITY_DISPLAY_DISGUISE_EQUIPMENT, false);

        // So that we don't read disguise equipment if we don't have to.
        var disguiseEquip = shouldDisplayDisguiseEquipment
                            ? propertyHolder.getOr(PropertyNames.ENTITY_EQUIPMENT, emptyEquipment)
                            : null;

        for (EquipmentSlot slot : EquipmentSlot.values())
        {
            if (!livingEntity.canUseSlot(slot))
                continue;

            var stack = shouldDisplayDisguiseEquipment ? disguiseEquip.getItem(slot) : bindingPlayer.getItemBySlot(slot);
            livingEntity.setItemSlot(slot, stack);
        }
    }

    protected void syncPosition()
    {
        var playerPos = bindingPlayer.position();
        disguiseInstance.setPos(playerPos.x, playerPos.y + 4096, playerPos.z);
    }

    private ClientLevel world;
    private ClientLevel prevWorld;
    protected void baseSync()
    {
        var entity = disguiseInstance;

        if (this.disposed())
        {
            logger.warn("Trying to update a disposed DisguiseSyncer(%s)!".formatted(this));
            Thread.dumpStack();
            return;
        }

        if (bindingPlayer.isRemoved() || bindingPlayer.level() != Minecraft.getInstance().level)
        {
            logger.info(this + " Player removed, scheduling syncer dispose");
            this.addSchedule(this::dispose);
            return;
        }

        world = Minecraft.getInstance().level;

        if (world != prevWorld)
        {
            var prev = prevWorld;
            prevWorld = world;

            if (prev != null)
            {
                logger.info(this + " World changed, scheduling syncer dispose");
                addSchedule(this::dispose);
            }

            return;
        }

        if (disguiseInstance.isRemoved())
        {
            logger.info(this + " Instance removed, scheduling syncer dispose");
            addSchedule(this::dispose);
            return;
        }

        markSyncing();
        syncPosition();
        syncEquipment();

        // 因为我们在LivingEntity和PlayerEntity那里都加了阻止伪装实体被世界tick的mixin,
        // 所以在这里手动调用tick
        entity.tick();

        if (beamTarget != null && beamTarget.isRemoved())
            beamTarget = null;

        if (entity instanceof LivingEntity livingEntity)
            syncOnLivingEntity(livingEntity);

        //todo: Move this out of DisguiseSyncer
        if (entity instanceof Display.ItemDisplay itemDisplay)
        {
            itemDisplay.setItemStack(bindingPlayer.getMainHandItem());
        }

        entity.setSharedFlagOnFire(bindingPlayer.isOnFire());

        // Hand and sneaking
        if (entity.getType() != EntityType.MANNEQUIN)
            entity.setShiftKeyDown(bindingPlayer.isShiftKeyDown());

        entity.setSprinting(bindingPlayer.isSprinting());

        //entity.inPowderSnow = clientPlayer.inPowderSnow;
        entity.setTicksFrozen(bindingPlayer.getTicksFrozen());

        entity.setDeltaMovement(bindingPlayer.getDeltaMovement());

        entity.setOnGround(bindingPlayer.onGround());

        ((EntityAccessor) entity).setWasTouchingWater(bindingPlayer.isInWater());

        // Glowing
        if (bindingPlayer.isCurrentlyGlowing() != entity.isCurrentlyGlowing())
            entity.setGlowingTag(bindingPlayer.isCurrentlyGlowing());

        //同步Pose
        if (!propertyHolder().contains(PropertyNames.ENTITY_STATIC_POSE))
            entity.setPose(bindingPlayer.getPose());

        entity.setSwimming(bindingPlayer.isSwimming());

        if (bindingPlayer.isPassenger() && entity.getVehicle() != bindingPlayer)
        {
            if (entity instanceof IMorphClientEntity asMorphClientEntity)
                asMorphClientEntity.featherMorph$overridePose(null);

            entity.startRiding(bindingPlayer);
        }
        else if (!bindingPlayer.isPassenger() && entity.isPassenger())
        {
            entity.stopRiding();
        }

        if (entity instanceof MorphLocalPlayer player)
            player.fishing = bindingPlayer.fishing;

        entity.setInvisible(bindingPlayer.isInvisible());

        markNotSyncing();
    }

    protected void syncOnLivingEntity(LivingEntity entity)
    {
        var entitylimbAnimatorAccessor = (LimbAnimatorAccessor) entity.walkAnimation;
        var playerLimbAccessor = (LimbAnimatorAccessor) bindingPlayer.walkAnimation;
        var playerLimb = bindingPlayer.walkAnimation;

        entitylimbAnimatorAccessor.setSpeedOld(playerLimbAccessor.getSpeedOld());
        entitylimbAnimatorAccessor.setPosition(playerLimb.position());
        entitylimbAnimatorAccessor.setSpeed(playerLimb.speed());

        // Sleep Pos
        if (!propertyHolder().contains(PropertyNames.LIVING_ENTITY_BED_POS))
        {
            var sleepPos = bindingPlayer.getSleepingPos().orElse(null);

            if (sleepPos != null)
                entity.setSleepingPos(sleepPos);
            else
                entity.clearSleepingPos();
        }

        // Health
        var healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);

        if (healthAttribute != null)
            healthAttribute.setBaseValue(bindingPlayer.getMaxHealth());

        if (!propertyHolder.contains(PropertyNames.LIVING_ENTITY_STATIC_HEALTH))
            entity.setHealth(bindingPlayer.getHealth());

        // Scale
        var scaleAttribute = entity.getAttribute(Attributes.SCALE);

        if (scaleAttribute != null && scaleAttribute.getValue() != bindingPlayer.getAttributeValue(Attributes.SCALE))
            scaleAttribute.setBaseValue(bindingPlayer.getAttributeValue(Attributes.SCALE));

        // Hand Swing
        // Mannequin don't have swing animation
        if (entity.getType() != EntityType.MANNEQUIN)
        {
            entity.swinging = bindingPlayer.swinging;
            entity.attackAnim = bindingPlayer.attackAnim;
            entity.oAttackAnim = bindingPlayer.oAttackAnim;
            entity.swingTime = bindingPlayer.swingTime;
            entity.swingingArm = bindingPlayer.swingingArm;
        }

        if (entity.isFallFlying() != bindingPlayer.isFallFlying())
            ((EntityAccessor) entity).callSetSharedFlag(7, bindingPlayer.isFallFlying());

        // Hurt and death
        entity.hurtTime = bindingPlayer.hurtTime;
        entity.deathTime = bindingPlayer.deathTime;

        if (entity.isUsingItem() != bindingPlayer.isUsingItem())
        {
            entity.startUsingItem(bindingPlayer.getUsedItemHand());
            ((LivingEntityAccessor)entity).callSetLivingEntityFlag(1, bindingPlayer.isUsingItem());
            ((LivingEntityAccessor)entity).callSetLivingEntityFlag(2, bindingPlayer.getUsedItemHand() == InteractionHand.OFF_HAND);
        }

        entity.setArrowCount(bindingPlayer.getArrowCount());
    }

    //endregion

    //region Disposal

    protected void disposeEntity(@NotNull Entity disguise)
    {
        if (disguise instanceof Guardian guardian && guardian.getActiveAttackTarget() instanceof IHasOverrideGlowing overrideGlowing)
            overrideGlowing.morphclient$overrideClientGlowing(false);

        if (RenderSystem.isOnRenderThread())
            disguise.discard();
        else
            addSchedule(disguise::discard);
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    public final boolean disposed()
    {
        return disposed.get();
    }

    protected abstract void onDispose();

    public final void dispose()
    {
        if (disposed())
            return;

        disposeEntity(disguiseInstance);

        try
        {
            this.onDispose();
        }
        catch (Throwable t)
        {
            logger.warn("Error calling onDispose() for DisguiseSyncer: %s".formatted(t.getMessage()));
            t.printStackTrace();
        }

        world = null;
        prevWorld = null;
        disposed.set(true);

        propertyHolder.reset();

        bindingPlayer.refreshDimensions();

        try
        {
            postDispose();
        }
        catch (Throwable t)
        {
            logger.warn("Error calling postDispose() for DisguiseSyncer: %s".formatted(t.getMessage()));
            t.printStackTrace();
        }
    }

    protected void postDispose()
    {
    }

    //endregion Disposal
}