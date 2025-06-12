package xyz.nifeather.morph.client.mixin.accessors;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyMapping.class)
public interface KeyBindingAccessor {
    @Accessor("CATEGORY_SORT_ORDER")
    static Map<String, Integer> fabric_getCategoryMap() {
        throw new AssertionError();
    }

    @Accessor("key")
    InputConstants.Key fabric_getBoundKey();
}