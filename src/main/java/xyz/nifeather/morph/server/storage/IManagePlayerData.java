package xyz.nifeather.morph.server.storage;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nifeather.morph.server.misc.DisguiseMeta;
import xyz.nifeather.morph.server.storage.playerdata.paper.PlayerMeta;

import java.util.List;
import java.util.UUID;

public interface IManagePlayerData
{
    /**
     * 获取某一玩家所有可用的伪装
     * @param player 目标玩家
     * @return 目标玩家拥有的伪装
     */
    public List<DisguiseMeta> getAvaliableDisguisesFor(ServerPlayerEntity player);

    /**
     * 将伪装授予某一玩家
     * @param player 要授予的玩家
     * @param disguiseIdentifier 伪装ID
     * @return 添加是否成功（伪装是否可用或玩家是否已经拥有目标伪装）
     */
    public boolean grantMorphToPlayer(ServerPlayerEntity player, String disguiseIdentifier);

    /**
     * 从某一玩家剥离伪装
     * @param player 要授予的玩家
     * @param disguiseIdentifier 伪装ID
     * @return 添加是否成功（伪装是否可用或玩家是否已经拥有目标伪装）
     */
    public boolean revokeMorphFromPlayer(ServerPlayerEntity player, String disguiseIdentifier);

    /**
     * 获取玩家的伪装配置
     * @param uuid 玩家UUID
     * @return 伪装信息
     */
    public PlayerMeta getPlayerMeta(UUID uuid);

    public boolean reloadConfiguration();

    public boolean saveConfiguration();
}
