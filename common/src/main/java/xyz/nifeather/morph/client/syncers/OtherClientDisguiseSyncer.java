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
    protected void onPreEntityTick()
    {
        syncPositionRotation();
    }

    @Override
    protected void onPostEntityTick()
    {
        baseSync();
    }

    @Override
    protected void initialSync()
    {
        syncPositionRotation();
    }
}
