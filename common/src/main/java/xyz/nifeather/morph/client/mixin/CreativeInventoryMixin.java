package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.nifeather.morph.client.graphics.InventoryRenderHelper;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeInventoryMixin
{
    @Unique
    private static final InventoryRenderHelper morphclient$helper = InventoryRenderHelper.getInstance();

    @Redirect(method = "extractBackground",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;extractEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"))
    public void onBackgroundDrawCall(GuiGraphicsExtractor context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity)
    {
        morphclient$helper.onRenderCall(context, x1, y1, x2, y2, size, f, mouseX, mouseY);
    }
}
