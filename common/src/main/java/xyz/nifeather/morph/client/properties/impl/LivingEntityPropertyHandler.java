package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.LivingEntity;
import xyz.nifeather.morph.client.properties.AbstractPropertyHandler;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public abstract class LivingEntityPropertyHandler<E extends LivingEntity> extends AbstractPropertyHandler<E>
{
    // Custom name is unsupported, since we use Adventure components on the plugin side...
    public final ClientProperty<String> CUSTOM_NAME = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME, CommonInputHandles::empty);
    public final ClientProperty<Boolean> CUSTOM_NAME_VISIBLE = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME_VISIBLE, CommonInputHandles.BOOLEAN);
}
