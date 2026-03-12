package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ItemDisplayPropertyCollection extends DisplayPropertyCollection
{
    public final ClientProperty<ItemDisplayContext, Display.ItemDisplay> DISPLAY_MODEL =
            ClientProperty.builder(PropertyNames.ITEM_DISPLAY_MODEL_TRANSFORM, ItemDisplayContext.NONE, Display.ItemDisplay.class)
                    .inputHandle(this::readTransform)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Display.ItemDisplay::setItemTransform)
                    .build();

    private Optional<ItemDisplayContext> readTransform(String input)
    {
        return CommonInputHandles.readEnum(ItemDisplayContext.values(), input);
    }

    public ItemDisplayPropertyCollection()
    {
        super();

        register(DISPLAY_MODEL);
    }
}
