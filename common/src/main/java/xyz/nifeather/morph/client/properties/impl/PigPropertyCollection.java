package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.animal.pig.PigVariants;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import xyz.nifeather.morph.client.mixin.accessors.PigAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class PigPropertyCollection extends LivingEntityPropertyCollection<Pig>
{
    public final ClientProperty<Holder<PigVariant>, PigAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.PIG_VARIANT, lookupVariantOrThrow(Registries.PIG_VARIANT, PigVariants.DEFAULT), PigAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.PIG_VARIANT, s))
                    .entityHandle(PigAccessor::callSetVariant)
                    .build();

    public PigPropertyCollection()
    {
        register(VARIANT);
    }
}
