package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.feline.CatVariants;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.mixin.accessors.CatAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;
import java.util.UUID;

public class CatPropertyCollection extends LivingEntityPropertyCollection<Cat>
{
    public final ClientProperty<Holder<CatVariant>, CatAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.CAT_VARIANT, lookupVariantOrThrow(Registries.CAT_VARIANT, CatVariants.BLACK), CatAccessor.class)
                    .inputHandle(this::readCatVariant)
                    .entityHandle(CatAccessor::callSetVariant)
                    .build();

    public final ClientProperty<UUID, Cat> OWNER =
            ClientProperty.builder(PropertyNames.CAT_OWNER, UUID.randomUUID(), Cat.class)
                    .inputHandle(CommonInputHandles::uuid)
                    .entityHandle((cat, owner) -> cat.setOwnerReference(EntityReference.of(owner)))
                    .build();

    public final ClientProperty<DyeColor, CatAccessor> COLLAR_COLOR =
            ClientProperty.builder(PropertyNames.CAT_COLLAR_COLOR, DyeColor.BLACK, CatAccessor.class)
                    .inputHandle(CommonInputHandles::readDyeColor)
                    .entityHandle(this::writeCollarColor)
                    .build();

    public final ClientProperty<Boolean, Cat> SITTING =
            ClientProperty.builder(PropertyNames.CAT_SITTING, false, Cat.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(TamableAnimal::setInSittingPose)
                    .build();

    public final ClientProperty<Boolean, Cat> LYING =
            ClientProperty.builder(PropertyNames.CAT_LYING, false, Cat.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(Cat::setLying)
                    .build();

    public CatPropertyCollection()
    {
        register(VARIANT, OWNER, COLLAR_COLOR, SITTING, LYING);
    }

    private Optional<Holder<CatVariant>> readCatVariant(String input)
    {
        return CommonInputHandles.readVariantHolder(Registries.CAT_VARIANT, input);
    }

    private void writeCollarColor(CatAccessor accessor, DyeColor dyeColor)
    {
        accessor.callSetCollarColor(dyeColor);
        ((Cat) accessor).setTame(true, true);
    }
}
