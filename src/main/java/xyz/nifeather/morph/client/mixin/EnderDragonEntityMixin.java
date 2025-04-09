package xyz.nifeather.morph.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;

@Mixin(EnderDragon.class)
public class EnderDragonEntityMixin
{
    @Unique
    private static final Random random = new Random();

    @Unique
    private EnderDragon morphClient$entityInstance;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void morphClient$onInit(EntityType<?> entityType, Level world, CallbackInfo ci)
    {
        this.morphClient$entityInstance = (EnderDragon) (Object) this;
    }

    @Inject(method = "onFlap", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private void morphClient$onFlapWings(CallbackInfo ci)
    {
        if (((IMorphClientEntity)this).featherMorph$isDisguiseEntity())
            morphClient$playSoundAtPlayer();
    }

    @Unique
    private void morphClient$playSoundAtPlayer()
    {
        var fmClient = FeatherMorphClient.getInstance();
        var allowClientView = fmClient.getModConfigData().allowClientView;
        if (!allowClientView && fmClient.morphManager.selfVisibleEnabled.get()) return;

        var playerLoc = Minecraft.getInstance().player.position();
        morphClient$entityInstance.level().playLocalSound(playerLoc.x, playerLoc.y, playerLoc.z,
                SoundEvents.ENDER_DRAGON_FLAP, morphClient$entityInstance.getSoundSource(),
                5.0F, 0.8F + random.nextFloat() * 0.3F, false);
    }
}
