package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements IDisguiseRenderState
{
    @Nullable
    @Unique
    private String morphclient$revealName;

    @Override
    @Nullable
    public String morphclient$getRevealName()
    {
        return morphclient$revealName;
    }

    @Override
    public void morphclient$setRevealName(@Nullable String name)
    {
        this.morphclient$revealName = name;
    }

    @Unique
    @Nullable
    private Vec3d morphclient$masterPosition;

    @Override
    @Nullable
    public Vec3d morphclient$masterPosition()
    {
        return morphclient$masterPosition;
    }

    @Override
    public void morphclient$setMasterPosition(@Nullable Vec3d pos)
    {
        this.morphclient$masterPosition = pos;
    }

    @Unique
    private boolean morphclient$isClientPlayer;

    @Override
    public boolean morphclient$isClientPlayer()
    {
        return morphclient$isClientPlayer;
    }

    @Override
    public void morphclient$setClientPlayer(boolean isClientPlayer)
    {
        this.morphclient$isClientPlayer = isClientPlayer;
    }
}
