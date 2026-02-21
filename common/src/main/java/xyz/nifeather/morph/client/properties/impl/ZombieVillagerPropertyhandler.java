package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class ZombieVillagerPropertyhandler extends EntityPropertyHandler<ZombieVillager>
{
    public final ClientProperty<Boolean, Zombie> IS_BABY =
            ClientProperty.<Boolean, Zombie>builder(PropertyNames.ZOMBIE_VILLAGER_IS_BABY, Zombie.class)
                    .inputHandle(CommonInputHandles::readBoolean)
                    .entityHandle(Zombie::setBaby)
                    .build();

    public final ClientProperty<Holder<VillagerType>, ZombieVillager> TYPE =
            ClientProperty.<Holder<VillagerType>, ZombieVillager>builder(PropertyNames.ZOMBIE_VILLAGER_TYPE, ZombieVillager.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_TYPE, s))
                    .entityHandle((villager, type) ->
                    {
                        VillagerData data = villager.getVillagerData();
                        villager.setVillagerData(new VillagerData(type, data.profession(), data.level()));
                    })
                    .build();

    public final ClientProperty<Holder<VillagerProfession>, ZombieVillager> PROFESSION =
            ClientProperty.<Holder<VillagerProfession>, ZombieVillager>builder(PropertyNames.ZOMBIE_VILLAGER_PROFESSION, ZombieVillager.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_PROFESSION, s))
                    .entityHandle((villager, professionHolder) ->
                    {
                        VillagerData data = villager.getVillagerData();
                        villager.setVillagerData(new VillagerData(data.type(), professionHolder, data.level()));
                    })
                    .build();

    public final ClientProperty<Integer, ZombieVillager> LEVEL =
            ClientProperty.<Integer, ZombieVillager>builder(PropertyNames.ZOMBIE_VILLAGER_LEVEL, ZombieVillager.class)
                    .inputHandle(CommonInputHandles::intOrEmpty)
                    .entityHandle((villager, level) ->
                    {
                        VillagerData data = villager.getVillagerData();
                        villager.setVillagerData(new VillagerData(data.type(), data.profession(), level));
                    })
                    .build();


    public ZombieVillagerPropertyhandler()
    {
        register(IS_BABY, TYPE, PROFESSION, LEVEL);
    }
}
