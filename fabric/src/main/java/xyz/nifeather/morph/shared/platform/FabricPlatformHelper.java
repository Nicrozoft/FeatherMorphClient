package xyz.nifeather.morph.shared.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.Identifier;

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
        ClientTickEvents.END_LEVEL_TICK.register(callback::onEndTick);
    }

    @Override
    public void registerHudRenderEvent(HudRenderCallback callback)
    {
        HudElementRegistry.addLast(Identifier.parse("feathermorph:hud_main"), callback::onRender);
    }

    @Override
    public void registerServerStoppingEvent(ServerStopping callback)
    {
        ServerLifecycleEvents.SERVER_STOPPING.register(callback::onServerStopping);
    }

    @Override
    public void registerWorldLoadEvent(WorldLoad callback)
    {
        ServerLevelEvents.LOAD.register(callback::onWorldLoad);
    }

    @Override
    public void registerServerStartTickEvent(Identifier phase, ServerStartTick callback)
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
        return KeyMappingHelper.registerKeyMapping(keyMapping);
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(Identifier id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer)
    {
        ArgumentTypeRegistry.registerArgumentType(id, clazz, serializer);
    }
}
