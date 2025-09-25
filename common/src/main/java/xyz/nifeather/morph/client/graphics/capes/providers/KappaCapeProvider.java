package xyz.nifeather.morph.client.graphics.capes.providers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.morph.client.graphics.capes.ICapeProvider;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 修改自 <a href="https://github.com/Hibiii/Kappa">Hibiii/Kappa</a>
 * <br>
 * 十分感谢Orz
 */
public final class KappaCapeProvider implements ICapeProvider
{
    //单独给披风请求开一个Worker，避免阻塞MainWorker上的其他请求
    private final ExecutorService capeService = Executors.newFixedThreadPool(3, runnable ->
    {
        var thread = new Thread(runnable);

        thread.setName("FeatherMorph Cape Worker");

        thread.setUncaughtExceptionHandler((t, error) ->
        {
            log.info("Error occurred in thread '%s': %s".formatted(t.getName(), error.getMessage()));
            error.printStackTrace();
        });

        return thread;
    });

    private static final Logger log = LoggerFactory.getLogger(KappaCapeProvider.class);

    private ExecutorService getCapeExecutor()
    {
        return capeService;
    }

    @Override
    public CompletableFuture<Optional<ResourceLocation>> getCapeAsync(GameProfile profile)
    {
        var uuid = profile.id();

        var existingRequest = onGoingRequests.getOrDefault(uuid, null);
        if (existingRequest != null)
            return existingRequest;

        var future = CompletableFuture.supplyAsync(() -> this.loadCape(profile), this.getCapeExecutor());

        onGoingRequests.put(uuid, future);
        future.thenAccept(optional -> onGoingRequests.remove(uuid));

        return future;
    }

    private final Map<UUID, CompletableFuture<Optional<ResourceLocation>>> onGoingRequests = new ConcurrentHashMap<>();

    // This loads the cape for one player, doesn't matter if it's the player or not.
    // Requires a callback, that receives the id for the cape
    public Optional<ResourceLocation> loadCape(GameProfile profile)
    {
        // Check if the player doesn't already have a cape.
        ResourceLocation existingCape = capes.get(profile.name());

        if (existingCape != null)
            return Optional.of(existingCape);

        var ofCape = this.tryUrl(profile, "https://optifine.net/capes/" + profile.name() + ".png");
        if (ofCape != null)
            return Optional.of(ofCape);

        var sOptifine = this.tryUrl(profile, "http://s.optifine.net/capes/" + profile.name() + ".png");
        return sOptifine == null ? Optional.empty() : Optional.of(sOptifine);
    }

    // This is a provider specific implementation.
    // Images are usually 46x22 or 92x44, and these work as expected (64x32, 128x64).
    // There are edge cages with sizes 184x88, 1024x512 and 2048x1024,
    // but these should work alright.
    private NativeImage uncrop(NativeImage in)
    {
        int srcHeight = in.getHeight(), srcWidth = in.getWidth();
        int zoom = (int) Math.ceil(in.getHeight() / 32f);

        NativeImage out = new NativeImage(64 * zoom, 32 * zoom, true);

        // NativeImage.copyFrom doesn't work! :(
        for (int x = 0; x < srcWidth; x++)
        {
            for (int y = 0; y < srcHeight; y++)
            {
                out.setPixel(x, y, in.getPixel(x, y));
            }
        }

        return out;
    }

    // This is where capes will be stored
    private static final Map<String, ResourceLocation> capes = new HashMap<String, ResourceLocation>();

    // Try to load a cape from an URL.
    // If this fails, it'll return false, and let us try another url.
    private @Nullable ResourceLocation tryUrl(GameProfile player, String targetUrl)
    {
        try
        {
            URL url = new URL(targetUrl);

            NativeImage tex = uncrop(NativeImage.read(url.openStream()));

            var id = CompletableFuture.supplyAsync(() ->
            {
                // 1.21.5: No longer allow creating texture async
                var texture = new DynamicTexture(() ->
                        "cape_tex_" + player.id().toString().toLowerCase().replace("-", "_"), tex);

                // Register texture is still allow async, but for sanity we do it on Minecraft thread
                ResourceLocation texID = ResourceLocation.fromNamespaceAndPath("kappa", player.id().toString().replace("-", "_"));
                Minecraft.getInstance().getTextureManager().register(texID, texture);

                return texID;
            }, Minecraft.getInstance()).join();

            capes.put(player.name(), id);
            return id;
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
        catch (Throwable t)
        {
            log.info("Error occurred while fetching/processing cape: " + t.getMessage());
            t.printStackTrace();
            return null;
        }
    }

    public KappaCapeProvider()
    {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> capes.clear());
    }
}