package xyz.nifeather.morph.shared.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeoForgePlatformHelper implements PlatformHelper {
    public static final Event<ServerStartTick> START_SERVER_TICK = EventFactory.createArrayBacked(ServerStartTick.class, callbacks -> server ->
    {
        for (ServerStartTick event : callbacks)
        {
            event.onStartTick(server);
        }
    });
    private boolean eventsRegistered = false;

    @Override
    public boolean fabric()
    {
        return false;
    }

    @Override
    public void registerClientTickEndEvent(EndTick callback)
    {
        NeoForgePlatformHelperHolder.clientTickCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerWorldTickEndEvent(EndWorldTick callback)
    {
        NeoForgePlatformHelperHolder.worldTickEndCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerHudRenderEvent(HudRenderCallback callback)
    {
        if (!Dist.CLIENT.isClient()) return;
        NeoForgePlatformHelperHolder.hudRenderCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerServerStoppingEvent(ServerStopping callback)
    {
        NeoForgePlatformHelperHolder.serverStoppingCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerWorldLoadEvent(WorldLoad callback)
    {
        NeoForgePlatformHelperHolder.worldLoadCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerServerStartTickEvent(Identifier phase, ServerStartTick callback)
    {
        NeoForgePlatformHelper.START_SERVER_TICK.register(phase, callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerAfterKilledOtherEntityEvent(AfterKilledOtherEntity callback)
    {
        NeoForgePlatformHelperHolder.afterKilledOtherEntityCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerCommandRegistrationEvent(CommandRegistrationCallback callback)
    {
        NeoForgePlatformHelperHolder.commandRegistrationCallbacks.add(callback);
        ensureEventsRegistered();
    }

    public final NeoForgeKeybindingHelper keybindingHelper = new NeoForgeKeybindingHelper();

    @Override
    public KeyMapping registerPlatformKeyBinding(KeyMapping keyMapping)
    {
        keybindingHelper.addKeybind(keyMapping);
        return keyMapping;
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(Identifier id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer)
    {
        NeoForgePlatformHelperHolder.ARGUMENT_TYPE_CLASSES.put(clazz, serializer);
        NeoForgePlatformHelperHolder.ARGUMENT_TYPES.put(id, serializer);
    }

    private void ensureEventsRegistered()
    {
        if (!eventsRegistered) {
            IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
            bus.addListener(RegisterEvent.class, event ->
                    event.register(Registries.COMMAND_ARGUMENT_TYPE, helper ->
                    {
                        NeoForgePlatformHelperHolder.ARGUMENT_TYPE_CLASSES.forEach(ArgumentTypeInfos::registerByClass);
                        NeoForgePlatformHelperHolder.ARGUMENT_TYPES.forEach(helper::register);
                    }));
            NeoForge.EVENT_BUS.register(this);
            eventsRegistered = true;
        }
    }

    @SubscribeEvent
    public void onClientTickEnd(ClientTickEvent.Post event)
    {
        NeoForgePlatformHelperHolder.clientTickCallbacks.forEach(callback -> callback.onEndTick(Minecraft.getInstance()));
    }

    @SubscribeEvent
    public void onWorldTickEnd(LevelTickEvent.Post event)
    {
        if (Dist.CLIENT.isClient() && event.getLevel() instanceof ClientLevel clientLevel)
            NeoForgePlatformHelperHolder.worldTickEndCallbacks.forEach(callback -> callback.onEndTick(clientLevel));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onHudRender(RenderGuiEvent.Post event)
    {
        NeoForgePlatformHelperHolder.hudRenderCallbacks.forEach(callback -> callback.onRender(event.getGuiGraphics(), event.getPartialTick()));
    }

    @SubscribeEvent
    public void onServerStoping(ServerStoppingEvent event)
    {
        NeoForgePlatformHelperHolder.serverStoppingCallbacks.forEach(callback -> callback.onServerStopping(event.getServer()));
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event)
    {
        if (event.getLevel() instanceof ServerLevel serverLevel)
            NeoForgePlatformHelperHolder.worldLoadCallbacks.forEach(callback -> callback.onWorldLoad(event.getLevel().getServer(), serverLevel));
    }

    @SubscribeEvent
    public void onServerStartTick(ServerTickEvent.Pre event)
    {
        NeoForgePlatformHelper.START_SERVER_TICK.invoker().onStartTick(event.getServer());
    }

    @SubscribeEvent
    public void onAfterKilled(LivingDeathEvent event)
    {
        var source = (LivingEntity) event.getSource().getDirectEntity();
        if (source == null) return;
        var world = (ServerLevel) source.level();
        var entity = event.getEntity();
        NeoForgePlatformHelperHolder.afterKilledOtherEntityCallbacks.forEach(callback -> callback.afterKilledOtherEntity(world, source, entity));
    }

    @SubscribeEvent
    public void setCommandRegistrationCallbacks(RegisterCommandsEvent event)
    {
        NeoForgePlatformHelperHolder.commandRegistrationCallbacks.forEach(callback -> callback.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
    }

    private static class NeoForgePlatformHelperHolder
    {
        @SuppressWarnings("rawtypes")
        static final Map<Class, ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_CLASSES = new HashMap<>();
        static final Map<Identifier, ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = new HashMap<>();
        static final List<EndTick> clientTickCallbacks = new ArrayList<>();
        static final List<EndWorldTick> worldTickEndCallbacks = new ArrayList<>();
        static final List<HudRenderCallback> hudRenderCallbacks = new ArrayList<>();
        static final List<ServerStopping> serverStoppingCallbacks = new ArrayList<>();
        static final List<WorldLoad> worldLoadCallbacks = new ArrayList<>();
        static final List<AfterKilledOtherEntity> afterKilledOtherEntityCallbacks = new ArrayList<>();
        static final List<CommandRegistrationCallback> commandRegistrationCallbacks = new ArrayList<>();
    }
}
