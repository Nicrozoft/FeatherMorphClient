package xyz.nifeather.morph.client.properties.impl;

import com.google.gson.GsonBuilder;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.properties.AbstractPropertyHandler;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

public abstract class EntityPropertyHandler<E extends Entity> extends AbstractPropertyHandler<E>
{
    // Custom name is unsupported, since we use Adventure components on the plugin side...
    public final ClientProperty<String> CUSTOM_NAME = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME, CommonInputHandles::string);
    public final ClientProperty<Boolean> CUSTOM_NAME_VISIBLE = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME_VISIBLE, CommonInputHandles.BOOLEAN);

    public EntityPropertyHandler()
    {
        register(CUSTOM_NAME, CUSTOM_NAME_VISIBLE);
    }

    @Override
    protected <X> void applyToEntity(E entity, ClientProperty<X> property, X value)
    {
        FeatherMorphClientBootstrap.LOGGER.error("LIVING APPLT TO ENTITY");
        if (property.equals(CUSTOM_NAME))
        {
            FeatherMorphClientBootstrap.LOGGER.error("CUSTOM NAME!");
            try
            {
                var compound = TagParser.parseCompoundFully(value.toString());

                var dynamic = entity.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

                var component = ComponentSerialization.CODEC.decode(dynamic, compound).getOrThrow().getFirst();
                entity.setCustomName(component);

                FeatherMorphClientBootstrap.LOGGER.error("Component is " + component);
            }
            catch (Throwable t)
            {
                FeatherMorphClientBootstrap.LOGGER.error("Failed to deserialize component", t);
                entity.setCustomName(Component.literal("<Component serialization failed>"));
            }
        }
        else if (property.equals(CUSTOM_NAME_VISIBLE))
        {
            entity.setCustomNameVisible((Boolean) value);
        }
    }
}
