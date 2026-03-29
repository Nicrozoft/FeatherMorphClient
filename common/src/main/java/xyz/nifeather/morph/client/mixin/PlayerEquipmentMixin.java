package xyz.nifeather.morph.client.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerEquipment;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;

@Mixin(PlayerEquipment.class)
public class PlayerEquipmentMixin
{
    @Shadow
    @Final
    private Player player;

    @Inject(method = "set", at = @At("HEAD"))
    public void dm$onSet(EquipmentSlot slot, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir)
    {
        var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(player);
        if (syncer != null)
            syncer.onMasterEquipmentChange(slot, itemStack);
    }
}
