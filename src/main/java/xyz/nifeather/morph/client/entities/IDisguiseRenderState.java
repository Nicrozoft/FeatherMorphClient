package xyz.nifeather.morph.client.entities;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public interface IDisguiseRenderState
{
    @Nullable
    String morphclient$getRevealName();

    void morphclient$setRevealName(@Nullable String name);

    @Nullable
    Vec3d morphclient$masterPosition();
    void morphclient$setMasterPosition(@Nullable Vec3d pos);

    boolean morphclient$isClientPlayer();
    void morphclient$setClientPlayer(boolean isClientPlayer);
}
