package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Slime;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class SlimePropertyHandler extends EntityPropertyHandler<Slime>
{
    public final ClientProperty<Integer> SIZE = ClientProperty.of(PropertyNames.SLIME_MAGMA_SIZE, CommonInputHandles::intOrEmpty);

    public SlimePropertyHandler()
    {
        register(SIZE);
    }

    @Override
    public Optional<Slime> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Slime slime ? slime : null);
    }

    @Override
    protected <X> void applyToEntity(Slime entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(SIZE))
            entity.setSize((Integer)value, false);
    }
}
