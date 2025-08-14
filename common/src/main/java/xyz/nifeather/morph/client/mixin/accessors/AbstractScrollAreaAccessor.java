package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.client.gui.components.AbstractScrollArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractScrollArea.class)
public interface AbstractScrollAreaAccessor
{
    @Accessor
    void setScrolling(boolean newVal);
}
