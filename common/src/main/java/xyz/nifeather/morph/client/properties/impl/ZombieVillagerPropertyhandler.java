package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Optional;

public class ZombieVillagerPropertyhandler extends EntityPropertyHandler<ZombieVillager>
{
    public final ClientProperty<Boolean> IS_BABY = ClientProperty.of(PropertyNames.ZOMBIE_VILLAGER_IS_BABY, CommonInputHandles.BOOLEAN);
    public final ClientProperty<Holder<VillagerType>> TYPE = ClientProperty.of(PropertyNames.ZOMBIE_VILLAGER_TYPE, s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_TYPE, s));
    public final ClientProperty<Holder<VillagerProfession>> PROFESSION = ClientProperty.of(PropertyNames.ZOMBIE_VILLAGER_PROFESSION, s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_PROFESSION, s));
    public final ClientProperty<Integer> LEVEL = ClientProperty.of(PropertyNames.ZOMBIE_VILLAGER_LEVEL, CommonInputHandles::intOrEmpty);

    public ZombieVillagerPropertyhandler()
    {
        register(IS_BABY, TYPE, PROFESSION, LEVEL);
    }

    @Override
    public Optional<ZombieVillager> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof ZombieVillager zombie ? zombie : null);
    }

    @Override
    protected <X> void applyToEntity(ZombieVillager entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        VillagerData data = entity.getVillagerData();

        switch (property.identifier())
        {
            case PropertyNames.ZOMBIE_VILLAGER_IS_BABY -> entity.setBaby((Boolean) value);
            case PropertyNames.ZOMBIE_VILLAGER_TYPE -> entity.setVillagerData(new VillagerData((Holder<VillagerType>) value, data.profession(), data.level()));
            case PropertyNames.ZOMBIE_VILLAGER_PROFESSION -> entity.setVillagerData(new VillagerData(data.type(), (Holder<VillagerProfession>) value, data.level()));
            case PropertyNames.ZOMBIE_VILLAGER_LEVEL -> entity.setVillagerData(new VillagerData(data.type(), data.profession(), (Integer) value));
        }
    }
}
