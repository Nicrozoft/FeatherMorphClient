package xyz.nifeather.morph.client.syncers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.graphics.ICustomItemInHandRenderer;
import xyz.nifeather.morph.client.properties.ClientDisguiseProperties;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.DisguiseEquipment;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.properties.impl.LivingEntityPropertyCollection;

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
                                @NotNull Entity disguiseEntity)
    {
        super(clientPlayer, morphId, networkId, disguiseEntity);

        currentInstance = this;

        propertyHolder.hookOnPropertyWrite((p, o, n) -> localSyncerOnPropertyWrite(p, n));
        propertyHolder.hookOnTemporaryPropertyWrite(this::localSyncerOnPropertyWrite);

        propertyHolder.hookOnPropertyDiscard(this::localSyncerOnPropertyDiscard);
        propertyHolder.hookOnTemporaryPropertyDiscard(this::localSyncerOnPropertyDiscard);
    }

    private void localSyncerOnPropertyDiscard(ClientProperty<Object, ?> property)
    {
        if (!property.identifier().equals(PropertyNames.ENTITY_EQUIPMENT)
                && !property.identifier().equals(PropertyNames.ENTITY_DISPLAY_DISGUISE_EQUIPMENT))
        {
            return;
        }

        var renderer = Minecraft.getInstance().getEntityRenderDispatcher()
                .getItemInHandRenderer();

        if (!(renderer instanceof ICustomItemInHandRenderer customItemInHandRenderer))
            return;

        customItemInHandRenderer.morphclient$overrideMainHandItem(null);
        customItemInHandRenderer.morphclient$overrideOffHandItem(null);
    }

    private void localSyncerOnPropertyWrite(ClientProperty<Object, ?> property, Object newVal)
    {
        var p = ClientDisguiseProperties.INSTANCE.getHandler(disguiseInstance).orElseThrow();
        if (!(p instanceof LivingEntityPropertyCollection<?> properties))
            return;

        if (property.identifier().equals(PropertyNames.ENTITY_EQUIPMENT))
        {
            if (propertyHolder.get(properties.DISPLAY_DISGUISE_EQUIPMENT))
                updateDisplayingItem((DisguiseEquipment) newVal);
        }
        else if (property.identifier().equals(PropertyNames.ENTITY_DISPLAY_DISGUISE_EQUIPMENT))
        {
            var display = (Boolean) newVal;

            if (display)
                updateDisplayingItem(propertyHolder.get(properties.EQUIPMENT));
            else
                updateDisplayingItem(null);
        }
    }

    private void updateDisplayingItem(@Nullable DisguiseEquipment equipment)
    {
        var renderer = Minecraft.getInstance().getEntityRenderDispatcher()
                .getItemInHandRenderer();

        if (!(renderer instanceof ICustomItemInHandRenderer customItemInHandRenderer))
            return;

        customItemInHandRenderer.morphclient$overrideMainHandItem(equipment == null ? null : equipment.getItemInMainHand());
        customItemInHandRenderer.morphclient$overrideOffHandItem(equipment == null ? null : equipment.getItemInOffHand());
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

        clientPlayer.sendSystemMessage(Component.translatable("text.morphclient.error.update_disguise1").withStyle(ChatFormatting.RED));
        clientPlayer.sendOverlayMessage(Component.translatable("text.morphclient.error.update_disguise2").withStyle(ChatFormatting.RED));
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
        var playerPos = bindingPlayer.position();
        var targetPos = new Vec3(playerPos.x, playerPos.y + 1, playerPos.z);
        disguiseInstance.setPos(targetPos);

        syncPosition();
        syncRotation();
    }

    @Override
    protected void onDispose()
    {
        updateDisplayingItem(null);
    }

    public static boolean syncing;

    private boolean acceptSyncing;

    @Override
    public void syncTick()
    {
        baseSync();
        syncRotation();
    }
}
