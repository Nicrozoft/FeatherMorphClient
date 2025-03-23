package xyz.nifeather.morph.server.storage;

import xiamomc.pluginbase.storage.JsonBasedStorage;
import xyz.nifeather.morph.server.FeatherMorphFabricMain;

public abstract class FabricJsonBasedStorage<T> extends JsonBasedStorage<T, FeatherMorphFabricMain>
{
    @Override
    protected String getPluginNamespace()
    {
        return FeatherMorphFabricMain.pluginNamespace();
    }
}
