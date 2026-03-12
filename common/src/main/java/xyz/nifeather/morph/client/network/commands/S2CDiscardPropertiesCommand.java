package xyz.nifeather.morph.client.network.commands;

import org.jetbrains.annotations.ApiStatus;
import xyz.nifeather.morph.client.network.ServerHandler;
import xyz.nifeather.morph.network.BasicServerHandler;
import xyz.nifeather.morph.network.commands.S2C.AbstractS2CCommand;
import xyz.nifeather.morph.network.utils.Asserts;

import java.util.List;
import java.util.Map;

@ApiStatus.Experimental
public class S2CDiscardPropertiesCommand extends AbstractS2CCommand<String>
{
    private final List<String> propertyNames;

    public List<String> propertyNames()
    {
        return List.copyOf(propertyNames);
    }

    public S2CDiscardPropertiesCommand(List<String> properties)
    {
        this.propertyNames = List.copyOf(properties);
    }

    @Override
    public String getBaseName()
    {
        return "discard_properties";
    }

    @Override
    public void onCommand(BasicServerHandler<?> basicServerHandler)
    {
        if (basicServerHandler instanceof ServerHandler modServerHandler)
            modServerHandler.onDiscardPropertiesCommand(this);
    }

    @Override
    public Map<String, String> generateArgumentMap()
    {
        return Map.of(
                "properties", gson().toJson(propertyNames)
        );
    }

    public static S2CDiscardPropertiesCommand fromArguments(Map<String, String> arguments)
            throws RuntimeException
    {
        var propertyList = Asserts.getStringOrThrow(arguments, "properties");
        var list = gson().fromJson(propertyList, List.class).stream().map(Object::toString).toList();

        return new S2CDiscardPropertiesCommand(list);
    }
}
