package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class SnifferAnimationSet extends AnimationSet
{
    private final SingleAnimation SNIFF = new SingleAnimation(AnimationNames.SNIFF, 20, true);

    public SnifferAnimationSet()
    {
        registerCommon(AnimationNames.SNIFF, List.of(SNIFF, RESET));
    }
}
