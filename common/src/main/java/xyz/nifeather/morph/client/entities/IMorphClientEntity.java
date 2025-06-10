package xyz.nifeather.morph.client.entities;

import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;

public interface IMorphClientEntity {
    void featherMorph$overridePose(@Nullable Pose newPose);

    void featherMorph$overrideInvisibility(boolean invisible);

    void featherMorph$setNoAcceptSetPose(boolean noAccept);

    void featherMorph$setIsDisguiseEntity(int masterId);

    boolean featherMorph$isDisguiseEntity();

    int featherMorph$getMasterEntityId();

    void featherMorph$requestBypassDispatcherRedirect(Object source, boolean bypass);

    boolean featherMorph$bypassesDispatcherRedirect();
}
