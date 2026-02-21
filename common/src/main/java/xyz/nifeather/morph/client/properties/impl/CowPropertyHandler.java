package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.CowVariant;
import net.minecraft.world.entity.animal.cow.CowVariants;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class CowPropertyHandler extends EntityPropertyHandler<Cow>
{
    public final ClientProperty<Holder<CowVariant>, Cow> VARIANT =
            ClientProperty.builder(PropertyNames.COW_VARIANT, lookupVariantOrThrow(Registries.COW_VARIANT, CowVariants.DEFAULT), Cow.class)
                    .inputHandle(this::readVariant)
                    .entityHandle(Cow::setVariant)
                    .build();

    private Optional<Holder<CowVariant>> readVariant(String s)
    {
        return CommonInputHandles.readVariantHolder(Registries.COW_VARIANT, s);
    }

    public CowPropertyHandler()
    {
        register(VARIANT);
    }
}
