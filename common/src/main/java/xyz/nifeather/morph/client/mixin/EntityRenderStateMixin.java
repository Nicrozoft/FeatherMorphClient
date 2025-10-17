package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.nifeather.morph.client.entities.IDisguiseRenderState;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

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
    private Vec3 morphclient$masterPosition;

    @Override
    @Nullable
    public Vec3 morphclient$masterPosition()
    {
        return morphclient$masterPosition;
    }

    @Override
    public void morphclient$setMasterPosition(@Nullable Vec3 pos)
    {
        this.morphclient$masterPosition = pos;
    }

    @Nullable
    private DisguiseSyncer morphclient$disguiseSyncer;

    @Override
    public @Nullable DisguiseSyncer morphclient$getDisguiseSyncer()
    {
        return morphclient$disguiseSyncer;
    }

    @Override
    public void morphclient$setDisguiseSyncer(@Nullable DisguiseSyncer syncer)
    {
        morphclient$disguiseSyncer = syncer;
    }

    @Unique
    private boolean morphclient$isClientPlayer;

}
