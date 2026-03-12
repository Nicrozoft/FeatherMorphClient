package xyz.nifeather.morph.client.properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.entity.Pose;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommonOutputHandles
{
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static <X> Optional<String> noOp(X x)
    {
        return Optional.empty();
    }

    public static Optional<String> writeBoolean(boolean bl)
    {
        return Optional.of(Boolean.toString(bl).toLowerCase());
    }

    public static Optional<String> writeEnum(Enum<?> e)
    {
        return Optional.of(e.name().toLowerCase());
    }

    public static Optional<String> writeFloat(Float aFloat)
    {
        return Optional.of(Float.toString(aFloat));
    }

    public static Optional<String> writeInteger(Integer integer)
    {
        return Optional.of(Integer.toString(integer));
    }

    public static Optional<String> writeVector3i(Vector3i vector3i)
    {
        List<Integer> list = new ArrayList<>();
        list.add(vector3i.x());
        list.add(vector3i.y());
        list.add(vector3i.z());

        return Optional.of(gson.toJson(list));
    }
}
