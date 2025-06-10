package xyz.nifeather.morph.server.disguise.providers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.morphs.DisguiseSession;
import xyz.nifeather.morph.server.disguise.animations.provider.PlayerAnimationProvider;

import java.util.Arrays;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

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
    public boolean disguise(Player player, String disguiseIdentifier)
    {
        return true;
    }

    @Override
    public boolean unDisguise(Player player)
    {
        return true;
    }

    @Override
    public boolean updateDisguise(Player player, DisguiseSession disguiseSession)
    {
        return true;
    }

    @Override
    public void onDisguiseApplied(DisguiseSession disguiseSession)
    {
    }

    @Override
    public Component getDisplayName(String disguiseIdentifier)
    {
        return Component.literal(disguiseIdentifier.replace("player:", ""));
    }

    private final PlayerAnimationProvider animationProvider = new PlayerAnimationProvider();

    @Override
    public PlayerAnimationProvider getAnimationProvider()
    {
        return animationProvider;
    }
}
