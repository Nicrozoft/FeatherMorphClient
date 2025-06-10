package xyz.nifeather.morph.server;

import xiamomc.pluginbase.PluginObject;

public class ServerPluginObject extends PluginObject<FeatherMorphMain>
{
    @Override
    protected String getPluginNamespace()
    {
        return FeatherMorphMain.pluginNamespace();
    }
}
