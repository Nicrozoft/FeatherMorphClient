package xyz.nifeather.morph.client.utilties;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.integrations.entityculling.EntityCullingCompatibilityHandler;
import xyz.nifeather.morph.shared.platform.Services;

import java.util.List;

public class CompatibilityUtils
{
    public static void initialize()
    {
        runIfModPresent("entityculling", EntityCullingCompatibilityHandler::tryAddDynamicEntityWhitelist);
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
