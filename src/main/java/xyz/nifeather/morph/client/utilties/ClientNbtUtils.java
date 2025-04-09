package xyz.nifeather.morph.client.utilties;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

public class ClientNbtUtils
{
    /**
     * 获取目标实体的 {@link net.minecraft.nbt.CompoundTag}
     * @param entity 目标实体
     * @return 此实体的NBT数据，当实体为null或不为 {@link Entity} 的实例时返回null
     */
    public static CompoundTag getRawTagCompound(Entity entity)
    {
        var dataSource = new EntityDataAccessor(entity);
        return dataSource.getData();
    }
}
