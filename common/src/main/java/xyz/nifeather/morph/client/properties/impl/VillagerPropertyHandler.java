package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class VillagerPropertyHandler extends EntityPropertyHandler<Villager>
{
    public final ClientProperty<Holder<VillagerType>> TYPE = ClientProperty.of(PropertyNames.VILLAGER_TYPE, s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_TYPE, s));
    public final ClientProperty<Holder<VillagerProfession>> PROFESSION = ClientProperty.of(PropertyNames.VILLAGER_PROFESSION, s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_PROFESSION, s));
    public final ClientProperty<Integer> LEVEL = ClientProperty.of(PropertyNames.VILLAGER_LEVEL, CommonInputHandles::intOrEmpty);

    public VillagerPropertyHandler()
    {
        register(TYPE, PROFESSION, LEVEL);
    }

    @Override
    public Optional<Villager> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Villager villager ? villager : null);
    }

    @Override
    protected <X> void applyToEntity(Villager entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        VillagerData data = entity.getVillagerData();

        switch (property.identifier())
        {
            case PropertyNames.VILLAGER_TYPE -> entity.setVillagerData(new VillagerData((Holder<VillagerType>) value, data.profession(), data.level()));
            case PropertyNames.VILLAGER_PROFESSION -> entity.setVillagerData(new VillagerData(data.type(), (Holder<VillagerProfession>) value, data.level()));
            case PropertyNames.VILLAGER_LEVEL -> entity.setVillagerData(new VillagerData(data.type(), data.profession(), (Integer)value));
        }
    }
}
