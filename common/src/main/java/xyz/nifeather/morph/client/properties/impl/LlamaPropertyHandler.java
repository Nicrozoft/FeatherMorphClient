package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Llama;
import xyz.nifeather.morph.client.mixin.accessors.LlamaAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Arrays;
import java.util.Optional;

public class LlamaPropertyHandler extends EntityPropertyHandler<Llama>
{
    public final ClientProperty<Llama.Variant> COLOR = ClientProperty.of(PropertyNames.LLAMA_COLOR, this::readLlamaVariant);

    private Optional<Llama.Variant> readLlamaVariant(String string)
    {
        return Arrays.stream(Llama.Variant.values())
                .filter(v -> v.name().equalsIgnoreCase(string))
                .findFirst();
    }

    public LlamaPropertyHandler()
    {
        register(COLOR);
    }

    @Override
    public Optional<Llama> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Llama llama ? llama : null);
    }

    @Override
    protected <X> void applyToEntity(Llama entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        if (property.equals(COLOR))
            ((LlamaAccessor)entity).callSetVariant((Llama.Variant) value);
    }
}
