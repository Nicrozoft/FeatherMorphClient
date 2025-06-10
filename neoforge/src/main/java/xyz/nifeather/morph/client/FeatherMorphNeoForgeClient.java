package xyz.nifeather.morph.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(dist = Dist.CLIENT, value = "feathermorph_client")
public class FeatherMorphNeoForgeClient {
    public FeatherMorphNeoForgeClient(ModContainer modContainer) {
        new FeatherMorphClientBootstrap(FMLPaths.CONFIGDIR.get(), (id) -> FMLLoader.getLoadingModList().getModFileById(id) != null);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (game, screen) -> FeatherMorphClientBootstrap.getInstance().getFactory(screen).build());
    }
}
