package xyz.nifeather.morph.client.properties.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.properties.AbstractPropertyHandler;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public abstract class EntityPropertyHandler<E extends Entity> extends AbstractPropertyHandler<E>
{
    public final ClientProperty<Component> CUSTOM_NAME = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME, CommonInputHandles::component);
    public final ClientProperty<Boolean> CUSTOM_NAME_VISIBLE = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME_VISIBLE, CommonInputHandles.BOOLEAN);

    public EntityPropertyHandler()
    {
        register(CUSTOM_NAME, CUSTOM_NAME_VISIBLE);
    }

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    protected <X> void applyToEntity(E entity, ClientProperty<X> property, X value)
    {
        if (property.equals(CUSTOM_NAME))
            entity.setCustomName((Component) value);
        else if (property.equals(CUSTOM_NAME_VISIBLE))
            entity.setCustomNameVisible((Boolean) value);
    }
}
