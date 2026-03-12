package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.ShulkerAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ShulkerPropertyCollection extends EntityPropertyCollection<Shulker>
{
    public final ClientProperty<DyeColor, ShulkerAccessor> COLOR =
            ClientProperty.builder(PropertyNames.SHEEP_COLOR, DyeColor.BLACK, ShulkerAccessor.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle((shulker, color) -> shulker.callSetVariant(Optional.ofNullable(color)))
                    .build();

    public ShulkerPropertyCollection()
    {
        register(COLOR);
    }
}
