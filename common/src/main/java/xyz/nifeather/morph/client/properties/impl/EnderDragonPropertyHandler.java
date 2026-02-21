package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class EnderDragonPropertyHandler extends EntityPropertyHandler<EnderDragon>
{
    public final ClientProperty<Integer> DRAGON_PHASE = ClientProperty.of(PropertyNames.ENDER_DRAGON_DRAGON_PHASE, CommonInputHandles::intOrEmpty);

    public EnderDragonPropertyHandler()
    {
        register(DRAGON_PHASE);
    }

    @Override
    public Optional<EnderDragon> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof EnderDragon dragon ? dragon : null);
    }

    @Override
    protected <X> void applyToEntity(EnderDragon entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, syncer, property, value);

        if (property.equals(DRAGON_PHASE))
            entity.getPhaseManager().setPhase(EnderDragonPhase.getById((Integer)value));
    }
}
