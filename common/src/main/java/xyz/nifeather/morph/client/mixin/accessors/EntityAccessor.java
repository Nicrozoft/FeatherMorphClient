package xyz.nifeather.morph.client.mixin.accessors;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor
    void setWasTouchingWater(boolean newVal);

    @Accessor
    SynchedEntityData getEntityData();

    /**
     * 直接读取网络ID字段。
     * <p>
     * 26.2 起 {@link Entity#getId()} 会在ID尚未分配时抛出 IllegalStateException，
     * 而我们有不少调用点会在实体完成初始化前被触发（比如切换维度时的重生流程）。
     */
    @Accessor("id")
    int getRawNetworkId();

    @Invoker
    void callSetSharedFlag(int flag, boolean value);
}
