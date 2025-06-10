package xyz.nifeather.morph.server.storage;

import xiamomc.pluginbase.storage.JsonBasedStorage;
import xyz.nifeather.morph.server.FeatherMorphMain;

public abstract class FabricJsonBasedStorage<T> extends JsonBasedStorage<T, FeatherMorphMain>
{
    @Override
    protected String getPluginNamespace()
    {
        return FeatherMorphMain.pluginNamespace();
    }
}
