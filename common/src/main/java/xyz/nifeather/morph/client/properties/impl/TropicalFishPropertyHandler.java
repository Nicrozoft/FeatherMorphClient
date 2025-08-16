package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.TropicalFishAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class TropicalFishPropertyHandler extends LivingEntityPropertyHandler<TropicalFish>
{
    public final ClientProperty<DyeColor> BODY_COLOR = ClientProperty.of(PropertyNames.TROPICAL_FISH_BODY_COLOR, s -> CommonInputHandles.readEnum(DyeColor.values(), s));
    public final ClientProperty<DyeColor> PATTERN_COLOR = ClientProperty.of(PropertyNames.TROPICAL_FISH_PATTERN_COLOR, s -> CommonInputHandles.readEnum(DyeColor.values(), s));
    public final ClientProperty<TropicalFish.Pattern> PATTERN = ClientProperty.of(PropertyNames.TROPICAL_FISH_PATTERN, s -> CommonInputHandles.readEnum(TropicalFish.Pattern.values(), s));

    public TropicalFishPropertyHandler()
    {
        register(BODY_COLOR, PATTERN_COLOR, PATTERN);
    }

    @Override
    public Optional<TropicalFish> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof TropicalFish tropicalFish ? tropicalFish : null);
    }

    @Override
    protected <X> void applyToEntity(TropicalFish entity, ClientProperty<X> property, X value)
    {
        switch (property.identifier())
        {
            case PropertyNames.TROPICAL_FISH_BODY_COLOR -> ((TropicalFishAccessor)entity).callSetBaseColor((DyeColor) value);
            case PropertyNames.TROPICAL_FISH_PATTERN_COLOR -> ((TropicalFishAccessor)entity).callSetPatternColor((DyeColor) value);
            case PropertyNames.TROPICAL_FISH_PATTERN -> ((TropicalFishAccessor)entity).callSetPattern((TropicalFish.Pattern) value);
        }
    }
}
