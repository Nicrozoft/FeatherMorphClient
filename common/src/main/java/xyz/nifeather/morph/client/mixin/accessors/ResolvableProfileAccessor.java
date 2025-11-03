package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ResolvableProfile.class)
public interface ResolvableProfileAccessor
{
    @Mutable
    @Accessor
    public void setSkinPatch(PlayerSkin.Patch patch);
}
