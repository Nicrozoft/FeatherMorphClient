package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3i;
import xyz.nifeather.morph.client.properties.*;

public abstract class LivingEntityPropertyCollection<E extends LivingEntity> extends EntityPropertyCollection<E>
{
    public final ClientProperty<Component, Entity> CUSTOM_NAME =
            ClientProperty.builder(PropertyNames.ENTITY_CUSTOM_NAME, Component.empty(), Component.class, Entity.class)
                    .inputHandle(CommonInputHandles::component)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(Entity::setCustomName)
                    .build();

    public final ClientProperty<Boolean, Entity> CUSTOM_NAME_VISIBLE =
            ClientProperty.builder(PropertyNames.ENTITY_CUSTOM_NAME_VISIBLE, false, Entity.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(Entity::setCustomNameVisible)
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

    public final ClientProperty<Float, LivingEntity> STATIC_HEALTH =
            ClientProperty.builder(PropertyNames.LIVING_ENTITY_STATIC_HEALTH, 20f, LivingEntity.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::writeFloat)
                    .entityHandle(LivingEntity::setHealth)
                    .build();

    public final ClientProperty<Vector3i, LivingEntity> BED_POS =
            ClientProperty.builder(PropertyNames.LIVING_ENTITY_BED_POS, new Vector3i(0), LivingEntity.class)
                    .restoreDefaultsBeforeDiscard(false)
                    .inputHandle(CommonInputHandles::readVector3iRelaxed)
                    .outputHandle(CommonOutputHandles::writeVector3i)
                    .entityHandle((e, pos) -> e.setSleepingPos(new BlockPos(pos.x, pos.y, pos.z)))
                    .build();

    public final ClientProperty<Boolean, LivingEntity> INVISIBLE =
            ClientProperty.builder(PropertyNames.LIVING_ENTITY_INVISIBLE, false, LivingEntity.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(LivingEntity::setInvisible)
                    .build();

    public LivingEntityPropertyCollection()
    {
        register(CUSTOM_NAME, CUSTOM_NAME_VISIBLE, EQUIPMENT, DISPLAY_DISGUISE_EQUIPMENT, STATIC_HEALTH, BED_POS, INVISIBLE);
    }

    protected <R extends Registry<V>, V> Holder<V> lookupVariantOrThrow(ResourceKey<R> registryKey, ResourceKey<V> key)
    {
        return Minecraft.getInstance().level.registryAccess()
                .lookupOrThrow(registryKey)
                .get(key)
                .orElseThrow();
    }
}
