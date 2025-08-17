package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import xyz.nifeather.morph.client.entities.IMorphCreeper;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class CreeperPropertyHandler extends EntityPropertyHandler<Creeper>
{
    public final ClientProperty<Boolean> CHARGED = ClientProperty.of(PropertyNames.CREEPER_CHARGED, CommonInputHandles.BOOLEAN);

    public CreeperPropertyHandler()
    {
        register(CHARGED);
    }

    @Override
    public Optional<Creeper> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Creeper creeper ? creeper : null);
    }

    @Override
    protected <X> void applyToEntity(Creeper entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.identifier().equals(PropertyNames.CREEPER_CHARGED))
            ((IMorphCreeper)entity).morphclient$setPowered((Boolean)value);
    }
}
