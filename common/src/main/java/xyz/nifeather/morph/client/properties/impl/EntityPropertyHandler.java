package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.properties.*;

public abstract class EntityPropertyHandler<E extends Entity> extends AbstractPropertyHandler
{
    public final ClientProperty<Component, Entity> CUSTOM_NAME =
            ClientProperty.builder(PropertyNames.ENTITY_CUSTOM_NAME, Component.empty(), Component.class, Entity.class)
                    .inputHandle(CommonInputHandles::component)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle((entity, component) -> entity.setCustomName(component))
                    .build();

    public final ClientProperty<Boolean, Entity> CUSTOM_NAME_VISIBLE =
            ClientProperty.builder(PropertyNames.ENTITY_CUSTOM_NAME_VISIBLE, false, Entity.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle((entity, bl) -> entity.setCustomNameVisible(bl))
                    .build();

    public final ClientProperty<DisguiseEquipment, Entity> EQUIPMENT =
            ClientProperty.builder(PropertyNames.ENTITY_EQUIPMENT, DisguiseEquipment.empty(), Entity.class)
                    .inputHandle(CommonInputHandles::equipment)
                    .outputHandle(CommonOutputHandles::noOp)
                    .build();

    public final ClientProperty<Boolean, Entity> DISPLAY_DISGUISE_EQUIPMENT =
            ClientProperty.builder(PropertyNames.ENTITY_DISPLAY_DISGUISE_EQUIPMENT, false, Entity.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::noOp)
                    .build();

    public final ClientProperty<Float, Entity> STATIC_YAW =
            ClientProperty.builder(PropertyNames.ENTITY_STATIC_YAW, 0f, Entity.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .build();

    public final ClientProperty<Float, Entity> STATIC_PITCH =
            ClientProperty.builder(PropertyNames.ENTITY_STATIC_PITCH, 0f, Entity.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .build();

    public EntityPropertyHandler()
    {
        register(CUSTOM_NAME, CUSTOM_NAME_VISIBLE, EQUIPMENT, DISPLAY_DISGUISE_EQUIPMENT, STATIC_YAW, STATIC_PITCH);
    }

    protected <R extends Registry<V>, V> Holder<V> lookupVariantOrThrow(ResourceKey<R> registryKey, ResourceKey<V> key)
    {
        return Minecraft.getInstance().level.registryAccess()
                .lookupOrThrow(registryKey)
                .get(key)
                .orElseThrow();
    }
}
