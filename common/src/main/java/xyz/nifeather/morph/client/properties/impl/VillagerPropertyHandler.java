package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.CommonInputHandles;
import xyz.nifeather.morph.client.properties.PropertyNames;
import xyz.nifeather.morph.client.syncers.DisguiseSyncer;

import java.util.Optional;

public class VillagerPropertyHandler extends EntityPropertyHandler<Villager>
{
    public final ClientProperty<Holder<VillagerType>, Villager> TYPE =
            ClientProperty.<Holder<VillagerType>, Villager>builder(PropertyNames.VILLAGER_TYPE, Villager.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_TYPE, s))
                    .entityHandle((villager, type) ->
                    {
                        VillagerData data = villager.getVillagerData();
                        villager.setVillagerData(new VillagerData(type, data.profession(), data.level()));
                    })
                    .build();

    public final ClientProperty<Holder<VillagerProfession>, Villager> PROFESSION =
            ClientProperty.<Holder<VillagerProfession>, Villager>builder(PropertyNames.VILLAGER_PROFESSION, Villager.class)
                    .inputHandle(s -> CommonInputHandles.readVariantHolder(Registries.VILLAGER_PROFESSION, s))
                    .entityHandle((villager, professionHolder) ->
                    {
                        VillagerData data = villager.getVillagerData();
                        villager.setVillagerData(new VillagerData(data.type(), professionHolder, data.level()));
                    })
                    .build();

    public final ClientProperty<Integer, Villager> LEVEL =
            ClientProperty.<Integer, Villager>builder(PropertyNames.VILLAGER_LEVEL, Villager.class)
                    .inputHandle(CommonInputHandles::intOrEmpty)
                    .entityHandle((villager, level) ->
                    {
                        VillagerData data = villager.getVillagerData();
                        villager.setVillagerData(new VillagerData(data.type(), data.profession(), level));
                    })
                    .build();

    public VillagerPropertyHandler()
    {
        register(TYPE, PROFESSION, LEVEL);
    }
}
