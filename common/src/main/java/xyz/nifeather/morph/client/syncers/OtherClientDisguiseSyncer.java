package xyz.nifeather.morph.client.syncers;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xyz.nifeather.morph.client.EntityCache;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;

public class OtherClientDisguiseSyncer extends DisguiseSyncer
{
    public OtherClientDisguiseSyncer(AbstractClientPlayer bindingPlayer,
                                     String morphId,
                                     int networkId,
                                     @NotNull LivingEntity disguiseEntity)
    {
        super(bindingPlayer, morphId, networkId, disguiseEntity);
    }

    @Override
    protected void onDispose()
    {
    }

    @Override
    public void syncTick()
    {
        if (disposed()) return;

        baseSync();
        syncYawPitch();
    }

    @Override
    protected void initialSync()
    {
        syncPosition();
        syncYawPitch();
    }
}
