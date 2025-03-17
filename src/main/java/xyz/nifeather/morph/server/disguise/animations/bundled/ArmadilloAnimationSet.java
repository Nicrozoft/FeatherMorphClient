package xyz.nifeather.morph.server.disguise.animations.bundled;

import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.shared.AnimationNames;

import java.util.List;

public class ArmadilloAnimationSet extends AnimationSet
{
    public final SingleAnimation PANIC_ROLLING = new SingleAnimation(AnimationNames.PANIC_ROLLING, 10, true);
    public final SingleAnimation PANIC_SCARED = new SingleAnimation(AnimationNames.PANIC_SCARED, 50, true);
    public final SingleAnimation PANIC_UNROLLING = new SingleAnimation(AnimationNames.PANIC_UNROLLING, 30, true);
    public final SingleAnimation PANIC_IDLE = new SingleAnimation(AnimationNames.PANIC_IDLE, 0, true);

    public ArmadilloAnimationSet()
    {
        registerCommon(AnimationNames.PANIC, List.of(PANIC_ROLLING, PANIC_SCARED, PANIC_UNROLLING, PANIC_IDLE, RESET));
    }

    @Override
    public List<String> getAvailableAnimationsForClient()
    {
        return List.of(AnimationNames.PANIC);
    }
}
