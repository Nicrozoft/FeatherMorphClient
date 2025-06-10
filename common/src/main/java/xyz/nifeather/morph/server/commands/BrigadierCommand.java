package xyz.nifeather.morph.server.commands;

import net.minecraft.server.MinecraftServer;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.ServerPluginObject;

public abstract class BrigadierCommand extends ServerPluginObject implements IBrigadierCommand
{
    protected final MinecraftServer server;

    protected BrigadierCommand()
    {
        server = MorphServerLoader.mcserver;
        if (server == null)
            throw new NullPointerException("Server instance is NULL!");
    }
}
