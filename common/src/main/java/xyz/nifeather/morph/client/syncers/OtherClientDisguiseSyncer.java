package xyz.nifeather.morph.client.syncers;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class OtherClientDisguiseSyncer extends DisguiseSyncer
{
    public OtherClientDisguiseSyncer(AbstractClientPlayer bindingPlayer,
                                     String morphId,
                                     int networkId,
                                     @NotNull Entity disguiseEntity)
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
        syncRotation();
    }

    @Override
    protected void initialSync()
    {
        syncPosition();
        syncRotation();
    }
}
