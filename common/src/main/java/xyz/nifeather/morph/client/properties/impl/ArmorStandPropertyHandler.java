package xyz.nifeather.morph.client.properties.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.mixin.accessors.ArmorStandEntityAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.List;
import java.util.Optional;

public class ArmorStandPropertyHandler extends LivingEntityPropertyHandler<ArmorStand>
{
    public final ClientProperty<Boolean> SHOW_ARMS = ClientProperty.of(PropertyNames.ARMOR_STAND_SHOW_ARMS, CommonInputHandles.BOOLEAN);
    public final ClientProperty<Boolean> HAS_BASE_PLATE = ClientProperty.of(PropertyNames.ARMOR_STAND_HAS_BASE_PLATE, CommonInputHandles.BOOLEAN);
    public final ClientProperty<Boolean> SMALL = ClientProperty.of(PropertyNames.ARMOR_STAND_SMALL, CommonInputHandles.BOOLEAN);

    public final ClientProperty<Rotations> HEAD_ROTATION = ClientProperty.of(PropertyNames.ARMOR_STAND_HEAD_ROTATION, this::vector3fFromString);
    public final ClientProperty<Rotations> BODY_ROTATION = ClientProperty.of(PropertyNames.ARMOR_STAND_BODY_ROTATION, this::vector3fFromString);
    public final ClientProperty<Rotations> LEFT_ARM_ROTATION = ClientProperty.of(PropertyNames.ARMOR_STAND_LEFT_ARM_ROTATION, this::vector3fFromString);
    public final ClientProperty<Rotations> RIGHT_ARM_ROTATION = ClientProperty.of(PropertyNames.ARMOR_STAND_RIGHT_ARM_ROTATION, this::vector3fFromString);
    public final ClientProperty<Rotations> LEFT_LEG_ROTATION = ClientProperty.of(PropertyNames.ARMOR_STAND_LEFT_LEG_ROTATION, this::vector3fFromString);
    public final ClientProperty<Rotations> RIGHT_LEG_ROTATION = ClientProperty.of(PropertyNames.ARMOR_STAND_RIGHT_LEG_ROTATION, this::vector3fFromString);

    public ArmorStandPropertyHandler()
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

    @Override
    public Optional<ArmorStand> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof ArmorStand armorStand ? armorStand : null);
    }

    protected <X> void applyToEntity(ArmorStand armorStand, ClientProperty<X> property, X value)
    {
        switch (property.identifier())
        {
            case PropertyNames.ARMOR_STAND_SHOW_ARMS -> armorStand.setShowArms((Boolean)value);
            case PropertyNames.ARMOR_STAND_HAS_BASE_PLATE -> armorStand.setNoBasePlate(!(Boolean)value);
            case PropertyNames.ARMOR_STAND_SMALL -> ((ArmorStandEntityAccessor)armorStand).callSetSmall((Boolean)value);

            case PropertyNames.ARMOR_STAND_HEAD_ROTATION -> armorStand.setHeadPose((Rotations) value);
            case PropertyNames.ARMOR_STAND_BODY_ROTATION -> armorStand.setBodyPose((Rotations) value);
            case PropertyNames.ARMOR_STAND_LEFT_ARM_ROTATION -> armorStand.setLeftArmPose((Rotations) value);
            case PropertyNames.ARMOR_STAND_RIGHT_ARM_ROTATION -> armorStand.setRightArmPose((Rotations) value);
            case PropertyNames.ARMOR_STAND_LEFT_LEG_ROTATION -> armorStand.setLeftLegPose((Rotations) value);
            case PropertyNames.ARMOR_STAND_RIGHT_LEG_ROTATION -> armorStand.setRightLegPose((Rotations) value);
        }
    }
}
