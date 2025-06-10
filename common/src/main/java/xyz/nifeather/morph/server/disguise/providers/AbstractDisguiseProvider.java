package xyz.nifeather.morph.server.disguise.providers;

import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.morphs.DisguiseSession;
import xyz.nifeather.morph.server.disguise.animations.AnimationProvider;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractDisguiseProvider extends ServerPluginObject
{
    public abstract String namespace();

    /**
     * Used for listing available disguises for players
     * @return Available disguise IDs, without namespace
     */
    public abstract List<String> availableDisguises();

    public abstract boolean isValid(String identifier);

    public abstract boolean disguise(Player player, String disguiseIdentifier);
    public abstract boolean unDisguise(Player player);

    public abstract boolean updateDisguise(Player player, DisguiseSession disguiseSession);

    public abstract void onDisguiseApplied(DisguiseSession disguiseSession);

    public void onPostConstructDisguise(DisguiseSession state, @Nullable Entity targetEntity)
    {
    }

    public abstract AnimationProvider getAnimationProvider();

    public String wrapId(String input)
    {
        return namespace() + ":" + input;
    }

    public abstract Component getDisplayName(String disguiseIdentifier);
}
