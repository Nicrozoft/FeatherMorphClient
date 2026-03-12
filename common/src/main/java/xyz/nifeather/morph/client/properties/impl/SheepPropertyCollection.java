package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class SheepPropertyCollection extends EntityPropertyCollection<Sheep>
{
    public final ClientProperty<DyeColor, Sheep> COLOR =
            ClientProperty.builder(PropertyNames.SHEEP_COLOR, DyeColor.BLACK, Sheep.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle(Sheep::setColor)
                    .build();

    public SheepPropertyCollection()
    {
        register(COLOR);
    }
}
