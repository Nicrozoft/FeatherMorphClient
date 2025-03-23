package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class PufferfishAnimationSet extends AnimationSet
{
    public final SingleAnimation INFLATE = new SingleAnimation(AnimationNames.INFLATE, 0, true);
    public final SingleAnimation DEFLATE = new SingleAnimation(AnimationNames.DEFLATE, 0, true);

    public PufferfishAnimationSet()
    {
        registerPersistent(AnimationNames.INFLATE, List.of(INFLATE));

        registerCommon(AnimationNames.DEFLATE, List.of(DEFLATE, RESET));
    }
}
