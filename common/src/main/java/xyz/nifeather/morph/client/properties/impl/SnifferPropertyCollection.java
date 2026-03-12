package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.sniffer.Sniffer;
import xyz.nifeather.morph.client.mixin.accessors.SnifferAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class SnifferPropertyCollection extends LivingEntityPropertyCollection<Sniffer>
{
    public final ClientProperty<Sniffer.State, SnifferAccessor> SNIFFER_STATE =
            ClientProperty.builder(PropertyNames.SNIFFER_STATE, Sniffer.State.IDLING, SnifferAccessor.class)
                    .inputHandle(s -> CommonInputHandles.readEnum(Sniffer.State.values(), s))
                    .outputHandle(CommonOutputHandles::writeEnum)
                    .entityHandle(SnifferAccessor::callSetState)
                    .build();

    public SnifferPropertyCollection()
    {
        register(SNIFFER_STATE);
    }
}
