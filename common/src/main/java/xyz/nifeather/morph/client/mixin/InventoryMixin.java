package xyz.nifeather.morph.client.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;

@Mixin(Inventory.class)
public abstract class InventoryMixin
{
    @Shadow
    @Final
    public Player player;

    @Shadow
    public abstract ItemStack getSelectedItem();

    @Shadow
    public abstract ItemStack getItem(int slot);

    @Shadow
    private int selected;

    @Inject(method = "setSelectedSlot", at = @At("RETURN"))
    private void morphclient$onSetSelectedSlot(int slot, CallbackInfo ci)
    {
        var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(player);
        if (syncer != null)
            syncer.updateSelectedItem(getItem(slot));
    }

    @Inject(method = "setItem", at = @At("RETURN"))
    private void morphclient$onSetItem(int slot, ItemStack itemStack, CallbackInfo ci)
    {
        if (slot != selected) return;

        var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(player);
        if (syncer != null)
            syncer.updateSelectedItem(itemStack);
    }
}
