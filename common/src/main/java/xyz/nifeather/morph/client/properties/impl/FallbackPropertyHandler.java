package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public class FallbackPropertyHandler extends EntityPropertyHandler<Entity>
{
    @Override
    public Optional<Entity> tryCast(Entity entity)
    {
        return entity instanceof Entity entity1 ? Optional.of(entity1) : Optional.empty();
    }
}
