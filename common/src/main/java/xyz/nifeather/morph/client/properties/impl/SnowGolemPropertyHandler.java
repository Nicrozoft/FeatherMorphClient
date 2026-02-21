package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class SnowGolemPropertyHandler extends EntityPropertyHandler<SnowGolem>
{
    public final ClientProperty<Boolean> HAS_PUMPKIN = ClientProperty.of(PropertyNames.SNOW_GOLEM_HAS_PUMPKIN, CommonInputHandles.BOOLEAN);

    public SnowGolemPropertyHandler()
    {
        register(HAS_PUMPKIN);
    }

    @Override
    public Optional<SnowGolem> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof SnowGolem snowGolem ? snowGolem : null);
    }

    @Override
    protected <X> void applyToEntity(SnowGolem entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, syncer, property, value);

        if (property.equals(HAS_PUMPKIN))
            entity.setPumpkin((Boolean) value);
    }
}
