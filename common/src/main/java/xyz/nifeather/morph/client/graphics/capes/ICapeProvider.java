package xyz.nifeather.morph.client.graphics.capes;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.ResourceLocation;

public interface ICapeProvider
{
    /**
     * 尝试获取和profile对应的披风
     *
     * @param profile {@link GameProfile}
     * @return 返回一个CompleteableFuture, 若披风不可用，则String为NULL
     */
    public CompletableFuture<Optional<ResourceLocation>> getCapeAsync(GameProfile profile);
}
