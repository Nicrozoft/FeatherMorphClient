package xyz.nifeather.morph.server.morphs.providers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.morphs.FabricDisguiseSession;

import java.util.Arrays;
import java.util.List;

public class PlayerDisguiseProvider extends AbstractDisguiseProvider
{
    @Override
    public String namespace()
    {
        return "player";
    }

    @Override
    public List<String> availableDisguises()
    {
        assert MorphServerLoader.mcserver != null;

        var list = new ObjectArrayList<String>();
        list.addAll(Arrays.asList(MorphServerLoader.mcserver.getPlayerNames()));

        return list;
    }

    @Override
    public boolean isValid(String identifier)
    {
        return identifier.startsWith("player:");
    }

    @Override
    public boolean disguise(PlayerEntity player, String disguiseIdentifier)
    {
        return true;
    }

    @Override
    public boolean unDisguise(PlayerEntity player)
    {
        return true;
    }

    @Override
    public boolean updateDisguise(PlayerEntity player, FabricDisguiseSession disguiseSession)
    {
        return true;
    }

    @Override
    public void onDisguiseApplied(FabricDisguiseSession disguiseSession)
    {
    }

    @Override
    public Text getDisplayName(String disguiseIdentifier)
    {
        return Text.literal(disguiseIdentifier.replace("player:", ""));
    }
}
