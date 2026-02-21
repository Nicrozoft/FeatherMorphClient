package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.properties.*;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

public abstract class EntityPropertyHandler<E extends Entity> extends AbstractPropertyHandler<E>
{
    public final ClientProperty<Component> CUSTOM_NAME = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME, CommonInputHandles::component);
    public final ClientProperty<Boolean> CUSTOM_NAME_VISIBLE = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME_VISIBLE, CommonInputHandles.BOOLEAN);
    public final ClientProperty<DisguiseEquipment> EQUIPMENT = ClientProperty.of(PropertyNames.ENTITY_EQUIPMENT, CommonInputHandles::equipment);
    public final ClientProperty<Boolean> DISPLAY_DISGUISE_EQUIPMENT = ClientProperty.of(PropertyNames.ENTITY_DISPLAY_DISGUISE_EQUIPMENT, CommonInputHandles.BOOLEAN);

    public EntityPropertyHandler()
    {
        register(CUSTOM_NAME, CUSTOM_NAME_VISIBLE, EQUIPMENT, DISPLAY_DISGUISE_EQUIPMENT);
    }

    @Override
    protected <X> void applyToEntity(E entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        if (property.equals(CUSTOM_NAME))
        {
            entity.setCustomName((Component) value);
        }
        else if (property.equals(CUSTOM_NAME_VISIBLE))
        {
            entity.setCustomNameVisible((Boolean) value);
        }
        else if (property.equals(EQUIPMENT) || property.equals(DISPLAY_DISGUISE_EQUIPMENT))
        {
            syncer.syncEquipment();
        }
    }
}
