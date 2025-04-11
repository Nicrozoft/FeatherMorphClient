package xyz.nifeather.morph.client.entities;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.client.graphics.capes.ICapeProvider;
import xyz.nifeather.morph.client.graphics.capes.providers.KappaCapeProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class MorphLocalPlayer extends RemotePlayer
{
    private final Tuple<Integer, GameProfile> currentProfilePair = new Tuple<>(0, null);

    private final String playerName;

    @NotNull
    private ResourceLocation morphTextureIdentifier = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");

    @Nullable
    private ResourceLocation capeTextureIdentifier;

    @Nullable
    private ResourceLocation ofCapeIdentifier;

    @Nullable
    private String skinTextureUrl;

    @NotNull
    private PlayerSkin.Model model = PlayerSkin.Model.SLIM;

    public boolean personEquals(MorphLocalPlayer other)
    {
        return other.playerName.equals(this.playerName);
    }

    public void copyFrom(MorphLocalPlayer prevPlayer)
    {
        requestId++;

        this.capeTextureIdentifier = prevPlayer.capeTextureIdentifier;
        this.morphTextureIdentifier = prevPlayer.morphTextureIdentifier;

        this.model = prevPlayer.model;

        this.currentProfilePair.setA(requestId);
        this.currentProfilePair.setB(prevPlayer.currentProfilePair.getB());

        this.skinTextureUrl = prevPlayer.skinTextureUrl;
    }

    @Nullable
    private Player bindingPlayer;

    @Nullable
    public Player getBindingPlayer()
    {
        return bindingPlayer;
    }

    public boolean hasBindingPlayer()
    {
        return bindingPlayer != null;
    }

    public void setBindingPlayer(@Nullable Player newInstance)
    {
        bindingPlayer = newInstance;
    }

    public MorphLocalPlayer(ClientLevel clientWorld, GameProfile profile, @Nullable Player bindingPlayer)
    {
        super(clientWorld, profile);

        this.setBindingPlayer(bindingPlayer);

        //if (bindingPlayer == null) bindingPlayer = MinecraftClient.getInstance().player;
        //this.bindingPlayer = bindingPlayer;

        this.playerName = profile.getName();

        currentProfilePair.setA(0);
        currentProfilePair.setB(profile);

        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)127);
        this.updateSkin(profile, true);
    }

    private int requestId = 0;

    private final static ICapeProvider customCapeProvider = new KappaCapeProvider();

    private static Services apiServices;
    private static Executor apiExecutor;

    public static void setMinecraftAPIServices(Services apiSrv, Executor apiExec)
    {
        apiServices = apiSrv;
        apiExecutor = apiExec;
    }

    //region From SkullBlockEntity

    public static CompletableFuture<Optional<GameProfile>> fetchProfileWithTextures(GameProfile profile)
    {
        return profile.getProperties().containsKey("textures")
                ? CompletableFuture.completedFuture(Optional.of(profile))
                : CompletableFuture.supplyAsync(() ->
                    {
                        var sessionService = Minecraft.getInstance().getMinecraftSessionService();

                        if (sessionService != null)
                        {
                            ProfileResult profileResult = sessionService.fetchProfile(profile.getId(), true);
                            return profileResult == null ? Optional.of(profile) : Optional.of(profileResult.profile());
                        } else
                        {
                            return Optional.empty();
                        }
                    }, Util.backgroundExecutor());
    }

    private static GameProfileCache userCache;

    private static CompletableFuture<Optional<GameProfile>> fetchProfile(String name)
    {
        if (userCache == null && apiServices != null)
            userCache = apiServices.profileCache();

        GameProfileCache userCache = MorphLocalPlayer.userCache;
        return userCache == null
                ? CompletableFuture.completedFuture(Optional.empty())
                : userCache.getAsync(name).thenCompose((optional) ->
                {
                    return optional.isPresent() ? fetchProfileWithTextures(optional.get()) : CompletableFuture.completedFuture(Optional.empty());
                }).thenApplyAsync((profile) ->
                {
                    return profile;
                }, apiExecutor);
    }

    //endregion From SkullBlockEntity

    private final AtomicBoolean initialFetchFired = new AtomicBoolean(false);

    public void updateSkin(GameProfile profile)
    {
        updateSkin(profile, false);
    }

    private void updateSkin(GameProfile profile, boolean isInitial)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            FeatherMorphClient.getInstance().schedule(() -> updateSkin(profile));
            return;
        }

        if (isInitial && initialFetchFired.get())
            return;

        initialFetchFired.set(true);

        //logger.info("Fetching skin for " + profile);

        if (!profile.getName().equals(playerName))
        {
            //logger.info("Profile %s player name not match : '%s' <-> '%s'".formatted(profile.getId(), profile.getName(), playerName));
            return;
        }

        requestId++;

        var invokeId = requestId;

        // 根据传入的profile来决定要不要由我们自己获取皮肤
        CompletableFuture<Optional<GameProfile>> profileFetchTask;

        // 如果传入的是NIL_UUID，则自己获取，否则就用传入的profile
        if (profile.getId().equals(Util.NIL_UUID))
            profileFetchTask = fetchProfile(profile.getName());
        else
            profileFetchTask = CompletableFuture.completedFuture(Optional.of(profile));

        profileFetchTask.thenApply(optional ->
        {
            // 确保targetProfile不是null
            GameProfile targetProfile = optional.orElse(profile);

            //logger.info("Target UUID is " + targetProfile.getId() + " :: Optional is " + optional.orElse(null));

            // 开始获取皮肤信息
            startFetchTask(targetProfile, invokeId);
            return null;
        });
    }

    private void startFetchTask(GameProfile profile, int invokeId)
    {
        // 通过fetchProfileWithTextures获取带皮肤的gameProfile
        var texturedProfileFetchTask = fetchProfileWithTextures(profile);
        texturedProfileFetchTask.thenApply((optional ->
        {
            GameProfile gameProfile = optional.orElse(null);

            // 如果没有，则不做任何举动
            if (gameProfile == null)
                return null;

            // 反之，获取其中的皮肤
            var skinProvider = Minecraft.getInstance().getSkinManager();
            var skinFetchTask = skinProvider.getOrLoad(profile);
            skinFetchTask.thenAccept(texturesOptional ->
            {
                if (texturesOptional.isEmpty())
                    return;

                onFetchComplete(invokeId, texturesOptional.get(), profile);
            });

            return null;
        }));
    }

    private void onFetchComplete(int invokeId, PlayerSkin tex, GameProfile profile)
    {
        if (this.isRemoved()) return;

        var currentId = currentProfilePair.getA();

        if (invokeId < currentId)
            return;

        this.capeTextureIdentifier = tex.capeTexture();
        currentProfilePair.setA(requestId);
        currentProfilePair.setB(profile);

        this.skinTextureUrl = tex.textureUrl();

        this.morphTextureIdentifier = tex.texture();
        this.model = tex.model();

        updateSkinTextures(profile);
    }

    @Nullable
    private PlayerInfo playerInfo;

    private void updateSkinTextures(GameProfile profile)
    {
        RenderSystem.assertOnRenderThread();
        this.playerInfo = new PlayerInfo(profile, false);
    }

    @Nullable
    private BlockPos overrideSleepPos;

    @Override
    public Optional<BlockPos> getSleepingPos()
    {
        if (overrideSleepPos != null)
            return Optional.of(overrideSleepPos);

        return super.getSleepingPos();
    }

    public void overrideSleepPos(BlockPos pos)
    {
        this.overrideSleepPos = pos;
    }

    @Override
    public boolean isSpectator()
    {
        return bindingPlayer != null && bindingPlayer.isSpectator();
    }

    @Override
    public HumanoidArm getMainArm()
    {
        return bindingPlayer == null ? super.getMainArm() : bindingPlayer.getMainArm();
    }

    @Override
    public boolean isFallFlying()
    {
        return bindingPlayer == null ? super.isFallFlying() : bindingPlayer.isFallFlying();
    }

    @Override
    public ItemStack getUseItem()
    {
        return bindingPlayer == null ? super.getUseItem() : bindingPlayer.getUseItem();
    }

    @Override
    public boolean isUsingItem()
    {
        return bindingPlayer == null ? super.isUsingItem() : bindingPlayer.isUsingItem();
    }

    @Override
    public int getTicksUsingItem()
    {
        return bindingPlayer == null ? super.getTicksUsingItem() : bindingPlayer.getTicksUsingItem();
    }

    @Override
    public int getUseItemRemainingTicks()
    {
        return bindingPlayer == null ? super.getUseItemRemainingTicks() : bindingPlayer.getUseItemRemainingTicks();
    }

    @Override
    public boolean isAutoSpinAttack()
    {
        return bindingPlayer == null ? super.isAutoSpinAttack() : bindingPlayer.isAutoSpinAttack();
    }

    @Override
    public Vec3 position()
    {
        return bindingPlayer == null ? super.position() : bindingPlayer.position();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
    }

    @Override
    public boolean isModelPartShown(PlayerModelPart modelPart)
    {
        return bindingPlayer == null || bindingPlayer.isModelPartShown(modelPart);
    }

    @Override
    public @Nullable PlayerInfo getPlayerInfo()
    {
        return playerInfo;
    }

    @Override
    public boolean shouldShowName()
    {
        return hasBindingPlayer() && (Minecraft.getInstance().cameraEntity != bindingPlayer || bindingPlayer != Minecraft.getInstance().player);
    }

    @Override
    public double distanceToSqr(Vec3 vector)
    {
        // compat with 3d skin layers
        if (vector.equals(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()))
            return 0d;

        return super.distanceToSqr(vector);
    }
}
