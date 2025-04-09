package xyz.nifeather.morph.client.entities;

import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.Nullable;

public interface IMorphClientEntity
{
    public void featherMorph$overridePose(@Nullable Pose newPose);
    public void featherMorph$overrideInvisibility(boolean invisible);
    public void featherMorph$setNoAcceptSetPose(boolean noAccept);

    public void featherMorph$setIsDisguiseEntity(int masterId);
    public boolean featherMorph$isDisguiseEntity();
    public int featherMorph$getMasterEntityId();

    public void featherMorph$requestBypassDispatcherRedirect(Object source, boolean bypass);
    public boolean featherMorph$bypassesDispatcherRedirect();
}
