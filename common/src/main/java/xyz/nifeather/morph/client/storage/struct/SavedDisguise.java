package xyz.nifeather.morph.client.storage.struct;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record SavedDisguise(@Expose String disguiseIdentifier, @Expose Map<String, String> properties)
{
    private static final Gson gson = new GsonBuilder().create();

    public String asJsonString()
    {
        return gson.toJson(this);
    }

    public static SavedDisguise read(String json)
    {
        return gson.fromJson(json, SavedDisguise.class);
    }

    public static SavedDisguise fromSyncer(DisguiseSyncer syncer)
    {
        Map<String, String> propertyMap = new ConcurrentHashMap<>();
        syncer.cachedNetworkProperties().forEach((p, value) ->
        {
            propertyMap.put(p, value);

            /*
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

            propertyMap.put(property.identifier(), result);*/
        });

        System.out.println("Propertues are " + propertyMap);

        return new SavedDisguise(syncer.disguiseIdentifier(), propertyMap);
    }
}
