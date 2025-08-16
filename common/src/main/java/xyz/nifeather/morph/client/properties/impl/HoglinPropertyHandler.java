package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class HoglinPropertyHandler extends LivingEntityPropertyHandler<Hoglin>
{
    public final ClientProperty<Boolean> IS_BABY = ClientProperty.of(PropertyNames.HOGLIN_IS_BABY, CommonInputHandles.BOOLEAN);

    public HoglinPropertyHandler()
    {
        register(IS_BABY);
    }

    @Override
    public Optional<Hoglin> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Hoglin hoglin ? hoglin : null);
    }

    @Override
    protected <X> void applyToEntity(Hoglin entity, ClientProperty<X> property, X value)
    {
        if (property.equals(IS_BABY))
            entity.setAge(((Boolean)value) ? Integer.MIN_VALUE : 1);
    }
}
