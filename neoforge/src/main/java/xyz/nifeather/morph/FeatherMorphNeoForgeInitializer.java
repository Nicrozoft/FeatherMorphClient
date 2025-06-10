package xyz.nifeather.morph;

import net.neoforged.fml.common.Mod;

@Mod("feathermorph_client")
public class FeatherMorphNeoForgeInitializer {
    /**
     * Runs the mod initializer.
     */
    public FeatherMorphNeoForgeInitializer() {
        new FeatherMorphCommonBootstrap();
    }
}
