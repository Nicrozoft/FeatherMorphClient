package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.goat.Goat;
import xyz.nifeather.morph.client.entities.IMorphGoat;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class GoatPropertyHandler extends EntityPropertyHandler<Goat>
{
    public final ClientProperty<Boolean> HAS_LEFT_HORN = ClientProperty.of(PropertyNames.GOAT_HAS_LEFT_HORN, CommonInputHandles.BOOLEAN);
    public final ClientProperty<Boolean> HAS_RIGHT_HORN = ClientProperty.of(PropertyNames.GOAT_HAS_RIGHT_HORN, CommonInputHandles.BOOLEAN);

    public GoatPropertyHandler()
    {
        register(HAS_LEFT_HORN, HAS_RIGHT_HORN);
    }

    @Override
    public Optional<Goat> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Goat goat ? goat : null);
    }

    @Override
    protected <X> void applyToEntity(Goat entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        switch (property.identifier())
        {
            case PropertyNames.GOAT_HAS_LEFT_HORN -> ((IMorphGoat)entity).morphclient$setHasLeftHorn((Boolean)value);
            case PropertyNames.GOAT_HAS_RIGHT_HORN -> ((IMorphGoat)entity).morphclient$setHasRightHorn((Boolean)value);
        }
    }
}
