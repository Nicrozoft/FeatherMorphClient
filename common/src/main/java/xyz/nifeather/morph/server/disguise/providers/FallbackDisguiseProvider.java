package xyz.nifeather.morph.server.disguise.providers;

import xyz.nifeather.morph.server.morphs.DisguiseSession;
import xyz.nifeather.morph.server.disguise.animations.AnimationProvider;
import xyz.nifeather.morph.server.disguise.animations.provider.FallbackAnimationProvider;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class FallbackDisguiseProvider extends AbstractDisguiseProvider
{
    @Override
    public String namespace()
    {
        return "fallback";
    }

    @Override
    public List<String> availableDisguises()
    {
        return List.of();
    }

    @Override
    public boolean isValid(String identifier)
    {
        return false;
    }

    @Override
    public boolean disguise(Player player, String disguiseIdentifier)
    {
        return false;
    }

    @Override
    public boolean unDisguise(Player player)
    {
        return false;
    }

    @Override
    public boolean updateDisguise(Player player, DisguiseSession disguiseSession)
    {
        return false;
    }

    @Override
    public void onDisguiseApplied(DisguiseSession disguiseSession)
    {
    }

    @Override
    public Component getDisplayName(String disguiseIdentifier)
    {
        return Component.literal("[Fallback: %s]".formatted(disguiseIdentifier));
    }

    private final AnimationProvider animationProvider = new FallbackAnimationProvider();

    @Override
    public AnimationProvider getAnimationProvider()
    {
        return animationProvider;
    }
}
