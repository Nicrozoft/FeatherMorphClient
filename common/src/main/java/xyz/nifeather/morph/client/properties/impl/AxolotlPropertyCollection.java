package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.axolotl.Axolotl;
import xyz.nifeather.morph.client.mixin.accessors.AxolotlAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class AxolotlPropertyCollection extends LivingEntityPropertyCollection<Axolotl>
{
    public final ClientProperty<Axolotl.Variant, AxolotlAccessor> VARIANT =
            ClientProperty.builder(PropertyNames.AXOLOTL_VARIANT, Axolotl.Variant.LUCY, AxolotlAccessor.class)
                    .inputHandle(this::readVariant)
                    .entityHandle(AxolotlAccessor::callSetVariant)
                    .build();

    public AxolotlPropertyCollection()
    {
        register(VARIANT);
    }

    private Optional<Axolotl.Variant> readVariant(String string)
    {
        return CommonInputHandles.readEnum(Axolotl.Variant.values(), string);
    }
}
