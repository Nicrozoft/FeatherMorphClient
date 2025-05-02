package xyz.nifeather.morph.server.morphs;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.network.commands.S2C.S2CSetCurrentCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CAddAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.admin.reveal.S2CRemoveAdminRevealCommand;
import xyz.nifeather.morph.network.commands.S2C.clientrender.S2CCRRegisterCommand;
import xyz.nifeather.morph.network.commands.S2C.clientrender.S2CCRUnregisterCommand;
import xyz.nifeather.morph.network.commands.S2C.set.S2CSetAvailableAnimationsCommand;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.misc.DisguiseMeta;
import xyz.nifeather.morph.server.misc.DisguiseTypes;
import xyz.nifeather.morph.server.disguise.providers.AbstractDisguiseProvider;
import xyz.nifeather.morph.server.disguise.providers.FallbackDisguiseProvider;
import xyz.nifeather.morph.server.disguise.providers.PlayerDisguiseProvider;
import xyz.nifeather.morph.server.disguise.providers.VanillaDisguiseProvider;
import xyz.nifeather.morph.server.network.FabricClientHandler;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.storage.playerdata.paper.PlayerDataStoreNew;
import xyz.nifeather.morph.shared.exceptions.AlreadyRegisteredException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class FabricMorphManager extends ServerPluginObject
{
    public FabricMorphManager()
    {
        fallbackProvider = new FallbackDisguiseProvider();

        registerDisguiseProvider(new VanillaDisguiseProvider());
        registerDisguiseProvider(new PlayerDisguiseProvider());
        registerDisguiseProvider(fallbackProvider);

        this.addSchedule(this::update);
    }

    private final Map<ServerPlayer, FabricDisguiseSession> disguiseSessionMap = new ConcurrentHashMap<>();

    public boolean playerDisguised(ServerPlayer player)
    {
        return disguiseSessionMap.containsKey(player);
    }

    @Nullable
    public FabricDisguiseSession getSessionFor(ServerPlayer player)
    {
        return disguiseSessionMap.getOrDefault(player, null);
    }

    public List<FabricDisguiseSession> listAllSession()
    {
        return new ObjectArrayList<>(disguiseSessionMap.values());
    }

    @Resolved
    private FabricClientHandler clientHandler;

    //region Disguise provider

    private final Map<String, AbstractDisguiseProvider> disguiseProviders = new ConcurrentHashMap<>();

    public void registerDisguiseProvider(AbstractDisguiseProvider provider) throws AlreadyRegisteredException
    {
        if (disguiseProviders.containsKey(provider.namespace()))
            throw new AlreadyRegisteredException("Already have a provider with the namespace '%s'".formatted(provider.namespace()));

        disguiseProviders.put(provider.namespace(), provider);
    }

    private final FallbackDisguiseProvider fallbackProvider;

    public AbstractDisguiseProvider getProvider(String id)
    {
        if (id == null)
            return fallbackProvider;

        id += ":";
        var splitedId = id.split(":", 2);

        return disguiseProviders.values().stream().filter(p -> p.namespace().equals(splitedId[0])).findFirst().orElse(fallbackProvider);
    }

    public List<AbstractDisguiseProvider> listProviders()
    {
        return new ObjectArrayList<>(disguiseProviders.values());
    }

    //endregion Disguise provider

    //region DisguiseMeta

    private final Map<String, DisguiseMeta> disguiseMetaCache = new ConcurrentHashMap<>();

    public DisguiseMeta getDisguiseMetaFrom(String identifier)
    {
        var cached = disguiseMetaCache.getOrDefault(identifier, null);
        if (cached != null)
            return cached;

        var provider = this.getProvider(identifier);
        var type = DisguiseTypes.fromId(identifier);

        var newInstance = new DisguiseMeta(identifier, type, provider);
        disguiseMetaCache.put(identifier, newInstance);

        return newInstance;
    }

    //endregion DisguiseMeta

    //region Data Access

    private final PlayerDataStoreNew dataStore = new PlayerDataStoreNew();

    public List<String> getUnlockedDisguiseIds(Player player)
    {
        var meta = dataStore.getPlayerMeta(player.getUUID());

        return meta.getUnlockedDisguiseIdentifiers();
    }

    public boolean grantDisguiseToPlayer(ServerPlayer player, String disguiseIdentifier)
    {
        var success = dataStore.grantMorphToPlayer(player, disguiseIdentifier);

        if (!success)
            return false;

        clientHandler.sendDiff(List.of(disguiseIdentifier), null, player);
        //multiInstanceService.notifyDisguiseMetaChange(player.getUniqueId(), Operation.ADD_IF_ABSENT, disguiseIdentifier);

        var meta = this.getDisguiseMetaFrom(disguiseIdentifier);

        var message = Component.translatableWithFallback("morph.disguise_unlocked", "Unlocked disguise of %s!", meta.asComponent());
        player.sendSystemMessage(message);

/*
        var config = dataStore.getPlayerMeta(player.getUuid());
        if (clientHandler.clientConnected(player))
        {
            if (!config.shownMorphClientHint)
            {
                player.sendMessage(MessageUtils.prefixes(player, HintStrings.firstGrantClientHintString()));
                config.shownMorphClientHint = true;
            }
        }
        else if (!config.shownMorphHint)
        {
            player.sendMessage(MessageUtils.prefixes(player, HintStrings.firstGrantHintString()));
            config.shownMorphHint = true;
        }
*/
        return success;
    }

    public boolean revokeDisguiseFromPlayer(ServerPlayer player, String disguiseIdentifier)
    {
        var success = dataStore.revokeMorphFromPlayer(player, disguiseIdentifier);

        if (success)
        {
            clientHandler.sendDiff(null, List.of(disguiseIdentifier), player);
            //multiInstanceService.notifyDisguiseMetaChange(player.getUniqueId(), Operation.REMOVE, disguiseIdentifier);

            var meta = this.getDisguiseMetaFrom(disguiseIdentifier);

            var message = Component.translatableWithFallback("morph.disguise_lost", "Lost disguise of %s!", meta.asComponent());
            player.sendSystemMessage(message);

            var disguiseState = this.getSessionFor(player);
            if (disguiseState != null && disguiseState.disguiseIdentifier().equalsIgnoreCase(disguiseIdentifier))
                this.unMorph(player);
        }

        return success;
    }

    //endregion Data Access

    public boolean morph(ServerPlayer player, String identifier)
    {
        return morph(player, identifier, false);
    }

    public boolean morph(ServerPlayer player,
                      String identifier,
                      boolean bypassAvailableCheck)
    {
        var idSplit = identifier.split(":", 2);
        var idNamespace = idSplit.length == 2 ? idSplit[0] : "minecraft";
        var provider = disguiseProviders.get(idNamespace);

        if (provider == null)
        {
            player.displayClientMessage(Component.translatableWithFallback("morph.error.invalid_namespace", "Error: Invalid namespace \"%s\"", idNamespace), false);
            return false;
        }

        if (!provider.isValid(identifier))
        {
            player.displayClientMessage(Component.translatableWithFallback("morph.error.invalid_id", "Error: Identifier \"%s\" not valid for \"%s\"", identifier, idNamespace), false);
            return false;
        }

        var available = getUnlockedDisguiseIds(player);
        if (!bypassAvailableCheck && !available.contains(identifier))
        {
            player.displayClientMessage(Component.translatableWithFallback("morph.error.not_unlocked", "Error: That disguise is not unlocked yet"), false);
            return false;
        }

        // todo: Morph stuffs

        disguiseSessionMap.put(player, new FabricDisguiseSession(player, identifier, provider));

        var availableAnimations = provider.getAnimationProvider().getAnimationSetFor(identifier).getAvailableAnimationsForClient();

        clientHandler.sendCommand(player, new S2CSetCurrentCommand(identifier));
        clientHandler.sendCommand(player, new S2CSetAvailableAnimationsCommand(availableAnimations));

        var cmd = new S2CCRRegisterCommand(player.getId(), identifier);

        Map<Integer, String> revealMap = new Object2ObjectOpenHashMap<>();
        revealMap.put(player.getId(), player.getName().getString());

        var cmdReveal = new S2CAddAdminRevealCommand(revealMap);
        for (ServerPlayer serverPlayerEntity : MorphServerLoader.mcserver.getPlayerList().getPlayers())
        {
            clientHandler.sendCommand(serverPlayerEntity, cmd);
            clientHandler.sendCommand(serverPlayerEntity, cmdReveal);
        }

        spawnParticle(player);

        player.level().playSound(
                player,
                BlockPos.containing(player.position()),
                SoundEvents.UI_LOOM_TAKE_RESULT,
                SoundSource.PLAYERS,
                1, 1
        );

        player.sendSystemMessage(Component.translatableWithFallback("morph.disguising_as", "Disguising as %s", provider.getDisplayName(identifier)));

        return true;
    }

    public void spawnParticle(ServerPlayer player)
    {
        if (player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) return;

        double collX, collY, collZ;

        collX = player.getBoundingBox().getXsize();
        collY = player.getBoundingBox().getYsize();
        collZ = player.getBoundingBox().getZsize();

        var location = player.position().add(0, collY / 2, 0);

        //根据碰撞箱计算粒子数量缩放
        //缩放为碰撞箱体积的1/15，最小为1
        var particleScale = Math.max(1, (collX * collY * collZ) / 15);

        ((ServerLevel)player.level()).sendParticles(ParticleTypes.CLOUD,
                false,
                false,
                location.x, location.y, location.z,
                (int) (25 * particleScale),
                collX * 0.6, collY / 4, collZ * 0.6, //分布空间
                particleScale >= 10 ? 0.2 : 0.05); //速度

        //显示粒子
        //player.getWorld().addParticle(ParticleTypes.CLOUD, location.x, location.y, location.z, //类型和位置
        //        (int) (25 * particleScale), //数量
        //        collX * 0.6, collY / 4, collZ * 0.6, //分布空间
        //        particleScale >= 10 ? 0.2 : 0.05); //速度
    }

    public void unMorph(ServerPlayer player)
    {
        if (!playerDisguised(player))
            return;

        disguiseSessionMap.remove(player);

        if (player.hasDisconnected())
            return;

        clientHandler.sendCommand(player, new S2CSetCurrentCommand(null));
        clientHandler.sendCommand(player, new S2CSetAvailableAnimationsCommand(List.of()));

        var cmd = new S2CCRUnregisterCommand(player.getId());
        var cmdReveal = new S2CRemoveAdminRevealCommand(player.getId());
        for (ServerPlayer serverPlayerEntity : MorphServerLoader.mcserver.getPlayerList().getPlayers())
        {
            clientHandler.sendCommand(serverPlayerEntity, cmd);
            clientHandler.sendCommand(serverPlayerEntity, cmdReveal);
        }

        spawnParticle(player);

        player.sendSystemMessage(Component.literal("Undisguised"));
    }

    public void update()
    {
        this.addSchedule(this::update);

        disguiseSessionMap.forEach((player, session) ->
        {
            if (player.hasDisconnected())
                unMorph(player);
            else
                session.update();
        });
    }

    public void dispose()
    {
        logger.info("Disposing FabricMorphManager");
    }
}
