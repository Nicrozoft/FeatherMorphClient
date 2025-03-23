package xyz.nifeather.morph.server.disguise.animations.provider;

import org.jetbrains.annotations.NotNull;
import xyz.nifeather.morph.server.disguise.animations.AnimationProvider;
import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.bundled.FallbackAnimationSet;

public class FallbackAnimationProvider extends AnimationProvider
{
    private final AnimationSet fallback = new FallbackAnimationSet();

    @Override
    public @NotNull AnimationSet getAnimationSetFor(String disguiseID)
    {
        return fallback;
    }
}
