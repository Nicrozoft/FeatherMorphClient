package xyz.nifeather.morph.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.network.syncher.SynchedEntityData;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;

@Mixin(SynchedEntityData.class)
public class DataTrackerMixin
{
    @Shadow @Final private SynchedEntityData.DataItem<?>[] itemsById;

    @Inject(
            method = "assignValues",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")
    )
    public void onEntries(List<SynchedEntityData.DataValue<?>> newEntries, CallbackInfo ci)
    {
        if (newEntries.stream().anyMatch(entry -> entry.id() >= this.itemsById.length))
        {
            FeatherMorphClientBootstrap.LOGGER.error("Server sent a metadata packet with mismatched entry id!");
            this.morphclient$dumpEntries(newEntries);
        }
    }

    @Unique
    private void morphclient$dumpEntries(List<SynchedEntityData.DataValue<?>> entries)
    {
        FeatherMorphClientBootstrap.LOGGER.info("- x - x - x - Entries - x - x - x -");
        for (SynchedEntityData.DataValue<?> entry : entries)
            FeatherMorphClientBootstrap.LOGGER.info("ID '%s' -> VALUE '%s'".formatted(entry.id(), entry.value()));
        FeatherMorphClientBootstrap.LOGGER.info("- x - x - x - Entries - x - x - x -");
    }
}
