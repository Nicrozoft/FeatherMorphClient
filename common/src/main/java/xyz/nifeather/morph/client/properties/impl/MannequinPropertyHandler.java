package xyz.nifeather.morph.client.properties.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.component.ResolvableProfile;
import xyz.nifeather.morph.client.mixin.accessors.MannequinAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class MannequinPropertyHandler extends EntityPropertyHandler<Mannequin>
{
    public final ClientProperty<Component> DESCRIPTION = ClientProperty.of(PropertyNames.MANNEQUIN_NPC_DESCRIPTION, CommonInputHandles::component);
    public final ClientProperty<ResolvableProfile> SKIN = ClientProperty.of(PropertyNames.MANNEQUIN_SKIN_INTERNAL, CommonInputHandles::resolvableProfile);
    public final ClientProperty<Boolean> HIDE_DESCRIPTION = ClientProperty.of(PropertyNames.MANNEQUIN_HIDE_DESCRIPTION, CommonInputHandles.BOOLEAN);
    public final ClientProperty<Boolean> IMMOVABLE = ClientProperty.of(PropertyNames.MANNEQUIN_IMMOVABLE, CommonInputHandles.BOOLEAN);

    public MannequinPropertyHandler()
    {
        register(DESCRIPTION, HIDE_DESCRIPTION, IMMOVABLE, SKIN);
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
        else if (property.equals(SKIN))
            ((MannequinAccessor)entity).callSetProfile((ResolvableProfile) value);
    }
}
