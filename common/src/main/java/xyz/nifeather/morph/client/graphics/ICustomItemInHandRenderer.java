package xyz.nifeather.morph.client.graphics;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICustomItemInHandRenderer
{
    void morphclient$overrideMainHandItem(@Nullable ItemStack itemStack);
    void morphclient$overrideOffHandItem(@Nullable ItemStack itemStack);

    void morphclient$setShouldDisplayOverridingItem(boolean value);
}
