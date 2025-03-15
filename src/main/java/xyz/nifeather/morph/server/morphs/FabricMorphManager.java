package xyz.nifeather.morph.server.morphs;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xiamomc.morph.network.commands.S2C.S2CCurrentCommand;
import xiamomc.morph.network.commands.S2C.clientrender.S2CRenderMapAddCommand;
import xiamomc.morph.network.commands.S2C.clientrender.S2CRenderMapRemoveCommand;
import xiamomc.morph.network.commands.S2C.map.S2CMapRemoveCommand;
import xiamomc.morph.network.commands.S2C.map.S2CPartialMapCommand;
import xiamomc.morph.network.commands.S2C.set.S2CSetAvailableAnimationsCommand;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.client.AnimationNames;
import xyz.nifeather.morph.server.misc.DisguiseMeta;
import xyz.nifeather.morph.server.misc.DisguiseTypes;
import xyz.nifeather.morph.server.morphs.providers.AbstractDisguiseProvider;
import xyz.nifeather.morph.server.morphs.providers.FallbackDisguiseProvider;
import xyz.nifeather.morph.server.morphs.providers.PlayerDisguiseProvider;
import xyz.nifeather.morph.server.morphs.providers.VanillaDisguiseProvider;
import xyz.nifeather.morph.server.network.FabricClientHandler;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.storage.playerdata.paper.PlayerDataStoreNew;
import xyz.nifeather.morph.shared.exceptions.AlreadyRegisteredException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FabricMorphManager extends ServerPluginObject
{
    public FabricMorphManager()
    {
        fallbackProvider = new FallbackDisguiseProvider();

        registerDisguiseProvider(new VanillaDisguiseProvider());
        registerDisguiseProvider(new PlayerDisguiseProvider());
        registerDisguiseProvider(fallbackProvider);
    }

    private final Map<PlayerEntity, FabricDisguiseSession> disguiseSessionMap = new ConcurrentHashMap<>();

    public boolean playerDisguised(PlayerEntity player)
    {
        return disguiseSessionMap.containsKey(player);
    }

    @Nullable
    public FabricDisguiseSession getSessionFor(PlayerEntity player)
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

    public List<String> getUnlockedDisguiseIds(PlayerEntity player)
    {
        var meta = dataStore.getPlayerMeta(player.getUuid());

        return meta.getUnlockedDisguiseIdentifiers();
    }

    public boolean grantDisguiseToPlayer(ServerPlayerEntity player, String disguiseIdentifier)
    {
        var success = dataStore.grantMorphToPlayer(player, disguiseIdentifier);

        if (!success)
            return false;

        clientHandler.sendDiff(List.of(disguiseIdentifier), null, player);
        //multiInstanceService.notifyDisguiseMetaChange(player.getUniqueId(), Operation.ADD_IF_ABSENT, disguiseIdentifier);

        var meta = this.getDisguiseMetaFrom(disguiseIdentifier);

        var message = Text.translatableWithFallback("morph.disguise_unlocked", "Unlocked disguise of %s!", meta.asComponent());
        player.sendMessage(message);

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

    public boolean revokeDisguiseFromPlayer(ServerPlayerEntity player, String disguiseIdentifier)
    {
        var success = dataStore.revokeMorphFromPlayer(player, disguiseIdentifier);

        if (success)
        {
            clientHandler.sendDiff(null, List.of(disguiseIdentifier), player);
            //multiInstanceService.notifyDisguiseMetaChange(player.getUniqueId(), Operation.REMOVE, disguiseIdentifier);

            var meta = this.getDisguiseMetaFrom(disguiseIdentifier);

            var message = Text.translatableWithFallback("morph.disguise_lost", "Lost disguise of %s!", meta.asComponent());
            player.sendMessage(message);

            var disguiseState = this.getSessionFor(player);
            if (disguiseState != null && disguiseState.disguiseIdentifier().equalsIgnoreCase(disguiseIdentifier))
                this.unMorph(player);
        }

        return success;
    }

    //endregion Data Access

    public boolean morph(ServerPlayerEntity player, String identifier)
    {
        return morph(player, identifier, false);
    }

    public boolean morph(ServerPlayerEntity player,
                      String identifier,
                      boolean bypassAvailableCheck)
    {
        var idSplit = identifier.split(":", 2);
        var idNamespace = idSplit.length == 2 ? idSplit[0] : "minecraft";
        var provider = disguiseProviders.get(idNamespace);

        if (provider == null)
        {
            player.sendMessage(Text.translatableWithFallback("morph.error.invalid_namespace", "Error: Invalid namespace \"%s\"", idNamespace), false);
            return false;
        }

        if (!provider.isValid(identifier))
        {
            player.sendMessage(Text.translatableWithFallback("morph.error.invalid_id", "Error: Identifier \"%s\" not valid for \"%s\"", identifier, idNamespace), false);
            return false;
        }

        var available = getUnlockedDisguiseIds(player);
        if (!bypassAvailableCheck && !available.contains(identifier))
        {
            player.sendMessage(Text.translatableWithFallback("morph.error.not_unlocked", "Error: That disguise is not unlocked yet"), false);
            return false;
        }

        // todo: Morph stuffs

        disguiseSessionMap.put(player, new FabricDisguiseSession(player, identifier));

        clientHandler.sendCommand(player, new S2CCurrentCommand(identifier));
        clientHandler.sendCommand(player, new S2CSetAvailableAnimationsCommand(
                AnimationNames.CRAWL,
                AnimationNames.DIGDOWN,
                AnimationNames.LAY,
                AnimationNames.DANCE
        ));

        var cmd = new S2CRenderMapAddCommand(player.getId(), identifier);

        Map<Integer, String> revealMap = new Object2ObjectOpenHashMap<>();
        revealMap.put(player.getId(), player.getName().getString());

        var cmdReveal = new S2CPartialMapCommand(revealMap);
        for (ServerPlayerEntity serverPlayerEntity : MorphServerLoader.mcserver.getPlayerManager().getPlayerList())
        {
            clientHandler.sendCommand(serverPlayerEntity, cmd);
            clientHandler.sendCommand(serverPlayerEntity, cmdReveal);
        }

        spawnParticle(player);

        player.getWorld().playSound(
                player,
                BlockPos.ofFloored(player.getPos()),
                SoundEvents.UI_LOOM_TAKE_RESULT,
                SoundCategory.PLAYERS,
                1, 1
        );

        player.sendMessage(Text.translatableWithFallback("morph.disguising_as", "Disguising as %s", provider.getDisplayName(identifier)));

        return true;
    }

    public void spawnParticle(ServerPlayerEntity player)
    {
        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) return;

        double collX, collY, collZ;

        collX = player.getBoundingBox().getLengthX();
        collY = player.getBoundingBox().getLengthY();
        collZ = player.getBoundingBox().getLengthZ();

        var location = player.getPos().add(0, collY / 2, 0);

        //根据碰撞箱计算粒子数量缩放
        //缩放为碰撞箱体积的1/15，最小为1
        var particleScale = Math.max(1, (collX * collY * collZ) / 15);

        player.getWorld().addParticle(ParticleTypes.CLOUD,
                location.x, location.y, location.z,
                0.05, 0.05, 0.05);

        ((ServerWorld)player.getWorld()).spawnParticles(ParticleTypes.CLOUD,
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

    public void unMorph(ServerPlayerEntity player)
    {
        if (!playerDisguised(player))
            return;

        disguiseSessionMap.remove(player);

        clientHandler.sendCommand(player, new S2CCurrentCommand(null));

        var cmd = new S2CRenderMapRemoveCommand(player.getId());
        var cmdReveal = new S2CMapRemoveCommand(player.getId());
        for (ServerPlayerEntity serverPlayerEntity : MorphServerLoader.mcserver.getPlayerManager().getPlayerList())
        {
            clientHandler.sendCommand(serverPlayerEntity, cmd);
            clientHandler.sendCommand(serverPlayerEntity, cmdReveal);
        }

        spawnParticle(player);

        player.sendMessage(Text.literal("Undisguised"));
    }

    public void dispose()
    {
        logger.info("Disposing FabricMorphManager");
    }
}
