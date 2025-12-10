package xyz.nifeather.morph.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.graphics.CameraHelper;

@Mixin(Camera.class)
public abstract class CameraMixin
{
    @Shadow private Entity entity;
    @Shadow private float eyeHeight;
    @Shadow private Level level;
    @Shadow private float eyeHeightOld;

    @Unique
    private boolean featherMorph$sodiumExtraInstalled;

    @Unique
    private final CameraHelper featherMorph$cameraHelper = new CameraHelper();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci)
    {
        featherMorph$sodiumExtraInstalled = FeatherMorphClientBootstrap.isModLoaded("sodium-extra") || FeatherMorphClientBootstrap.isModLoaded("sodium_extra");
    }

    @Unique
    private boolean featherMorph$isInstantSneak;

    @Inject(method = "setup", at = @At("HEAD"))
    private void onUpdate(Level level, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
    {
        CameraHelper.isThirdPerson.set(thirdPerson);
    }

    @Redirect(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getEyeHeight()F"))
    private float featherMorph$onEntityEyeHeightCall(Entity instance)
    {
        //Workaround for SodiumExtra's InstantSneak
        //https://github.com/FlashyReese/sodium-extra-fabric/blob/1.19.x/dev/src/main/java/me/flashyreese/mods/sodiumextra/mixin/instant_sneak/MixinCamera.java
        featherMorph$isInstantSneak = featherMorph$sodiumExtraInstalled && this.eyeHeight == instance.getEyeHeight();

        if (featherMorph$isInstantSneak)
            return instance.getEyeHeight();
        else
            return featherMorph$cameraHelper.onEyeHeightCall(instance, level);
    }

    @Inject(method = "tick",at = @At("RETURN"))
    private void featherMorph$endUpdateEyeHeight(CallbackInfo ci)
    {
        if (featherMorph$isInstantSneak)
            this.eyeHeightOld = this.eyeHeight = featherMorph$cameraHelper.onEyeHeightCall(this.entity, level);
    }
}
