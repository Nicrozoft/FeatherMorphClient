package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.mixin.accessors.ShulkerAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ShulkerPropertyHandler extends LivingEntityPropertyHandler<Shulker>
{
    public final ClientProperty<DyeColor> COLOR = ClientProperty.of(PropertyNames.SHULKER_COLOR, s -> CommonInputHandles.readEnum(DyeColor.values(), s));

    public ShulkerPropertyHandler()
    {
        register(COLOR);
    }

    @Override
    public Optional<Shulker> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Shulker shulker ? shulker : null);
    }

    @Override
    protected <X> void applyToEntity(Shulker entity, ClientProperty<X> property, X value)
    {
        if (property.equals(COLOR))
            ((ShulkerAccessor)entity).callSetVariant(Optional.of((DyeColor) value));
    }
}
