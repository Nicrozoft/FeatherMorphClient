package xyz.nifeather.morph.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FeatherMorphClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        new FeatherMorphClientBootstrap(FabricLoader.getInstance().getConfigDir(), FabricLoader.getInstance()::isModLoaded);
    }
}
