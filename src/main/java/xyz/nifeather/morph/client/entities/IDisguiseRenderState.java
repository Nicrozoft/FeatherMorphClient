package xyz.nifeather.morph.client.entities;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

public interface IDisguiseRenderState
{
    @Nullable
    String morphclient$getRevealName();

    void morphclient$setRevealName(@Nullable String name);

    @Nullable
    Vec3 morphclient$masterPosition();
    void morphclient$setMasterPosition(@Nullable Vec3 pos);

    @Nullable
    DisguiseSyncer morphclient$getDisguiseSyncer();
    void morphclient$setDisguiseSyncer(@Nullable DisguiseSyncer syncer);

    boolean morphclient$isClientPlayer();
    void morphclient$setClientPlayer(boolean isClientPlayer);
}
