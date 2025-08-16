package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import xyz.nifeather.morph.client.entities.IMorphLivingEntity;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.utilties.MathUtils;

import java.util.Optional;

public class PlayerPropertyHandler extends LivingEntityPropertyHandler<Player>
{
    private final ClientProperty<HumanoidArm> MAIN_HAND = ClientProperty.of(PropertyNames.PLAYER_MAIN_HAND, this::humanoidArmFromString);
    private final ClientProperty<Integer> STUCKED_ARROWS = ClientProperty.of(PropertyNames.ENTITY_ARROW_COUNT, CommonInputHandles::intOrEmpty);

    private Optional<HumanoidArm> humanoidArmFromString(String input)
    {
        if (input.equals("notset")) return Optional.empty();
        return Optional.of(input.equals("left") ? HumanoidArm.LEFT : HumanoidArm.RIGHT);
    }

    public PlayerPropertyHandler()
    {
        register(MAIN_HAND, STUCKED_ARROWS);
    }

    @Override
    public Optional<Player> tryCast(Entity entity)
    {
        return entity instanceof Player player ? Optional.of(player) : Optional.empty();
    }

    @Override
    protected <X> void applyToEntity(Player entity, ClientProperty<X> property, X value)
    {
        if (!(entity instanceof MorphLocalPlayer morphLocalPlayer)) return;
        if (!(entity instanceof IMorphLivingEntity customLiving)) return;

        switch (property.identifier())
        {
            case PropertyNames.PLAYER_MAIN_HAND -> morphLocalPlayer.setOverrideMainArm(((HumanoidArm)value));
            case PropertyNames.ENTITY_ARROW_COUNT -> customLiving.morphclient$setOverrideArrowCount(MathUtils.clamp(0, 100, (Integer)value));
        }
    }
}
