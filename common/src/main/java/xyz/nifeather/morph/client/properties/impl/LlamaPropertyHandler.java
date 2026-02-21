package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.equine.Llama;
import xyz.nifeather.morph.client.mixin.accessors.LlamaAccessor;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Arrays;
import java.util.Optional;

public class LlamaPropertyHandler extends EntityPropertyHandler<Llama>
{
    public final ClientProperty<Llama.Variant, LlamaAccessor> COLOR =
            ClientProperty.builder(PropertyNames.LLAMA_COLOR, Llama.Variant.DEFAULT, LlamaAccessor.class)
                    .inputHandle(this::readLlamaVariant)
                    .entityHandle(LlamaAccessor::callSetVariant)
                    .build();

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
}
