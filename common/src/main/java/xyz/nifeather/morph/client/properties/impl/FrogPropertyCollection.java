package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.frog.FrogVariants;
import xyz.nifeather.morph.client.mixin.accessors.FrogAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class FrogPropertyCollection extends LivingEntityPropertyCollection<Frog>
{
    public final ClientProperty<Holder<FrogVariant>, FrogAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.FROG_VARIANT, lookupVariantOrThrow(Registries.FROG_VARIANT, FrogVariants.TEMPERATE), FrogAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.FROG_VARIANT, s))
                    .entityHandle(FrogAccessor::callSetVariant)
                    .build();

    public FrogPropertyCollection()
    {
        register(VARIANT);
    }
}
