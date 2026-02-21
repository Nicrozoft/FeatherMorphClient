package xyz.nifeather.morph.client.properties.impl;

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
    public final ClientProperty<Markings, HorseAccessor> STYLE =
            ClientProperty.builder(PropertyNames.HORSE_STYLE, Markings.NONE, HorseAccessor.class)
                    .inputHandle(this::readHorseStyle)
                    .entityHandle(this::applyStyle)
                    .build();

    private void applyStyle(HorseAccessor accessor, Markings markings)
    {
        var horse = (Horse) accessor;
        accessor.callSetVariantAndMarkings(horse.getVariant(), markings);
    }

    public final ClientProperty<Variant, HorseAccessor> COLOR =
            ClientProperty.builder(PropertyNames.HORSE_COLOR, Variant.BLACK, HorseAccessor.class)
                    .inputHandle(this::readHorseColor)
                    .entityHandle(this::applyColor)
                    .build();

    private void applyColor(HorseAccessor accessor, Variant variant)
    {
        var horse = (Horse) accessor;
        accessor.callSetVariantAndMarkings(variant, horse.getMarkings());
    }

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
}
