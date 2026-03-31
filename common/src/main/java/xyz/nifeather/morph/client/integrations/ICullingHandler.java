package xyz.nifeather.morph.client.integrations;

import net.minecraft.world.entity.Entity;

public interface ICullingHandler
{
    void avoidEntityFromCulling(Entity entity);
}
