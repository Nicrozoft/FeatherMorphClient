package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class SheepPropertyHandler extends EntityPropertyHandler<Sheep>
{
    public final ClientProperty<DyeColor, Sheep> COLOR =
            ClientProperty.builder(PropertyNames.SHEEP_COLOR, DyeColor.BLACK, Sheep.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle(Sheep::setColor)
                    .build();

    public SheepPropertyHandler()
    {
        register(COLOR);
    }
}
