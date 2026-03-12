package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.ShulkerAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ShulkerPropertyCollection extends LivingEntityPropertyCollection<Shulker>
{
    public final ClientProperty<DyeColor, ShulkerAccessor> COLOR =
            ClientProperty.builder(PropertyNames.SHULKER_COLOR, DyeColor.BLACK, ShulkerAccessor.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle((shulker, color) -> shulker.callSetVariant(Optional.ofNullable(color)))
                    .build();

    public final ClientProperty<Integer, ShulkerAccessor> SHELL_HEIGHT =
            ClientProperty.builder(PropertyNames.SHULKER_SHELL_HEIGHT, 0, ShulkerAccessor.class)
                    .inputHandle(CommonInputHandles::readInteger)
                    .outputHandle(CommonOutputHandles::writeInteger)
                    .entityHandle(ShulkerAccessor::callSetRawPeekAmount)
                    .build();

    public ShulkerPropertyCollection()
    {
        register(COLOR, SHELL_HEIGHT);
    }
}
