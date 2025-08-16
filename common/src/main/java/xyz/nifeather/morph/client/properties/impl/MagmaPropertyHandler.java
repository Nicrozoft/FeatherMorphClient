package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class MagmaPropertyHandler extends LivingEntityPropertyHandler<MagmaCube>
{
    public final ClientProperty<Integer> SIZE = ClientProperty.of(PropertyNames.SLIME_MAGMA_SIZE, CommonInputHandles::intOrEmpty);

    public MagmaPropertyHandler()
    {
        register(SIZE);
    }

    @Override
    public Optional<MagmaCube> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof MagmaCube magmaCube ? magmaCube : null);
    }

    @Override
    protected <X> void applyToEntity(MagmaCube entity, ClientProperty<X> property, X value)
    {
        if (property.equals(SIZE))
            entity.setSize((Integer)value, false);
    }
}
