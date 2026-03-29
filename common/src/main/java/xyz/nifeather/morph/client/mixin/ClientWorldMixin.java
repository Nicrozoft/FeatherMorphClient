package xyz.nifeather.morph.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;
import xyz.nifeather.morph.client.utilties.EntityCacheUtils;

import java.util.Collections;
import java.util.List;

@Mixin(ClientLevel.class)
public class ClientWorldMixin
{
    @Inject(method = "addEntity", at = @At("HEAD"))
    private void fm$onAddEntity(Entity entity, CallbackInfo ci)
    {
        EntityCacheUtils.onEntityAdd(entity);

        var fm$instanceTracker = DisguiseInstanceTracker.getInstance();
        fm$instanceTracker.setupSyncerIfNotExist(entity);
    }

    @WrapOperation(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void morphclient$onEntityTick(Entity entity, Operation<Void> original)
    {
        var syncer = DisguiseInstanceTracker.getInstance().findSyncerByDisguiseEntity(entity);
        if (syncer != null)
            syncer.preEntityTick();

        original.call(entity);

        if (syncer != null)
            syncer.postEntityTick();
    }

    @Inject(method = "getPushableEntities", at = @At("HEAD"), cancellable = true)
    private void morphclient$getPushableEntities(Entity pusher, AABB boundingBox,
                                                 CallbackInfoReturnable<List<Entity>> cir)
    {
        if (pusher instanceof IMorphClientEntity iMorphClientEntity && iMorphClientEntity.featherMorph$isDisguiseEntity())
            cir.setReturnValue(Collections.emptyList());
    }
}
