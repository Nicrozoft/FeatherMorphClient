package xyz.nifeather.morph.client.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.EntityCache;
import xyz.nifeather.morph.client.ServerHandler;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;
import xyz.nifeather.morph.client.utilties.ClientSyncerUtils;
import xyz.nifeather.morph.client.utilties.EntityCacheUtils;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public abstract class MorphClientEntityMixin implements IMorphClientEntity
{
    @Shadow
    private int id;

    @Shadow
    private Vec3 position;

    @Shadow public abstract Pose getPose();

    @Shadow public abstract void remove(Entity.RemovalReason reason);

    @Shadow protected abstract void setSharedFlag(int index, boolean value);

    @Shadow public abstract void setPose(Pose pose);

    private Entity featherMorph$entityInstance;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void featherMorph$onInit(EntityType<?> type, Level world, CallbackInfo ci)
    {
        featherMorph$entityInstance = (Entity) (Object) this;
    }

    @Inject(method = "setGlowingTag", at = @At("RETURN"))
    private void morphClient$onGlowingCall(boolean glowing, CallbackInfo ci)
    {
        var thisInstance = ((Entity)(Object)this);
        if (thisInstance.getTags().contains(EntityCache.tag))
            this.setSharedFlag(6, glowing);
    }

    @Inject(method = "getEyeY", at = @At("HEAD"), cancellable = true)
    private void featherMorph$onGetEyeY(CallbackInfoReturnable<Double> cir)
    {
        if (featherMorph$entityInstance == Minecraft.getInstance().player && ServerHandler.modifyBoundingBox)
        {
            runIfSyncerEntityNotNull(syncerEntity ->
                    cir.setReturnValue(Minecraft.getInstance().player.getY() + syncerEntity.getEyeHeight()));
        }
    }

    @Inject(method = "getEyeHeight(Lnet/minecraft/world/entity/Pose;)F", at = @At("HEAD"), cancellable = true)
    private void featherMorph$onGetEyeHeight(Pose pose, CallbackInfoReturnable<Float> cir)
    {
        if (featherMorph$entityInstance == Minecraft.getInstance().player && ServerHandler.modifyBoundingBox)
        {
            runIfSyncerEntityNotNull(syncerEntity ->
                    cir.setReturnValue(syncerEntity.getEyeHeight(pose)));
        }
    }

    @Inject(method = "getEyeHeight()F", at = @At("HEAD"), cancellable = true)
    private void featherMorph$onGetStandingEyeHeight(CallbackInfoReturnable<Float> cir)
    {
        if (featherMorph$entityInstance == Minecraft.getInstance().player && ServerHandler.modifyBoundingBox)
        {
            runIfSyncerEntityNotNull(syncerEntity ->
                    cir.setReturnValue(syncerEntity.getEyeHeight()));
        }
    }

    @Inject(method = "makeBoundingBox()Lnet/minecraft/world/phys/AABB;", at = @At("HEAD"), cancellable = true)
    private void featherMorph$onCalcCall(CallbackInfoReturnable<AABB> cir)
    {
        featherMorph$onCalcCallMthod(cir);
    }

    @Unique
    private void featherMorph$onCalcCallMthod(CallbackInfoReturnable<AABB> cir)
    {
        if (featherMorph$entityInstance == Minecraft.getInstance().player && ServerHandler.modifyBoundingBox)
        {
            runIfSyncerEntityNotNull(e ->
                    cir.setReturnValue(e.getDimensions(getPose()).makeBoundingBox(this.position)));
        }
    }

    @Unique
    private boolean featherMorph$isDisguiseInstance()
    {
        var currentClientSyncer = ClientDisguiseSyncer.getCurrentInstance();
        if (currentClientSyncer == null) return false;

        var disguise = currentClientSyncer.getDisguiseInstance();
        if (disguise == null) return false;

        return disguise.equals(this);
    }

    @Inject(method = "setRemoved", at = @At("RETURN"))
    private void morphClient$onRemoved(CallbackInfo ci)
    {
        EntityCacheUtils.postEntityRemove(featherMorph$entityInstance);
    }

    @Unique
    private void runIfSyncerEntityNotNull(Consumer<Entity> consumerifNotNull)
    {
        ClientSyncerUtils.runIfSyncerEntityValid(consumerifNotNull::accept);
    }

    @Unique
    private Pose morphClient$overridePose;

    @Override
    public void featherMorph$overridePose(@Nullable Pose newPose)
    {
        this.morphClient$overridePose = newPose;

        if (newPose != null)
            this.setPose(newPose);
    }

    @Inject(method = "getPose", at = @At("HEAD"), cancellable = true)
    private void morphClient$onPoseCall(CallbackInfoReturnable<Pose> cir)
    {
        if (morphClient$overridePose != null)
            cir.setReturnValue(morphClient$overridePose);
    }

    @Unique
    @Nullable
    private Boolean morphClient$isInvisible;

    @Override
    public void featherMorph$overrideInvisibility(boolean invisible)
    {
        if (invisible)
            this.morphClient$isInvisible = invisible;
        else
            this.morphClient$isInvisible = null;
    }

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void morphClient$onInvisibleCall(CallbackInfoReturnable<Boolean> cir)
    {
        if (this.morphClient$isInvisible != null)
            cir.setReturnValue(this.morphClient$isInvisible);
    }

    @Unique
    private boolean morphClient$noAcceptSetPose;

    @Override
    public void featherMorph$setNoAcceptSetPose(boolean noAccept)
    {
        this.morphClient$noAcceptSetPose = noAccept;
    }

    @Inject(method = "setPose", at = @At("HEAD"), cancellable = true)
    private void morphClient$onSetPose(Pose pose, CallbackInfo ci)
    {
        if (this.morphClient$noAcceptSetPose)
            ci.cancel();
    }

    @Unique
    private final List<Object> morphClient$bypassRequests = new ObjectArrayList<>();

    @Override
    public void featherMorph$requestBypassDispatcherRedirect(Object source, boolean bypass)
    {
        if (!bypass)
        {
            morphClient$bypassRequests.remove(source);
            return;
        }

        if (morphClient$bypassRequests.contains(source)) return;

        morphClient$bypassRequests.add(source);
    }

    @Override
    public boolean featherMorph$bypassesDispatcherRedirect()
    {
        return !morphClient$bypassRequests.isEmpty();
    }
}
