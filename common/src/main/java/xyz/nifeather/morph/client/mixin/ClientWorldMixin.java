package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
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

    @Inject(method = "getPushableEntities", at = @At("HEAD"), cancellable = true)
    private void morphclient$getPushableEntities(Entity pusher, AABB boundingBox,
                                                 CallbackInfoReturnable<List<Entity>> cir)
    {
        if (pusher instanceof IMorphClientEntity iMorphClientEntity && iMorphClientEntity.featherMorph$isDisguiseEntity())
            cir.setReturnValue(Collections.emptyList());
    }
}
