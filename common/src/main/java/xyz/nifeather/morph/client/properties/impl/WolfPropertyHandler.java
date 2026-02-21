package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariants;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.WolfAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;
import java.util.UUID;

public class WolfPropertyHandler extends EntityPropertyHandler<Wolf>
{
    public final ClientProperty<Holder<WolfVariant>, WolfAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.WOLF_VARIANT, lookupVariantOrThrow(Registries.WOLF_VARIANT, WolfVariants.ASHEN), WolfAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.WOLF_VARIANT, s))
                    .entityHandle(WolfAccessor::callSetVariant)
                    .build();

    public final ClientProperty<UUID, Wolf> OWNER =
            ClientProperty.builder(PropertyNames.WOLF_OWNER, UUID.randomUUID(), Wolf.class)
            .inputHandle(CommonInputHandles::uuid)
            .entityHandle((wolf, ownerUUID) ->
            {
                wolf.setOwnerReference(EntityReference.of(ownerUUID));
                wolf.setTame(true, true);
            })
            .build();

    public final ClientProperty<DyeColor, WolfAccessor> COLLAR_COLOR =
            ClientProperty.builder(PropertyNames.WOLF_COLLAR_COLOR, DyeColor.BLACK, WolfAccessor.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle(WolfAccessor::callSetCollarColor)
                    .build();

    public WolfPropertyHandler()
    {
        register(VARIANT, OWNER, COLLAR_COLOR);
    }
}
