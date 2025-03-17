package xyz.nifeather.morph.client.syncers;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.jetbrains.annotations.NotNull;
import xyz.nifeather.morph.client.EntityCache;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;

public class OtherClientDisguiseSyncer extends DisguiseSyncer
{
    public OtherClientDisguiseSyncer(AbstractClientPlayerEntity bindingPlayer, String morphId, int networkId)
    {
        super(bindingPlayer, morphId, networkId);
    }

    @Override
    public boolean refreshEntity()
    {
        if (!super.refreshEntity())
            return false;

        if (disguiseInstance instanceof MorphLocalPlayer localPlayer)
            localPlayer.setBindingPlayer(this.bindingPlayer);

        return true;
    }

    @Override
    protected void syncPosition()
    {
        if (disguiseInstance == null) return;

        var playerPos = bindingPlayer.getPos();

        //暂时先这样
        disguiseInstance.setPosition(playerPos.add(-4096, -4096, -4096));
    }

    @Override
    protected void onDispose()
    {
        //if (disguiseInstance != null)
        //    bindingPlayer.setPosition(disguiseInstance.getPos());
    }

    private EntityCache localCache;

    @Override
    protected @NotNull EntityCache getEntityCache()
    {
        if (localCache == null) localCache = new EntityCache();
        else if (localCache.disposed() && !this.disposed())
        {
            logger.warn("A non-disposed DisguiseSyncer '%s' has a disposed EntityCache?!");
            logger.warn("Creating a new instance now...");
            Thread.dumpStack();

            localCache = new EntityCache();
        }

        return localCache;
    }

    @Override
    public void syncTick()
    {
        if (disguiseInstance == null || disposed()) return;

        baseSync();
        syncPosition();
        syncYawPitch();

        if (disguiseInstance.isGlowing() != bindingPlayer.isGlowing())
            disguiseInstance.setGlowing(bindingPlayer.isGlowing());
    }

    @Override
    public void syncDraw()
    {
        if (disposed()) return;

        syncYawPitch();
    }

    @Override
    protected void initialSync()
    {
        syncPosition();
        syncYawPitch();
    }
}
