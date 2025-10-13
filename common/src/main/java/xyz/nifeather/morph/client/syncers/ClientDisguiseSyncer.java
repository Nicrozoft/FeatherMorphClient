package xyz.nifeather.morph.client.syncers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import xyz.nifeather.morph.client.ClientMorphManager;
import xyz.nifeather.morph.client.EntityCache;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.network.ServerHandler;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.morph.client.properties.ClientProperty;

import java.util.Map;

public class ClientDisguiseSyncer extends DisguiseSyncer
{
    private static ClientDisguiseSyncer currentInstance;

    @Nullable
    public static ClientDisguiseSyncer getCurrentInstance()
    {
        return currentInstance;
    }

    public ClientDisguiseSyncer(AbstractClientPlayer clientPlayer, String morphId, int networkId)
    {
        super(clientPlayer, morphId, networkId);

        currentInstance = this;
    }

    @Resolved(shouldSolveImmediately = true)
    private ClientMorphManager morphManager;

    private final Bindable<CompoundTag> currentNbtCompound = new Bindable<>(null);

    @Initializer
    private void load(ServerHandler serverHandler)
    {
        currentNbtCompound.bindTo(morphManager.currentNbtCompound);

        currentNbtCompound.onValueChanged((o, n) ->
        {
            if (n != null) FeatherMorphClientBootstrap.getInstance().schedule(() -> this.mergeNbt(n));
        }, true);
    }

    @Override
    protected @NotNull EntityCache getEntityCache()
    {
        return EntityCache.getGlobalCache();
    }

    @Override
    public boolean setupEntity()
    {
        if (!super.setupEntity())
        {
            acceptSyncing = false;
            return false;
        }

        acceptSyncing = true;
        beamTarget = null;

        var clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null)
            clientPlayer.refreshDimensions();

        return true;
    }

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

    public static boolean syncing;

    private boolean acceptSyncing;

    @Override
    public void syncTick()
    {
        baseSync();
        syncYawPitch();
    }

    @Override
    protected @Nullable CompoundTag getCompound()
    {
        return morphManager.currentNbtCompound.get();
    }

    @Override
    protected void syncEquipments()
    {
        if (disguiseInstance == null) return;

        //同步装备
        if (!morphManager.equipOverriden.get())
        {
            disguiseInstance.setItemSlot(EquipmentSlot.MAINHAND, bindingPlayer.getItemBySlot(EquipmentSlot.MAINHAND));
            disguiseInstance.setItemSlot(EquipmentSlot.OFFHAND, bindingPlayer.getItemBySlot(EquipmentSlot.OFFHAND));

            disguiseInstance.setItemSlot(EquipmentSlot.HEAD, bindingPlayer.getItemBySlot(EquipmentSlot.HEAD));
            disguiseInstance.setItemSlot(EquipmentSlot.CHEST, bindingPlayer.getItemBySlot(EquipmentSlot.CHEST));
            disguiseInstance.setItemSlot(EquipmentSlot.LEGS, bindingPlayer.getItemBySlot(EquipmentSlot.LEGS));
            disguiseInstance.setItemSlot(EquipmentSlot.FEET, bindingPlayer.getItemBySlot(EquipmentSlot.FEET));
        }
        else
        {
            var manager = FeatherMorphClientBootstrap.getInstance().morphManager;

            disguiseInstance.setItemSlot(EquipmentSlot.MAINHAND, manager.getOverridedItemStackOn(EquipmentSlot.MAINHAND));
            disguiseInstance.setItemSlot(EquipmentSlot.OFFHAND, manager.getOverridedItemStackOn(EquipmentSlot.OFFHAND));

            disguiseInstance.setItemSlot(EquipmentSlot.HEAD, manager.getOverridedItemStackOn(EquipmentSlot.HEAD));
            disguiseInstance.setItemSlot(EquipmentSlot.CHEST, manager.getOverridedItemStackOn(EquipmentSlot.CHEST));
            disguiseInstance.setItemSlot(EquipmentSlot.LEGS, manager.getOverridedItemStackOn(EquipmentSlot.LEGS));
            disguiseInstance.setItemSlot(EquipmentSlot.FEET, manager.getOverridedItemStackOn(EquipmentSlot.FEET));
        }
    }

    @Override
    protected boolean showOverridedEquips()
    {
        return morphManager.equipOverriden.get();
    }

    @Override
    protected void onDispose()
    {
        currentNbtCompound.unBindFromTarget();
        currentNbtCompound.unBindBindings();
    }
}
