package xyz.nifeather.morph.client.storage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.io.FileUtils;
import xyz.nifeather.morph.client.storage.struct.SavedDisguise;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SavedDisguiseStorage extends ClientDirectoryJsonBasedStorage<SavedDisguise>
{
    public SavedDisguiseStorage()
    {
        super("saved_disguises");
    }

    @Override
    public SavedDisguise getDefault()
    {
        return new SavedDisguise("minecraft:allay", Map.of());
    }

    public boolean save(SavedDisguise savedDisguise, String id)
    {
        var path = this.getPath(id) + ".json";
        var file = this.clientDirectoryStorage.getFile(path, true);
        if (file == null)
        {
            logger.error("Can't create storage file at '%s' for unknown reason".formatted(path));
            return false;
        }

        try
        {
            FileUtils.writeStringToFile(file, savedDisguise.asJsonString(), StandardCharsets.UTF_8);
            return true;
        }
        catch (Throwable t)
        {
            logger.error("Can't write content to file", t);
            return false;
        }
    }

    public boolean drop(String id)
    {
        var file = getFile(id);
        if (!file.exists()) return false;

        return file.delete();
    }

    public List<String> listAll()
    {
        List<String> list = new ObjectArrayList<>();
        for (File file : this.clientDirectoryStorage.getFiles())
        {
            var key = this.getKeyFromFile(file);
            list.add(key);
        }

        return list;
    }
}
