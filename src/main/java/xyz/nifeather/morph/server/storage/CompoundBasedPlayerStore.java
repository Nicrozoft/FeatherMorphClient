package xyz.nifeather.morph.server.storage;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nifeather.morph.server.misc.DisguiseMeta;
import xyz.nifeather.morph.server.storage.playerdata.paper.PlayerMeta;

import java.util.List;
import java.util.UUID;

public class CompoundBasedPlayerStore implements IManagePlayerData
{
    @Override
    public List<DisguiseMeta> getAvaliableDisguisesFor(ServerPlayerEntity player)
    {
        return List.of();
    }

    @Override
    public boolean grantMorphToPlayer(ServerPlayerEntity player, String disguiseIdentifier)
    {
        return false;
    }

    @Override
    public boolean revokeMorphFromPlayer(ServerPlayerEntity player, String disguiseIdentifier)
    {
        return false;
    }

    @Override
    public PlayerMeta getPlayerMeta(UUID uuid)
    {
        return null;
    }

    @Override
    public boolean reloadConfiguration()
    {
        return false;
    }

    @Override
    public boolean saveConfiguration()
    {
        return false;
    }
}
