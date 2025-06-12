package xyz.nifeather.morph;

import net.neoforged.fml.common.Mod;
import xyz.nifeather.morph.shared.SharedValues;

@Mod(SharedValues.MOD_ID)
public class FeatherMorphNeoForgeInitializer
{
    /**
     * Runs the mod initializer.
     */
    public FeatherMorphNeoForgeInitializer()
    {
        new FeatherMorphCommonBootstrap();
    }
}
