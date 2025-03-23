package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class AllayAnimationSet extends AnimationSet
{
    public final SingleAnimation ROLL_START = new SingleAnimation(AnimationNames.DANCE_START, 0, true);
    public final SingleAnimation ROLL_STOP = new SingleAnimation(AnimationNames.STOP, 0, true);

    public AllayAnimationSet()
    {
        registerPersistent(AnimationNames.DANCE, List.of(ROLL_START));

        registerCommon(AnimationNames.STOP, List.of(ROLL_STOP, RESET));
    }
}
