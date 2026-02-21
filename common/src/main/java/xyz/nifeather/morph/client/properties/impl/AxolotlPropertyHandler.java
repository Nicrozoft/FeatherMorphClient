package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import xyz.nifeather.morph.client.mixin.accessors.AxolotlAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Arrays;
import java.util.Optional;

public class AxolotlPropertyHandler extends EntityPropertyHandler<Axolotl>
{
    public final ClientProperty<Axolotl.Variant> VARIANT = ClientProperty.of(PropertyNames.AXOLOTL_VARIANT, this::readVariant);

    public AxolotlPropertyHandler()
    {
        register(VARIANT);
    }

    private Optional<Axolotl.Variant> readVariant(String string)
    {
        return Arrays.stream(Axolotl.Variant.values())
                .filter(v -> v.getName().equalsIgnoreCase(string))
                .findFirst();
    }

    @Override
    public Optional<Axolotl> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Axolotl axolotl ? axolotl : null);
    }

    @Override
    protected <X> void applyToEntity(Axolotl entity, DisguiseSyncer syncer, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, syncer, property, value);

        if (property.equals(VARIANT))
            ((AxolotlAccessor)entity).callSetVariant((Axolotl.Variant) value);
    }
}
