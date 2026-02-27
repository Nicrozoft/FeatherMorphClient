package xyz.nifeather.morph.client.graphics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import xiamomc.pluginbase.Annotations.Initializer;
import xyz.nifeather.morph.client.ClientMorphManager;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.MorphClientObject;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;

public class InventoryRenderHelper extends MorphClientObject
{
    private static InventoryRenderHelper instance;

    public static InventoryRenderHelper getInstance()
    {
        if (instance == null) instance = new InventoryRenderHelper();

        return instance;
    }

    @Initializer
    private void load(ClientMorphManager morphManager)
    {
        morphManager.currentIdentifier.onValueChanged((o, n) ->
        {
            this.allowRender = true;
        });
    }

    public boolean allowRender = true;

    public void onRenderCall(GuiGraphics context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY)
    {
        if (!allowRender) return;
        var modConfig = FeatherMorphClientBootstrap.getInstance().getModConfigData();

        var syncer = ClientDisguiseSyncer.getCurrentInstance();
        var syncerNotAvailable = syncer == null || syncer.disposed();
        var entity = syncerNotAvailable ? null : syncer.getDisguiseInstance();

        PlayerRenderHelper.instance().skipRender = true;

        if (entity instanceof LivingEntity living && (modConfig.clientViewVisible() || modConfig.alwaysShowPreviewInInventory))
        {
            try
            {
                InventoryScreen.renderEntityInInventoryFollowsMouse(context, x1, y1, x2, y2, size, f, mouseX, mouseY, living);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                allowRender = false;
            }
        }
        else
        {
            var clientPlayer = Minecraft.getInstance().player;

            if (clientPlayer != null)
                InventoryScreen.renderEntityInInventoryFollowsMouse(context, x1, y1, x2, y2, size, f, mouseX, mouseY, clientPlayer);
        }

        PlayerRenderHelper.instance().skipRender = false;
    }
}