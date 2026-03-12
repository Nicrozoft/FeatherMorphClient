package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.animal.chicken.ChickenVariants;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ChickenPropertyCollection extends LivingEntityPropertyCollection<Chicken>
{
    public final ClientProperty<Holder<ChickenVariant>, Chicken> VARIANT =
            ClientProperty.builder(PropertyNames.CHICKEN_VARIANT, lookupVariantOrThrow(Registries.CHICKEN_VARIANT, ChickenVariants.DEFAULT), Chicken.class)
                    .inputHandle(this::readChickenVariant)
                    .entityHandle(Chicken::setVariant)
                    .build();

    private Optional<Holder<ChickenVariant>> readChickenVariant(String input)
    {
        return CommonInputHandles.readVariantHolder(Registries.CHICKEN_VARIANT, input);
    }

    public ChickenPropertyCollection()
    {
        register(VARIANT);
    }
}
