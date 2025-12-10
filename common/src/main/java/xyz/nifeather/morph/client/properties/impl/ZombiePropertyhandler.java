package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ZombiePropertyhandler extends EntityPropertyHandler<Zombie>
{
    public final ClientProperty<Boolean> IS_BABY = ClientProperty.of(PropertyNames.ZOMBIE_IS_BABY, CommonInputHandles.BOOLEAN);

    public ZombiePropertyhandler()
    {
        register(IS_BABY);
    }

    @Override
    public Optional<Zombie> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Zombie zombie ? zombie : null);
    }

    @Override
    protected <X> void applyToEntity(Zombie entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(IS_BABY))
            entity.setBaby((Boolean) value);
    }
}
