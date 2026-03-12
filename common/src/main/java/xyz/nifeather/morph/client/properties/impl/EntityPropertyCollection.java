package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import xyz.nifeather.morph.client.properties.*;

import java.util.Optional;

public class EntityPropertyCollection<E extends Entity> extends AbstractPropertyCollection
{
    public final ClientProperty<Float, Entity> STATIC_YAW =
            ClientProperty.builder(PropertyNames.ENTITY_STATIC_YAW, 0f, Entity.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .build();

    public final ClientProperty<Float, Entity> STATIC_PITCH =
            ClientProperty.builder(PropertyNames.ENTITY_STATIC_PITCH, 0f, Entity.class)
                    .inputHandle(CommonInputHandles::readFloat)
                    .outputHandle(CommonOutputHandles::noOp)
                    .build();

    public final ClientProperty<Pose, Entity> STATIC_POSE =
            ClientProperty.builder(PropertyNames.ENTITY_STATIC_POSE, Pose.STANDING, Entity.class)
                    .inputHandle(this::readPose)
                    .outputHandle(CommonOutputHandles::writeEnum)
                    .entityHandle(Entity::setPose)
                    .build();

    private Optional<Pose> readPose(String input)
    {
        var intOptional = CommonInputHandles.readInteger(input);
        if (intOptional.isEmpty())
            return Optional.empty();

        return Optional.of(Pose.BY_ID.apply(intOptional.get()));
    }

    public EntityPropertyCollection()
    {
        register(STATIC_YAW, STATIC_PITCH, STATIC_POSE);
    }
}
