package xyz.nifeather.morph.client.graphics;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.syncers.ClientDisguiseSyncer;
import xiamomc.pluginbase.Bindables.Bindable;

public class CameraHelper
{
    public static Bindable<Boolean> isThirdPerson = new Bindable<>(false);

    @Nullable
    private Entity getCurrentDisguise()
    {
        var syncer = ClientDisguiseSyncer.getCurrentInstance();
        if (syncer == null || syncer.disposed()) return null;

        return syncer.getDisguiseInstance();
    }

    public float onEyeHeightCall(Entity instance, BlockGetter area)
    {
        if (instance == null) return 0f;

        var current = getCurrentDisguise();
        var client = FeatherMorphClientBootstrap.getInstance();

        if (current != null && client.morphManager.selfVisibleEnabled.get() && client.getModConfigData().changeCameraHeight)
        {
            if (current.getEyeHeight() <= instance.getEyeHeight())
            {
                var vehicle = instance.getVehicle();

                if (vehicle != null)
                    return Math.max(current.getEyeHeight(), vehicle.getEyeHeight() + 0.15f);

                return current.getEyeHeight();
            }

            var pos = instance.getEyePosition();
            var targetPos = pos.add(0, current.getEyeHeight() - instance.getEyeHeight(), 0);

            var rayCastContext = new ClipContext(pos, targetPos,
                    ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, instance);

            var rayCast = area.clip(rayCastContext);

            double distance = current.getEyeHeight();
            if (rayCast.getType() == HitResult.Type.BLOCK)
            {
                distance = instance.getEyeHeight();
                //distance = instance.getStandingEyeHeight() + rayCast.getPos().distanceTo(pos) - 0.375f;
            }

            //LoggerFactory.getLogger("dddd").info("type? " + (rayCast.getType()) + " :: distance? " + distance);

            return (float) distance;
        }

        return instance.getEyeHeight();
    }

    public void onCameraUpdate(Camera camera)
    {
    }
}
