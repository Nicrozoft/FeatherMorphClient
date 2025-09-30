package xyz.nifeather.morph.client;

import xyz.nifeather.morph.FeatherMorphCommonBootstrap;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record SavedDisguise(String identifier, Map<String, String> properties)
{
    public static SavedDisguise fromSyncer(DisguiseSyncer syncer)
    {
        Map<String, String> propertyMap = new ConcurrentHashMap<>();
        syncer.getProperties().forEach((p, value) ->
        {
            var property = (ClientProperty<Object>) p;
            String result = null;
            try
            {
                result = property.handleOutput(value);
            }
            catch (Exception e)
            {
                FeatherMorphClientBootstrap.LOGGER.error("???", e);
            }

            propertyMap.put(property.identifier(), result);
        });

        return new SavedDisguise(syncer.disguiseIdentifier(), propertyMap);
    }
}
