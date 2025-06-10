package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nifeather.morph.client.graphics.InventoryRenderHelper;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin
{
    @Unique
    private static final InventoryRenderHelper featherMorphClient$helper = InventoryRenderHelper.getInstance();

    @Redirect(method = "renderBg",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"))
    public void onBackgroundDrawCall(GuiGraphics context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity)
    {
       featherMorphClient$helper.onRenderCall(context, x1, y1, x2, y2, size, f, mouseX, mouseY);
    }
}
