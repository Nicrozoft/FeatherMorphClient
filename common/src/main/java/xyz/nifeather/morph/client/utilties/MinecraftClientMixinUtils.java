package xyz.nifeather.morph.client.utilties;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import xyz.nifeather.morph.client.entities.MorphLocalPlayer;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.server.Services;

public class MinecraftClientMixinUtils
{
    public static void setApiService(YggdrasilAuthenticationService authenticationService, File runDirectory)
    {
        /*
        Services apiServices = Services.create(authenticationService, runDirectory);
        apiServices.nameToIdCache().setExecutor(Minecraft.getInstance());

        MorphLocalPlayer.setMinecraftAPIServices(apiServices, Minecraft.getInstance());
        */
    }
}
