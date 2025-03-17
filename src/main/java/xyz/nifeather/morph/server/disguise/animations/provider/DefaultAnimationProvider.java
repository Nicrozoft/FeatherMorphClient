package xyz.nifeather.morph.server.disguise.animations.provider;

import net.minecraft.entity.EntityType;
import xyz.nifeather.morph.server.disguise.animations.AnimationProvider;
import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.bundled.FallbackAnimationSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DefaultAnimationProvider extends AnimationProvider
{
    protected final AnimationSet fallbackAnimationSet = new FallbackAnimationSet();

    // DisguiseID <-> AnimationSet
    protected final Map<String, AnimationSet> animSets = new ConcurrentHashMap<>();

    protected void registerAnimSet(EntityType<?> type, AnimationSet animationSet)
    {
        this.registerAnimSet(EntityType.getId(type).toString(), animationSet);
    }

    protected void registerAnimSet(String disguiseIdentifier, AnimationSet animationSet)
    {
        animSets.put(disguiseIdentifier, animationSet);
    }
}
