package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class ParrotAnimationSet extends AnimationSet
{
    public final SingleAnimation DANCE_START = new SingleAnimation(AnimationNames.DANCE_START, 10, true);
    public final SingleAnimation DANCE_STOP = new SingleAnimation(AnimationNames.STOP, 10, true);

    public ParrotAnimationSet()
    {
        registerPersistent(AnimationNames.DANCE, List.of(DANCE_START));
        registerPersistent(AnimationNames.STOP, List.of(DANCE_STOP));
    }
}
