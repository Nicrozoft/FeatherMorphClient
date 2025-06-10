package xyz.nifeather.morph;

import net.fabricmc.api.ModInitializer;

public class FeatherMorphFabricInitializer implements ModInitializer
{
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize()
    {
        new FeatherMorphCommonBootstrap();
    }
}
