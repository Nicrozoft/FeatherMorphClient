package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Phantom;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class PhantomPropertyHandler extends EntityPropertyHandler<Phantom>
{
    public final ClientProperty<Integer> SIZE = ClientProperty.of(PropertyNames.PHANTOM_SIZE, CommonInputHandles::intOrEmpty);

    public PhantomPropertyHandler()
    {
        register(SIZE);
    }

    @Override
    public Optional<Phantom> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Phantom phantom ? phantom : null);
    }

    @Override
    protected <X> void applyToEntity(Phantom entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(SIZE))
            entity.setPhantomSize((Integer) value);
    }
}
