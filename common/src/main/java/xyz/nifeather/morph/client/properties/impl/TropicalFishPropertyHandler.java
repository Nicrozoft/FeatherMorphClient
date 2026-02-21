package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.TropicalFishAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class TropicalFishPropertyHandler extends EntityPropertyHandler<TropicalFish>
{
    public final ClientProperty<DyeColor, TropicalFishAccessor> BODY_COLOR =
            ClientProperty.<DyeColor, TropicalFishAccessor>builder(PropertyNames.TROPICAL_FISH_BODY_COLOR, TropicalFishAccessor.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle(TropicalFishAccessor::callSetBaseColor)
                    .build();

    public final ClientProperty<DyeColor, TropicalFishAccessor> PATTERN_COLOR =
            ClientProperty.<DyeColor, TropicalFishAccessor>builder(PropertyNames.TROPICAL_FISH_PATTERN, TropicalFishAccessor.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle(TropicalFishAccessor::callSetPatternColor)
                    .build();

    public final ClientProperty<TropicalFish.Pattern, TropicalFishAccessor> PATTERN =
            ClientProperty.<TropicalFish.Pattern, TropicalFishAccessor>builder(PropertyNames.TROPICAL_FISH_PATTERN, TropicalFishAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readEnum(TropicalFish.Pattern.values(), s))
                    .entityHandle(TropicalFishAccessor::callSetPattern)
                    .build();

    public TropicalFishPropertyHandler()
    {
        register(BODY_COLOR, PATTERN_COLOR, PATTERN);
    }
}
