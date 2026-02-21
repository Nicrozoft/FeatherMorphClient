package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import xyz.nifeather.morph.client.mixin.accessors.PigAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class PigPropertyHandler extends EntityPropertyHandler<Pig>
{
    public final ClientProperty<Holder<PigVariant>, PigAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.PIG_VARIANT, Holder.direct(new PigVariant(new ModelAndTexture<>(PigVariant.ModelType.NORMAL, Identifier.parse("nonexist")), SpawnPrioritySelectors.EMPTY)), PigAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.PIG_VARIANT, s))
                    .entityHandle(PigAccessor::callSetVariant)
                    .build();

    public PigPropertyHandler()
    {
        register(VARIANT);
    }
}
