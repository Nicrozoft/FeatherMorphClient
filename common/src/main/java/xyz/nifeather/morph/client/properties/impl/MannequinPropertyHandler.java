package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.item.component.ResolvableProfile;
import xyz.nifeather.morph.client.mixin.accessors.MannequinAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;
import java.util.UUID;

public class MannequinPropertyHandler extends EntityPropertyHandler<Mannequin>
{
    public final ClientProperty<Component, MannequinAccessor> DESCRIPTION =
            ClientProperty.builder(PropertyNames.MANNEQUIN_NPC_DESCRIPTION, Component.empty(), Component.class, MannequinAccessor.class)
                    .inputHandle(CommonInputHandles::component)
                    .entityHandle(MannequinAccessor::callSetDescription)
                    .build();

    public final ClientProperty<ResolvableProfile, MannequinAccessor> SKIN = 
            ClientProperty.builder(PropertyNames.MANNEQUIN_SKIN, ResolvableProfile.createUnresolved(UUID.randomUUID()), MannequinAccessor.class)
                    .inputHandle(CommonInputHandles::resolvableProfile)
                    .entityHandle(MannequinAccessor::callSetProfile)
                    .build();

    public final ClientProperty<Boolean, MannequinAccessor> HIDE_DESCRIPTION =
            ClientProperty.builder(PropertyNames.MANNEQUIN_HIDE_DESCRIPTION, false, MannequinAccessor.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(MannequinAccessor::callSetHideDescription)
                    .build();

    public final ClientProperty<Boolean, MannequinAccessor> IMMOVABLE =
            ClientProperty.builder(PropertyNames.MANNEQUIN_IMMOVABLE, false, MannequinAccessor.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(MannequinAccessor::callSetImmovable)
                    .build();

    public MannequinPropertyHandler()
    {
        register(DESCRIPTION, HIDE_DESCRIPTION, IMMOVABLE, SKIN);
    }
}
