package xyz.nifeather.morph.client;

import xiamomc.pluginbase.PluginObject;

public class MorphClientObject extends PluginObject<FeatherMorphClientBootstrap>
{
    @Override
    protected String getPluginNamespace()
    {
        return FeatherMorphClientBootstrap.getClientNameSpace();
    }
}
