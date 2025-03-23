package xyz.nifeather.morph.server.storage.playerdata.fabric;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import xyz.nifeather.morph.shared.SharedValues;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated 我们似乎无法在不使用mixin的情况下向玩家自己的 playerdata 保存数据
 */
@Deprecated
public class FabricPlayerDataLoader extends PersistentState
{
    //region Read/Write impl

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
    {
        return nbt;
    }

    public static final String TAGKEY_MASTER_COMPOUND = "feathermorph:playerdata";
    public static final String TAGKEY_UNLOCKED_DISGUISES = "unlocked_disguises";

    public static FabricPlayerDataLoader newInstance()
    {
        return new FabricPlayerDataLoader();
    }

    public static FabricPlayerDataLoader fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        var instance = new FabricPlayerDataLoader();
/*
        if (!(nbt.contains(TAGKEY_MASTER_COMPOUND)))
            return instance;

        var child = nbt.getCompound(TAGKEY_MASTER_COMPOUND);

        // Unlocked disguises
        if (child.contains(TAGKEY_UNLOCKED_DISGUISES))
        {
            var list = child.getList(TAGKEY_UNLOCKED_DISGUISES, NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); i++)
                instance.availableDisguiseIdentifiers.add(list.getString(i));
        }
*/
        return instance;
    }

    private static Type<FabricPlayerDataLoader> type = new Type<>(
            FabricPlayerDataLoader::newInstance, // If there's no 'StateSaverAndLoader' yet create one and refresh variables
            FabricPlayerDataLoader::fromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static FabricPlayerDataLoader getServerState(MinecraftServer server)
    {
        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();

        var state = persistentStateManager.getOrCreate(type, SharedValues.MOD_ID);

        state.markDirty();

        return state;
    }

    public Map<UUID, FabricPlayerData> players = new ConcurrentHashMap<>();

    public static FabricPlayerData getPlayerState(ServerPlayerEntity player)
    {
        var serverState = getServerState(player.getServer());

        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new FabricPlayerData());
    }

    //endregion Read/Write impl
}
