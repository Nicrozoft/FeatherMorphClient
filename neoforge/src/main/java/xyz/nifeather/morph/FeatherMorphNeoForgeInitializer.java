package xyz.nifeather.morph;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import xyz.nifeather.morph.shared.SharedValues;
import xyz.nifeather.morph.shared.platform.NeoForgeKeybindingHelper;
import xyz.nifeather.morph.shared.platform.NeoForgePlatformHelper;
import xyz.nifeather.morph.shared.platform.Services;

@Mod(SharedValues.MOD_ID)
public class FeatherMorphNeoForgeInitializer
{
    /**
     * Runs the mod initializer.
     */
    public FeatherMorphNeoForgeInitializer(IEventBus modBus)
    {
        ((NeoForgePlatformHelper)Services.PLATFORM).keybindingHelper.register(modBus);

        new FeatherMorphCommonBootstrap();
    }
}
