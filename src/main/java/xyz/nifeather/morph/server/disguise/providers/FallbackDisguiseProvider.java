package xyz.nifeather.morph.server.disguise.providers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import xyz.nifeather.morph.server.morphs.FabricDisguiseSession;
import xyz.nifeather.morph.server.disguise.animations.AnimationProvider;
import xyz.nifeather.morph.server.disguise.animations.provider.FallbackAnimationProvider;

import java.util.List;

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
    public boolean disguise(PlayerEntity player, String disguiseIdentifier)
    {
        return false;
    }

    @Override
    public boolean unDisguise(PlayerEntity player)
    {
        return false;
    }

    @Override
    public boolean updateDisguise(PlayerEntity player, FabricDisguiseSession disguiseSession)
    {
        return false;
    }

    @Override
    public void onDisguiseApplied(FabricDisguiseSession disguiseSession)
    {
    }

    @Override
    public Text getDisplayName(String disguiseIdentifier)
    {
        return Text.literal("[Fallback: %s]".formatted(disguiseIdentifier));
    }

    private final AnimationProvider animationProvider = new FallbackAnimationProvider();

    @Override
    public AnimationProvider getAnimationProvider()
    {
        return animationProvider;
    }
}
