package xyz.nifeather.morph.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

//public class ModMenuApiImpl {}

public class ModMenuApiImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> FeatherMorphClientBootstrap.getInstance().getFactory(parent).build();
    }
}