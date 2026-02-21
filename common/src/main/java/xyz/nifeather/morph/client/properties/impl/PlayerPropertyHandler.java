package xyz.nifeather.morph.client.properties.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import xyz.nifeather.morph.client.entities.IMorphLivingEntity;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;
import xyz.nifeather.morph.client.mixin.accessors.EntityAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;
import xyz.nifeather.morph.client.utilties.MathUtils;

import java.util.Optional;
import java.util.UUID;

public class PlayerPropertyHandler extends EntityPropertyHandler<Player>
{
    private final ClientProperty<HumanoidArm, MorphLocalPlayer> MAIN_HAND =
            ClientProperty.builder(PropertyNames.PLAYER_MAIN_HAND, HumanoidArm.RIGHT, MorphLocalPlayer.class)
                    .inputHandle(this::humanoidArmFromString)
                    .entityHandle(MorphLocalPlayer::setOverrideMainArm)
                    .build();

    private final ClientProperty<Integer, IMorphLivingEntity> STUCKED_ARROWS =
            ClientProperty.builder(PropertyNames.ENTITY_ARROW_COUNT, 0, IMorphLivingEntity.class)
                    .inputHandle(CommonInputHandles::intOrEmpty)
                    .entityHandle(IMorphLivingEntity::morphclient$setOverrideArrowCount)
                    .build();

    private final ClientProperty<GameProfile, MorphLocalPlayer> SKIN =
            ClientProperty.builder(PropertyNames.PLAYER_SKIN, new GameProfile(UUID.randomUUID(), "x"), MorphLocalPlayer.class)
                    .inputHandle(CommonInputHandles::gameProfile)
                    .entityHandle(MorphLocalPlayer::updateSkin)
                    .build();

    private Optional<HumanoidArm> humanoidArmFromString(String input)
    {
        if (input.equals("notset")) return Optional.empty();
        return Optional.of(input.equals("left") ? HumanoidArm.LEFT : HumanoidArm.RIGHT);
    }

    public PlayerPropertyHandler()
    {
        register(MAIN_HAND, STUCKED_ARROWS, SKIN);
    }
}
