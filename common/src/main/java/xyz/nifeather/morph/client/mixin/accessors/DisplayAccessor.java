package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Display;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Display.class)
public interface DisplayAccessor
{
    @Accessor
    EntityDataAccessor<Vector3fc> getDATA_SCALE_ID();

    @Accessor
    EntityDataAccessor<Vector3fc> getDATA_TRANSLATION_ID();
}
