package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import xyz.nifeather.morph.client.properties.AbstractPropertyHandler;
import xyz.nifeather.morph.client.properties.ClientProperty;

import java.util.Optional;

public class FallbackPropertyHandler extends AbstractPropertyHandler<LivingEntity>
{
    @Override
    public Optional<LivingEntity> tryCast(Entity entity)
    {
        return entity instanceof LivingEntity living ? Optional.of(living) : Optional.empty();
    }

    @Override
    protected <X> void applyToEntity(LivingEntity entity, ClientProperty<X> property, X value)
    {
    }
}
