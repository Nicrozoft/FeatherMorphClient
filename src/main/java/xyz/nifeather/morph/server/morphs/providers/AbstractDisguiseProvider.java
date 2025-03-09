package xyz.nifeather.morph.server.morphs.providers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.morphs.FabricDisguiseSession;

import java.util.List;

public abstract class AbstractDisguiseProvider extends ServerPluginObject
{
    public abstract String namespace();

    /**
     * Used for listing available disguises for players
     * @return Available disguise IDs, without namespace
     */
    public abstract List<String> availableDisguises();

    public abstract boolean isValid(String identifier);

    public abstract boolean disguise(PlayerEntity player, String disguiseIdentifier);
    public abstract boolean unDisguise(PlayerEntity player);

    public abstract boolean updateDisguise(PlayerEntity player, FabricDisguiseSession disguiseSession);

    public abstract void onDisguiseApplied(FabricDisguiseSession disguiseSession);

    public void onPostConstructDisguise(FabricDisguiseSession state, @Nullable Entity targetEntity)
    {
    }

    public abstract Text getDisplayName(String disguiseIdentifier);
}
