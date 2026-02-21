package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Guardian;
import xyz.nifeather.morph.client.entities.IHasOverrideGlowing;
import xyz.nifeather.morph.client.mixin.accessors.GuardianAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class GuardianPropertyHandler extends EntityPropertyHandler<Guardian>
{
    public final ClientProperty<Integer> ATTACK_TARGET = ClientProperty.of(PropertyNames.GUARDIAN_ATTACK_TARGET, CommonInputHandles::intOrEmpty);

    public GuardianPropertyHandler()
    {
        register(ATTACK_TARGET);
    }

    @Override
    public Optional<Guardian> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Guardian guardian ? guardian : null);
    }

    @Override
    protected <X> void applyToEntity(Guardian entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, syncer, property, value);

        switch (property.identifier())
        {
            case PropertyNames.GUARDIAN_ATTACK_TARGET ->
            {
                // todo: Not a very good implement but it works
                var lastEntity = entity.getActiveAttackTarget();
                if (lastEntity != null)
                    ((IHasOverrideGlowing)lastEntity).morphclient$overrideClientGlowing(false);

                var targetEntity = entity.level().getEntity((int)value);
                if (targetEntity != null)
                    ((IHasOverrideGlowing)targetEntity).morphclient$overrideClientGlowing(true);

                ((GuardianAccessor)entity).callSetActiveAttackTarget((int)value);
            }
        }
    }
}
