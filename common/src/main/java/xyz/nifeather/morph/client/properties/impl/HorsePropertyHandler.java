package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;
import xyz.nifeather.morph.client.mixin.accessors.HorseAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Arrays;
import java.util.Optional;

public class HorsePropertyHandler extends EntityPropertyHandler<Horse>
{
    public final ClientProperty<Markings> STYLE = ClientProperty.of(PropertyNames.HORSE_STYLE, this::readHorseStyle);
    public final ClientProperty<Variant> COLOR = ClientProperty.of(PropertyNames.HORSE_COLOR, this::readHorseColor);

    private Optional<Markings> readHorseStyle(String string)
    {
        return Arrays.stream(Markings.values()).filter(m -> m.name().equalsIgnoreCase(string))
                .findFirst();
    }

    private Optional<Variant> readHorseColor(String string)
    {
        return Arrays.stream(Variant.values()).filter(v -> v.name().equalsIgnoreCase(string))
                .findFirst();
    }

    public HorsePropertyHandler()
    {
        register(STYLE, COLOR);
    }

    @Override
    public Optional<Horse> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Horse horse ? horse : null);
    }

    @Override
    protected <X> void applyToEntity(Horse entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        switch (property.identifier())
        {
            case PropertyNames.HORSE_COLOR -> ((HorseAccessor)entity).callSetVariantAndMarkings((Variant) value, entity.getMarkings());
            case PropertyNames.HORSE_STYLE -> ((HorseAccessor)entity).callSetVariantAndMarkings(entity.getVariant(), (Markings) value);
        }
    }
}
