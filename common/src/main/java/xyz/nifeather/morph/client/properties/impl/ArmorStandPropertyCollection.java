package xyz.nifeather.morph.client.properties.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.decoration.ArmorStand;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.mixin.accessors.ArmorStandEntityAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.CommonOutputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.List;
import java.util.Optional;

public class ArmorStandPropertyCollection extends LivingEntityPropertyCollection<ArmorStand>
{
    public final ClientProperty<Boolean, ArmorStand> SHOW_ARMS =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_SHOW_ARMS, false, ArmorStand.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(ArmorStand::setShowArms)
                    .build();

    public final ClientProperty<Boolean, ArmorStand> HAS_BASE_PLATE =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_HAS_BASE_PLATE, true, ArmorStand.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle((e, v) -> e.setNoBasePlate(!v))
                    .build();

    public final ClientProperty<Boolean, ArmorStandEntityAccessor> SMALL =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_SMALL, false, ArmorStandEntityAccessor.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .outputHandle(CommonOutputHandles::writeBoolean)
                    .entityHandle(ArmorStandEntityAccessor::callSetSmall)
                    .build();
    
    private static final Rotations ROT_ZERO = new Rotations(0, 0, 0);

    public final ClientProperty<Rotations, ArmorStand> HEAD_ROTATION =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_HEAD_ROTATION, ROT_ZERO, ArmorStand.class)
                    .inputHandle(this::vector3fFromString)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(ArmorStand::setHeadPose)
                    .build();

    public final ClientProperty<Rotations, ArmorStand> BODY_ROTATION =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_BODY_ROTATION, ROT_ZERO, ArmorStand.class)
                    .inputHandle(this::vector3fFromString)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(ArmorStand::setBodyPose)
                    .build();

    public final ClientProperty<Rotations, ArmorStand> LEFT_ARM_ROTATION =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_LEFT_ARM_ROTATION, ROT_ZERO, ArmorStand.class)
                    .inputHandle(this::vector3fFromString)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(ArmorStand::setLeftArmPose)
                    .build();

    public final ClientProperty<Rotations, ArmorStand> RIGHT_ARM_ROTATION =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_RIGHT_ARM_ROTATION, ROT_ZERO, ArmorStand.class)
                    .inputHandle(this::vector3fFromString)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(ArmorStand::setRightArmPose)
                    .build();

    public final ClientProperty<Rotations, ArmorStand> LEFT_LEG_ROTATION =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_LEFT_LEG_ROTATION, ROT_ZERO, ArmorStand.class)
                    .inputHandle(this::vector3fFromString)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(ArmorStand::setLeftLegPose)
                    .build();

    public final ClientProperty<Rotations, ArmorStand> RIGHT_LEG_ROTATION =
            ClientProperty.builder(PropertyNames.ARMOR_STAND_RIGHT_LEG_ROTATION, ROT_ZERO, ArmorStand.class)
                    .inputHandle(this::vector3fFromString)
                    .outputHandle(CommonOutputHandles::noOp)
                    .entityHandle(ArmorStand::setRightLegPose)
                    .build();

    public ArmorStandPropertyCollection()
    {
        register(SHOW_ARMS, HAS_BASE_PLATE, SMALL);

        register(HEAD_ROTATION, BODY_ROTATION, LEFT_ARM_ROTATION, RIGHT_ARM_ROTATION, LEFT_LEG_ROTATION, RIGHT_LEG_ROTATION);
    }

    private final Gson gson = new GsonBuilder().create();

    private Optional<Rotations> vector3fFromString(String input)
    {
        try
        {
            List<?> gsonList = gson.fromJson(input, List.class);
            var castList = gsonList.stream().map(o -> Float.parseFloat("" + o)).toList();

            if (castList.size() != 3)
                throw new RuntimeException("Malformed float array: Invalid array size");

            if (castList.stream().anyMatch(f -> !Float.isFinite(f)))
                throw new RuntimeException("Malformed float array: contains Non-finite values");

            return Optional.of(new Rotations(castList.get(0), castList.get(1), castList.get(2)));
        }
        catch (Throwable t)
        {
            FeatherMorphClientBootstrap.LOGGER.error("Failed to map string input to Vector3f! Input is '%s'".formatted(input), t);
        }

        return Optional.empty();
    }
}
