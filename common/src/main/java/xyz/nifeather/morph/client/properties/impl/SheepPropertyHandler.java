package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class SheepPropertyHandler extends LivingEntityPropertyHandler<Sheep>
{
    public final ClientProperty<DyeColor> COLOR = ClientProperty.of(PropertyNames.SHEEP_COLOR, s -> CommonInputHandles.readEnum(DyeColor.values(), s));

    public SheepPropertyHandler()
    {
        register(COLOR);
    }

    @Override
    public Optional<Sheep> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Sheep sheep ? sheep : null);
    }

    @Override
    protected <X> void applyToEntity(Sheep entity, ClientProperty<X> property, X value)
    {
        if (property.equals(COLOR))
            entity.setColor((DyeColor) value);
    }
}
