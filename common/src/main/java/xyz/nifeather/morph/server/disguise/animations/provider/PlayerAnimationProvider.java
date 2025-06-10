package xyz.nifeather.morph.server.disguise.animations.provider;

import org.jetbrains.annotations.NotNull;
import xyz.nifeather.morph.server.disguise.animations.AnimationProvider;
import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.bundled.PlayerAnimationSet;

public class PlayerAnimationProvider extends AnimationProvider
{
    private final AnimationSet playerAnimationSet = new PlayerAnimationSet();

    @Override
    public @NotNull AnimationSet getAnimationSetFor(String disguiseID)
    {
        return playerAnimationSet;
    }
}
