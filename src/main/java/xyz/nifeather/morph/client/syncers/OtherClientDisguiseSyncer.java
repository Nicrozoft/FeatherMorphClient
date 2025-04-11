package xyz.nifeather.morph.client.syncers;

import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.NotNull;
import xyz.nifeather.morph.client.EntityCache;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;

public class OtherClientDisguiseSyncer extends DisguiseSyncer
{
    public OtherClientDisguiseSyncer(AbstractClientPlayer bindingPlayer, String morphId, int networkId)
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
    protected void onDispose()
    {
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
        syncYawPitch();
    }

    @Override
    public void postEntityRender()
    {
        super.postEntityRender();
    }

    @Override
    public void preEntityRender()
    {
        super.preEntityRender();
    }

    @Override
    protected void initialSync()
    {
        syncPosition();
        syncYawPitch();
    }
}
