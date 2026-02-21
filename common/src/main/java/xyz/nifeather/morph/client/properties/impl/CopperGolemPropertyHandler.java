package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.level.block.WeatheringCopper;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Arrays;
import java.util.Optional;

public class CopperGolemPropertyHandler extends EntityPropertyHandler<CopperGolem>
{
    public final ClientProperty<WeatheringCopper.WeatherState> WEATHER_STATE = ClientProperty.of(PropertyNames.COPPER_GOLEM_WEATHER_STATE, this::readWeatherState);

    private Optional<WeatheringCopper.WeatherState> readWeatherState(String input)
    {
        return Arrays.stream(WeatheringCopper.WeatherState.values())
                .filter(ws -> ws.name().equals(input.toUpperCase()))
                .findFirst();
    }

    public CopperGolemPropertyHandler()
    {
        register(WEATHER_STATE);
    }

    @Override
    public Optional<CopperGolem> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof CopperGolem copperGolem ? copperGolem : null);
    }

    @Override
    protected <X> void applyToEntity(CopperGolem entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, syncer, property, value);

        if (property.equals(WEATHER_STATE))
            entity.setWeatherState((WeatheringCopper.WeatherState) value);
    }
}
