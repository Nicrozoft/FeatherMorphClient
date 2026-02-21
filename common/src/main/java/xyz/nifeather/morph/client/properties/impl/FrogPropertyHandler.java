package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import xyz.nifeather.morph.client.mixin.accessors.FrogAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class FrogPropertyHandler extends EntityPropertyHandler<Frog>
{
    public final ClientProperty<Holder<FrogVariant>, FrogAccessor> VARIANT =
            ClientProperty.<Holder<FrogVariant>, FrogAccessor>builder(PropertyNames.FROG_VARIANT, FrogAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.FROG_VARIANT, s))
                    .entityHandle(FrogAccessor::callSetVariant)
                    .build();

    public FrogPropertyHandler()
    {
        register(VARIANT);
    }

    private Holder<FrogVariant> defaultVariant()
    {
        return Holder.direct(
                new FrogVariant(
                        new ClientAsset.ResourceTexture(
                                Identifier.parse("nonexist")
                        ),
                        SpawnPrioritySelectors.EMPTY
                )
        );
    }
}
