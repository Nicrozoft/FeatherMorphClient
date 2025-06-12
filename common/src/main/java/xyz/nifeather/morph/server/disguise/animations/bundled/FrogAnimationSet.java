package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class FrogAnimationSet extends AnimationSet
{
    public final SingleAnimation EAT = new SingleAnimation(AnimationNames.EAT, 10, true);

    public FrogAnimationSet()
    {
        registerCommon(AnimationNames.EAT, List.of(EAT, RESET));
    }
}
