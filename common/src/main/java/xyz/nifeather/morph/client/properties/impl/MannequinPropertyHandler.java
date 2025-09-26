package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Mannequin;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.mixin.accessors.MannequinAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class MannequinPropertyHandler extends EntityPropertyHandler<Mannequin>
{
    public final ClientProperty<Component> DESCRIPTION = ClientProperty.of(PropertyNames.MANNEQUIN_NPC_DESCRIPTION, CommonInputHandles::component);
    public final ClientProperty<Boolean> HIDE_DESCRIPTION = ClientProperty.of(PropertyNames.MANNEQUIN_HIDE_DESCRIPTION, CommonInputHandles.BOOLEAN);
    public final ClientProperty<Boolean> IMMOVABLE = ClientProperty.of(PropertyNames.MANNEQUIN_IMMOVABLE, CommonInputHandles.BOOLEAN);

    public MannequinPropertyHandler()
    {
        register(DESCRIPTION, HIDE_DESCRIPTION, IMMOVABLE);
    }

    @Override
    public Optional<Mannequin> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Mannequin mannequin ? mannequin : null);
    }

    @Override
    protected <X> void applyToEntity(Mannequin entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(DESCRIPTION))
            ((MannequinAccessor) entity).callSetDescription((Component) value);
        else if (property.equals(HIDE_DESCRIPTION))
            ((MannequinAccessor)entity).callSetHideDescription((Boolean) value);
        else if (property.equals(IMMOVABLE))
            ((MannequinAccessor)entity).callSetImmovable((Boolean) value);

        if (entity instanceof ClientAvatarEntity avatar)
        FeatherMorphClientBootstrap.LOGGER.info("Undername is "+  avatar.belowNameDisplay() + " + erntity " + entity);
    }
}
