package xyz.nifeather.morph.server.disguise.animations;

import org.jetbrains.annotations.NotNull;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.disguise.animations.bundled.FallbackAnimationSet;

public abstract class AnimationProvider extends ServerPluginObject
{
    /**
     * @param disguiseID
     * @return The sequence of the given parameters, {@link FallbackAnimationSet} if invalid.
     */
    @NotNull
    public abstract AnimationSet getAnimationSetFor(String disguiseID);
}
