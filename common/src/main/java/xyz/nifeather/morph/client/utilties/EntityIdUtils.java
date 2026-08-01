package xyz.nifeather.morph.client.utilties;

import net.minecraft.world.entity.Entity;
import xyz.nifeather.morph.client.mixin.accessors.EntityAccessor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 26.2 起 {@link Entity#getId()} 在ID尚未分配（值为0）时会抛出 IllegalStateException，
 * 而不再像以前那样直接返回0。
 * <p>
 * 切换维度时，客户端会在重生流程中先构造出新的玩家实体、随后才为其分配网络ID，
 * 期间 {@code Inventory#setSelectedSlot} 等方法就已经会被调用到，
 * 因此所有可能在实体初始化完成前触发的调用点都应该先经过这里判断。
 */
public class EntityIdUtils
{
    /**
     * 表示实体尚未被分配网络ID
     */
    public static final int NO_ID = 0;

    /**
     * @return 实体是否已被分配网络ID
     */
    public static boolean hasNetworkId(Entity entity)
    {
        return entity != null && ((EntityAccessor) entity).getRawNetworkId() != NO_ID;
    }

    /**
     * 安全地获取实体的网络ID，如果尚未分配则返回 {@link #NO_ID}
     */
    public static int networkIdOf(Entity entity)
    {
        return entity == null ? NO_ID : ((EntityAccessor) entity).getRawNetworkId();
    }

    private static final AtomicInteger clientOnlyIdCounter = new AtomicInteger(0);

    /**
     * 为纯客户端实体分配一个网络ID。
     * <p>
     * 26.1 及之前，{@code Entity} 的构造函数会用一个全局计数器给所有实体分配ID，
     * 所以我们自己创建的伪装实体天然就有ID；26.2 改为使用 {@code Level#getNextEntityId()}，
     * 而该方法只有 ServerLevel 才会真正分配，客户端恒返回0。
     * <p>
     * ID为0的实体在 26.2 下无法使用：{@link Entity#getId()}、{@link Entity#equals(Object)}
     * 以及 {@code ClientLevel#addEntity} 都会因此抛出 IllegalStateException。
     * <p>
     * 这里返回递减的负数：服务端分配的ID总是正数，因此不会与之冲突。
     */
    public static int nextClientOnlyId()
    {
        return clientOnlyIdCounter.decrementAndGet();
    }
}
