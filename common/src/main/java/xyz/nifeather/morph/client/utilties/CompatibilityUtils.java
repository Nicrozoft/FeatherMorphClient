package xyz.nifeather.morph.client.utilties;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.integrations.ICullingHandler;
import xyz.nifeather.morph.client.integrations.entityculling.EntityCullingCompatibilityHandler;
import xyz.nifeather.morph.shared.platform.Services;

import java.util.List;

public class CompatibilityUtils
{
    private static final List<ICullingHandler> CULLING_HANDLERS = new ObjectArrayList<>();

    public static void makeEntityNotCulledIfPossible(Entity entity)
    {
        CULLING_HANDLERS.forEach(h -> h.avoidEntityFromCulling(entity));
    }

    public static void initialize()
    {
        runIfModPresent("entityculling", () -> CULLING_HANDLERS.add(new EntityCullingCompatibilityHandler()));
    }

    private static void runIfModPresent(String modid, Runnable runnable)
    {
        if (Services.PLATFORM.isModPresent("entityculling"))
        {
            FeatherMorphClientBootstrap.LOGGER.info("Found mod named %s, enabling compatibility handler...".formatted(modid));
            runnable.run();
        }
    }
}
