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
}
