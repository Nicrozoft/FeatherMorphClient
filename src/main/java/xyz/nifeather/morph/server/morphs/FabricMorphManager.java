package xyz.nifeather.morph.server.morphs;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.particle.Particle;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xiamomc.morph.network.commands.S2C.S2CCurrentCommand;
import xiamomc.morph.network.commands.S2C.clientrender.S2CRenderMapAddCommand;
import xiamomc.morph.network.commands.S2C.clientrender.S2CRenderMapRemoveCommand;
import xiamomc.morph.network.commands.S2C.map.S2CMapRemoveCommand;
import xiamomc.morph.network.commands.S2C.map.S2CPartialMapCommand;
import xiamomc.morph.network.commands.S2C.set.S2CSetAvailableAnimationsCommand;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.client.AnimationNames;
import xyz.nifeather.morph.server.morphs.providers.AbstractDisguiseProvider;
import xyz.nifeather.morph.server.morphs.providers.PlayerDisguiseProvider;
import xyz.nifeather.morph.server.morphs.providers.VanillaDisguiseProvider;
import xyz.nifeather.morph.server.network.FabricClientHandler;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.shared.exceptions.AlreadyRegisteredException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FabricMorphManager extends ServerPluginObject
{
    public FabricMorphManager()
    {
    }

    @Initializer
    private void load()
    {
        registerDisguiseProvider(new VanillaDisguiseProvider());
        registerDisguiseProvider(new PlayerDisguiseProvider());
    }

    public List<String> getUnlockedDisguises(PlayerEntity player)
    {
        var list = new ObjectArrayList<String>();

        for (AbstractDisguiseProvider provider : disguiseProviders.values())
            list.addAll(provider.availableDisguises());

        return list;
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

    //endregion Disguise provider

    public void morph(ServerPlayerEntity player, String identifier)
    {
        var idSplit = identifier.split(":", 2);
        var idNamespace = idSplit.length == 2 ? idSplit[0] : "minecraft";
        var provider = disguiseProviders.get(idNamespace);

        if (provider == null)
        {
            player.sendMessage(Text.translatableWithFallback("morph.error.invalid_namespace", "Error: Invalid namespace \"%s\"", idNamespace), false);
            return;
        }

        if (!provider.isValid(identifier))
        {
            player.sendMessage(Text.translatableWithFallback("morph.error.invalid_id", "Error: Identifier \"%s\" not valid for \"%s\"", identifier, idNamespace), false);
            return;
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
