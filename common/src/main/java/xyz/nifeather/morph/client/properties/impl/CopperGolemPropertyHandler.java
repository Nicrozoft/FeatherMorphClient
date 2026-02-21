package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.level.block.WeatheringCopper;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Arrays;
import java.util.Optional;

public class CopperGolemPropertyHandler extends EntityPropertyHandler<CopperGolem>
{
    public final ClientProperty<WeatheringCopper.WeatherState, CopperGolem> WEATHER_STATE =
            ClientProperty.builder(PropertyNames.COPPER_GOLEM_WEATHER_STATE, WeatheringCopper.WeatherState.UNAFFECTED, CopperGolem.class)
                    .inputHandle(this::readWeatherState)
                    .entityHandle(CopperGolem::setWeatherState)
                    .build();

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
}
