package xyz.nifeather.morph.server.storage;

import xyz.nifeather.morph.server.misc.DisguiseMeta;
import xyz.nifeather.morph.server.storage.playerdata.paper.PlayerMeta;

import java.util.List;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public class CompoundBasedPlayerStore implements IManagePlayerData
{
    @Override
    public List<DisguiseMeta> getAvaliableDisguisesFor(ServerPlayer player)
    {
        return List.of();
    }

    @Override
    public boolean grantMorphToPlayer(ServerPlayer player, String disguiseIdentifier)
    {
        return false;
    }

    @Override
    public boolean revokeMorphFromPlayer(ServerPlayer player, String disguiseIdentifier)
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
