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
    protected void syncPosition()
    {
        if (disguiseInstance == null) return;

        var playerPos = bindingPlayer.position();

        //暂时先这样
        disguiseInstance.setPos(playerPos.add(-4096, -4096, -4096));
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

        if (disguiseInstance.isCurrentlyGlowing() != bindingPlayer.isCurrentlyGlowing())
            disguiseInstance.setGlowingTag(bindingPlayer.isCurrentlyGlowing());
    }

    @Override
    public void postEntityRender()
    {
        if (disguiseInstance == null)
            return;

        var playerPos = bindingPlayer.position();
        disguiseInstance.setPosRaw(playerPos.x, playerPos.y - 4096, playerPos.z);
        disguiseInstance.xOld = playerPos.x;
        disguiseInstance.yOld = playerPos.y - 4096;
        disguiseInstance.zOld = playerPos.z;
    }

    @Override
    public void preEntityRender()
    {
        if (disposed()) return;

        if (disguiseInstance == null)
            return;

        // From ClientDisguiseSyncer

        // workaround: When an entity is far away from the player, EMF will reduce the update rate for it.
        var playerPos = bindingPlayer.position();
        disguiseInstance.setPosRaw(playerPos.x, playerPos.y, playerPos.z);

        // And this is for 3d skin layer compatibility
        // See https://github.com/tr7zw/3d-Skin-Layers/blob/bd8637d2fedd0b9d836b3932b5b0e2415337a40c/src/main/java/dev/tr7zw/skinlayers/mixin/CustomHeadLayerMixin.java#L49
        disguiseInstance.xOld = playerPos.x;
        disguiseInstance.yOld = playerPos.y;
        disguiseInstance.zOld = playerPos.z;
    }

    @Override
    protected void initialSync()
    {
        syncPosition();
        syncYawPitch();
    }
}
