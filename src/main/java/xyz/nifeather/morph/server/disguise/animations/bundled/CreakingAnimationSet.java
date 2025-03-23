package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class CreakingAnimationSet extends AnimationSet
{
    public final SingleAnimation EYE_GLOW = new SingleAnimation(AnimationNames.MAKE_ACTIVE, 0, true);
    public final SingleAnimation DISABLE_EYE_GLOW = new SingleAnimation(AnimationNames.MAKE_INACTIVE, 0, true);

    public CreakingAnimationSet()
    {
        registerPersistent(AnimationNames.DISPLAY_EYE_GLOW, List.of(EYE_GLOW));
        registerPersistent(AnimationNames.DISPLAY_STOP_EYE_GLOW, List.of(DISABLE_EYE_GLOW));
    }
}
