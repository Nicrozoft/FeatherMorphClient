package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.HappyGhast;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class HappyGhastPropertyHandler extends EntityPropertyHandler<HappyGhast>
{
    public final ClientProperty<Boolean> IS_GHASTLING = ClientProperty.of(PropertyNames.HAPPY_GHAST_IS_GHASTLING, CommonInputHandles.BOOLEAN);

    public HappyGhastPropertyHandler()
    {
        register(IS_GHASTLING);
    }

    @Override
    public Optional<HappyGhast> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof HappyGhast happyGhast ? happyGhast : null);
    }

    @Override
    protected <X> void applyToEntity(HappyGhast entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(IS_GHASTLING))
            entity.setBaby(((Boolean)value));
    }
}
