package xyz.nifeather.morph.server.disguise.animations.provider;

import xyz.nifeather.morph.server.disguise.animations.AnimationProvider;
import xyz.nifeather.morph.server.disguise.animations.AnimationSet;
import xyz.nifeather.morph.server.disguise.animations.bundled.FallbackAnimationSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.EntityType;

public abstract class DefaultAnimationProvider extends AnimationProvider
{
    protected final AnimationSet fallbackAnimationSet = new FallbackAnimationSet();

    // DisguiseID <-> AnimationSet
    protected final Map<String, AnimationSet> animSets = new ConcurrentHashMap<>();

    protected void registerAnimSet(EntityType<?> type, AnimationSet animationSet)
    {
        this.registerAnimSet(EntityType.getKey(type).toString(), animationSet);
    }

    protected void registerAnimSet(String disguiseIdentifier, AnimationSet animationSet)
    {
        animSets.put(disguiseIdentifier, animationSet);
    }
}
