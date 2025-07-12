package xyz.nifeather.morph.shared.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;

public class FabricPlatformHelper implements PlatformHelper
{
    @Override
    public boolean fabric()
    {
        return true;
    }

    @Override
    public void registerClientTickEndEvent(EndTick callback)
    {
        ClientTickEvents.END_CLIENT_TICK.register(callback::onEndTick);
    }

    @Override
    public void registerWorldTickEndEvent(EndWorldTick callback)
    {
        ClientTickEvents.END_WORLD_TICK.register(callback::onEndTick);
    }

    @Override
    public void registerHudRenderEvent(HudRenderCallback callback)
    {
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register(callback::onRender);
    }

    @Override
    public void registerServerStoppingEvent(ServerStopping callback)
    {
        ServerLifecycleEvents.SERVER_STOPPING.register(callback::onServerStopping);
    }

    @Override
    public void registerWorldLoadEvent(WorldLoad callback)
    {
        ServerWorldEvents.LOAD.register(callback::onWorldLoad);
    }

    @Override
    public void registerServerStartTickEvent(ResourceLocation phase, ServerStartTick callback)
    {
        ServerTickEvents.START_SERVER_TICK.register(phase, callback::onStartTick);
    }

    @Override
    public void registerAfterKilledOtherEntityEvent(AfterKilledOtherEntity callback)
    {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(callback::afterKilledOtherEntity);
    }

    @Override
    public void registerCommandRegistrationEvent(CommandRegistrationCallback callback)
    {
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register(callback::register);
    }

    @Override
    public KeyMapping registerPlatformKeyBinding(KeyMapping keyMapping)
    {
        return KeyBindingHelper.registerKeyBinding(keyMapping);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(ResourceLocation id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer)
    {
        ArgumentTypeRegistry.registerArgumentType(id, clazz, serializer);
    }
}
