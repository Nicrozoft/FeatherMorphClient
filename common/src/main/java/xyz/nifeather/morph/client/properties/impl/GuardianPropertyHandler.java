package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.monster.Guardian;
import xyz.nifeather.morph.client.entities.IHasOverrideGlowing;
import xyz.nifeather.morph.client.mixin.accessors.GuardianAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public class GuardianPropertyHandler extends EntityPropertyHandler<Guardian>
{
    public final ClientProperty<Integer, Guardian> ATTACK_TARGET =
            ClientProperty.builder(PropertyNames.GUARDIAN_ATTACK_TARGET, 0, Guardian.class)
                    .inputHandle(CommonInputHandles::intOrEmpty)
                    .entityHandle(this::applyAttackTarget)
                    .build();

    private void applyAttackTarget(Guardian guardian, int value)
    {
        // todo: Not a very good implement but it works
        var lastEntity = guardian.getActiveAttackTarget();
        if (lastEntity != null)
            ((IHasOverrideGlowing)lastEntity).morphclient$overrideClientGlowing(false);

        var targetEntity = guardian.level().getEntity(value);
        if (targetEntity != null)
            ((IHasOverrideGlowing)targetEntity).morphclient$overrideClientGlowing(true);

        ((GuardianAccessor)guardian).callSetActiveAttackTarget(value);
    }

    public GuardianPropertyHandler()
    {
        register(ATTACK_TARGET);
    }
}
