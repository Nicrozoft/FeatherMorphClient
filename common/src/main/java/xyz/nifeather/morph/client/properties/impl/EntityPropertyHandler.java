package xyz.nifeather.morph.client.properties.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import xyz.nifeather.morph.client.DisguiseInstanceTracker;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.entities.IMorphClientEntity;
import xyz.nifeather.morph.client.properties.*;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;

public abstract class EntityPropertyHandler<E extends Entity> extends AbstractPropertyHandler<E>
{
    public final ClientProperty<Component> CUSTOM_NAME = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME, CommonInputHandles::component);
    public final ClientProperty<Boolean> CUSTOM_NAME_VISIBLE = ClientProperty.of(PropertyNames.ENTITY_CUSTOM_NAME_VISIBLE, CommonInputHandles.BOOLEAN);
    public final ClientProperty<DisguiseEquipment> EQUIPMENT = ClientProperty.of(PropertyNames.ENTITY_EQUIPMENT, CommonInputHandles::equipment);
    public final ClientProperty<Boolean> DISPLAY_DISGUISE_EQUIPMENT = ClientProperty.of(PropertyNames.ENTITY_DISPLAY_DISGUISE_EQUIPMENT, CommonInputHandles.BOOLEAN);

    public EntityPropertyHandler()
    {
        register(CUSTOM_NAME, CUSTOM_NAME_VISIBLE, EQUIPMENT, DISPLAY_DISGUISE_EQUIPMENT);
    }

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    protected <X> void applyToEntity(E entity, ClientProperty<X> property, X value)
    {
        if (property.equals(CUSTOM_NAME))
        {
            entity.setCustomName((Component) value);
        }
        else if (property.equals(CUSTOM_NAME_VISIBLE))
        {
            entity.setCustomNameVisible((Boolean) value);
        }
        else if (property.equals(EQUIPMENT))
        {
            if (!(entity instanceof IMorphClientEntity morphClientEntity) || !morphClientEntity.featherMorph$isDisguiseEntity()) return;

            var equipment = (DisguiseEquipment) value;
            var masterId = morphClientEntity.featherMorph$getMasterEntityId();
            var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(masterId);

            if (syncer == null || !syncer.equals(ClientDisguiseSyncer.getCurrentInstance())) return;

            for (EquipmentSlot slot : EquipmentSlot.values())
            {
                if (slot == EquipmentSlot.BODY || slot == EquipmentSlot.SADDLE) continue;

                var item = equipment.getItemOrNull(slot);

                if (item != null)
                    FeatherMorphClientBootstrap.getInstance().morphManager.setEquip(slot, item);
            }
        }
        else if (property.equals(DISPLAY_DISGUISE_EQUIPMENT))
        {
            if (!(entity instanceof IMorphClientEntity morphClientEntity) || !morphClientEntity.featherMorph$isDisguiseEntity()) return;

            var masterId = morphClientEntity.featherMorph$getMasterEntityId();
            var syncer = DisguiseInstanceTracker.getInstance().getSyncerFor(masterId);

            if (syncer == null || !syncer.equals(ClientDisguiseSyncer.getCurrentInstance())) return;

            FeatherMorphClientBootstrap.getInstance().morphManager.equipOverriden.set((Boolean) value);
        }
    }
}
