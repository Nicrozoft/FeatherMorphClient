package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zoglin;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class ZoglinPropertyHandler extends EntityPropertyHandler<Zoglin>
{
    public final ClientProperty<Boolean> IS_BABY = ClientProperty.of(PropertyNames.ZOGLIN_IS_BABY, CommonInputHandles.BOOLEAN);

    public ZoglinPropertyHandler()
    {
        register(IS_BABY);
    }

    @Override
    public Optional<Zoglin> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Zoglin hoglin ? hoglin : null);
    }

    @Override
    protected <X> void applyToEntity(Zoglin entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, syncer, property, value);

        if (property.equals(IS_BABY))
            entity.setBaby((Boolean)value);
    }
}
