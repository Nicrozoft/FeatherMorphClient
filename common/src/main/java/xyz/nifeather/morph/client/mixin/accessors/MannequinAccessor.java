package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mannequin.class)
public interface MannequinAccessor
{
    @Invoker
    void callSetDescription(Component component);

    @Invoker
    void callSetHideDescription(boolean hide);

    @Invoker
    void callSetImmovable(boolean val);

    @Invoker
    void callSetProfile(ResolvableProfile resolvableProfile);
}
