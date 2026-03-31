package xyz.nifeather.morph.client.integrations.entityculling;

import dev.tr7zw.entityculling.versionless.access.Cullable;
import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.integrations.ICullingHandler;

public class EntityCullingCompatibilityHandler implements ICullingHandler
{
    @Override
    public void avoidEntityFromCulling(Entity entity)
    {
        if (!(entity instanceof Cullable cullable)) return;

        cullable.setOutOfCamera(false);
        cullable.setCulled(false);
    }
}
