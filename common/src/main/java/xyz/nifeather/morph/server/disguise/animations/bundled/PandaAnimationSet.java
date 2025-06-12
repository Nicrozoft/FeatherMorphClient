package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class PandaAnimationSet extends AnimationSet
{
    public final SingleAnimation SIT = new SingleAnimation(AnimationNames.SIT, 5, true);
    public final SingleAnimation STANDUP = new SingleAnimation(AnimationNames.STANDUP, 5, true);

    public PandaAnimationSet()
    {
        registerPersistent(AnimationNames.SIT, List.of(SIT));

        registerCommon(AnimationNames.STANDUP, List.of(STANDUP, RESET));
    }
}
