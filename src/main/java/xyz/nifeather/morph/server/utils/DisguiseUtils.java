package xyz.nifeather.morph.server.utils;

import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Managers.DependencyManager;
import xyz.nifeather.morph.server.FeatherMorphFabricMain;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.misc.DisguiseMeta;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;

public class DisguiseUtils
{
    @Nullable
    public static DisguiseMeta getDisguiseMeta(String identifier)
    {
        var logger = MorphServerLoader.LOGGER;
        var dependencies = DependencyManager.getInstance(FeatherMorphFabricMain.pluginNamespace());

        if (dependencies == null)
        {
            logger.warn("Calling DisguiseUtils#getDisguiseMeta while the server is not fully started?!");
            Thread.dumpStack();
            return null;
        }

        var morphManager = dependencies.get(FabricMorphManager.class, false);
        if (morphManager == null)
        {
            logger.warn("Calling DisguiseUtils#getDisguiseMeta while the server is not fully started?!");
            Thread.dumpStack();
            return null;
        }

        return morphManager.getDisguiseMetaFrom(identifier);
    }
}
