package xyz.nifeather.morph.client.syncers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.ClientMorphManager;
import xyz.nifeather.morph.client.EntityCache;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.network.ServerHandler;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;

public class ClientDisguiseSyncer extends DisguiseSyncer
{
    private static ClientDisguiseSyncer currentInstance;

    @Nullable
    public static ClientDisguiseSyncer getCurrentInstance()
    {
        return currentInstance;
    }

    public ClientDisguiseSyncer(AbstractClientPlayer clientPlayer,
                                String morphId,
                                int networkId,
                                @NotNull LivingEntity disguiseEntity)
    {
        super(clientPlayer, morphId, networkId, disguiseEntity);

        currentInstance = this;
    }

    @Resolved(shouldSolveImmediately = true)
    private ClientMorphManager morphManager;

    @Override
    protected void markSyncing()
    {
        syncing = true;
    }

    @Override
    protected void markNotSyncing()
    {
        syncing = false;
    }

    @Override
    public boolean isSyncing()
    {
        return syncing;
    }

    @Override
    protected void onTickError()
    {
        var clientPlayer = Minecraft.getInstance().player;
        assert clientPlayer != null;

        acceptSyncing = false;

        FeatherMorphClientBootstrap.getInstance().updateClientView(true, false);

        clientPlayer.displayClientMessage(Component.translatable("text.morphclient.error.update_disguise1").withStyle(ChatFormatting.RED), false);
        clientPlayer.displayClientMessage(Component.translatable("text.morphclient.error.update_disguise2").withStyle(ChatFormatting.RED), false);
    }

    @Nullable
    public Entity getBeamTarget()
    {
        return beamTarget;
    }

    //private boolean isSpider = false;

    @Override
    protected void initialSync()
    {
        syncPosition();
        syncYawPitch();
    }

    @Override
    protected void onDispose()
    {
    }

    public static boolean syncing;

    private boolean acceptSyncing;

    @Override
    public void syncTick()
    {
        baseSync();
        syncYawPitch();
    }
}
