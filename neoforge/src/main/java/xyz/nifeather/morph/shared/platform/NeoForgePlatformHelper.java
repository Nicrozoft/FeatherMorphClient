package xyz.nifeather.morph.shared.platform;

import com.mojang.brigadier.arguments.ArgumentType;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
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
import xyz.nifeather.morph.client.mixin.accessors.KeyBindingAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class NeoForgePlatformHelper implements PlatformHelper {
    public static final Event<ServerStartTick> START_SERVER_TICK = EventFactory.createArrayBacked(ServerStartTick.class, callbacks -> server -> {
        for (ServerStartTick event : callbacks) {
            event.onStartTick(server);
        }
    });
    private boolean eventsRegistered = false;

    private static <T extends CustomPacketPayload> Packet<ServerCommonPacketListener> createC2SPacket(T payload) {
        Objects.requireNonNull(payload, "Payload cannot be null");
        Objects.requireNonNull(payload.type(), "CustomPayload#getId() cannot return null for payload class: " + payload.getClass());

        return new ServerboundCustomPayloadPacket(payload);
    }

    @Override
    public boolean fabric() {
        return false;
    }

    @Override
    public void registerClientJoinEvent(Runnable callback) {
        NeoForgePlatformHelperHolder.joinCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @SubscribeEvent
    public void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        NeoForgePlatformHelperHolder.joinCallbacks.forEach(Runnable::run);
    }

    @Override
    public void registerClientDisconnectEvent(Runnable callback) {
        NeoForgePlatformHelperHolder.disconnectCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        NeoForgePlatformHelperHolder.disconnectCallbacks.forEach(Runnable::run);
    }

    @Override
    public void registerClientTickEndEvent(EndTick callback) {
        NeoForgePlatformHelperHolder.clientTickCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerWorldTickEndEvent(EndWorldTick callback) {
        NeoForgePlatformHelperHolder.worldTickEndCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerHudRenderEvent(HudRenderCallback callback) {
        if (!Dist.CLIENT.isClient()) return;
        NeoForgePlatformHelperHolder.hudRenderCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerServerStoppingEvent(ServerStopping callback) {
        NeoForgePlatformHelperHolder.serverStoppingCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerWorldLoadEvent(WorldLoad callback) {
        NeoForgePlatformHelperHolder.worldLoadCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerServerStartTickEvent(ResourceLocation phase, ServerStartTick callback) {
        NeoForgePlatformHelper.START_SERVER_TICK.register(phase, callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerAfterKilledOtherEntityEvent(AfterKilledOtherEntity callback) {
        NeoForgePlatformHelperHolder.afterKilledOtherEntityCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public void registerCommandRegistrationEvent(CommandRegistrationCallback callback) {
        NeoForgePlatformHelperHolder.commandRegistrationCallbacks.add(callback);
        ensureEventsRegistered();
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(ResourceLocation id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer) {
        NeoForgePlatformHelperHolder.ARGUMENT_TYPE_CLASSES.put(clazz, serializer);
        NeoForgePlatformHelperHolder.ARGUMENT_TYPES.put(id, serializer);
    }

    @Override
    public KeyMapping registerKeyBinding(KeyMapping binding) {
        Objects.requireNonNull(binding, "key binding cannot be null");
        return KeyBindingRegistryImpl.registerKeyBinding(binding);
    }

    @Override
    public void sendNetworkPacket(CustomPacketPayload payload) {
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(createC2SPacket(payload));
            return;
        }

        throw new IllegalStateException("Cannot send packets when not in game!");
    }

    private void ensureEventsRegistered() {
        if (!eventsRegistered) {
            IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
            bus.addListener(RegisterEvent.class, event ->
                    event.register(Registries.COMMAND_ARGUMENT_TYPE, helper -> {
                        NeoForgePlatformHelperHolder.ARGUMENT_TYPE_CLASSES.forEach(ArgumentTypeInfos::registerByClass);
                        NeoForgePlatformHelperHolder.ARGUMENT_TYPES.forEach(helper::register);
                    }));
            bus.addListener(RegisterKeyMappingsEvent.class, KeyBindingRegistryImpl::process);
            NeoForge.EVENT_BUS.register(this);
            eventsRegistered = true;
        }
    }

    @SubscribeEvent
    public void onClientTickEnd(ClientTickEvent.Post event) {
        NeoForgePlatformHelperHolder.clientTickCallbacks.forEach(callback -> callback.onEndTick(Minecraft.getInstance()));
    }

    @SubscribeEvent
    public void onWorldTickEnd(LevelTickEvent.Post event) {
        if (Dist.CLIENT.isClient() && event.getLevel() instanceof ClientLevel clientLevel)
            NeoForgePlatformHelperHolder.worldTickEndCallbacks.forEach(callback -> callback.onEndTick(clientLevel));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onHudRender(RenderGuiEvent.Post event) {
        NeoForgePlatformHelperHolder.hudRenderCallbacks.forEach(callback -> callback.onRender(event.getGuiGraphics(), event.getPartialTick()));
    }

    @SubscribeEvent
    public void onServerStoping(ServerStoppingEvent event) {
        NeoForgePlatformHelperHolder.serverStoppingCallbacks.forEach(callback -> callback.onServerStopping(event.getServer()));
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel)
            NeoForgePlatformHelperHolder.worldLoadCallbacks.forEach(callback -> callback.onWorldLoad(event.getLevel().getServer(), serverLevel));
    }

    @SubscribeEvent
    public void onServerStartTick(ServerTickEvent.Pre event) {
        NeoForgePlatformHelper.START_SERVER_TICK.invoker().onStartTick(event.getServer());
        //NeoForgePlatformHelperHolder.serverStartTickCallbacks.forEach(callback -> callback.onStartTick(event.getServer()));
    }

    //TODO
    @SubscribeEvent
    public void onAfterKilled(LivingDeathEvent event) {
        var source = (LivingEntity) event.getSource().getDirectEntity();
        if (source == null) return;
        var world = (ServerLevel) source.level();
        var entity = event.getEntity();
        NeoForgePlatformHelperHolder.afterKilledOtherEntityCallbacks.forEach(callback -> callback.afterKilledOtherEntity(world, entity, source));
    }

    @SubscribeEvent
    public void setCommandRegistrationCallbacks(RegisterCommandsEvent event) {
        NeoForgePlatformHelperHolder.commandRegistrationCallbacks.forEach(callback -> callback.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
    }

    private static class KeyBindingRegistryImpl {
        private static final List<KeyMapping> MODDED_KEY_BINDINGS = new ReferenceArrayList<>(); // ArrayList with identity based comparisons for contains/remove/indexOf etc., required for correctly handling duplicate keybinds
        private static boolean processed;

        private static Map<String, Integer> getCategoryMap() {
            return KeyBindingAccessor.fabric_getCategoryMap();
        }

        public static boolean addCategory(String categoryTranslationKey) {
            Map<String, Integer> map = getCategoryMap();

            if (map.containsKey(categoryTranslationKey)) {
                return false;
            }

            Optional<Integer> largest = map.values().stream().max(Integer::compareTo);
            int largestInt = largest.orElse(0);
            map.put(categoryTranslationKey, largestInt + 1);
            return true;
        }

        public static KeyMapping registerKeyBinding(KeyMapping binding) {
            if (processed) {
                throw new IllegalStateException("Key bindings have already been processed");
            }

            for (KeyMapping existingKeyBindings : MODDED_KEY_BINDINGS) {
                if (existingKeyBindings == binding) {
                    throw new IllegalArgumentException("Attempted to register a key binding twice: " + binding.getName());
                } else if (existingKeyBindings.getName().equals(binding.getName())) {
                    throw new IllegalArgumentException("Attempted to register two key bindings with equal ID: " + binding.getName() + "!");
                }
            }

            // This will do nothing if the category already exists.
            addCategory(binding.getCategory());
            MODDED_KEY_BINDINGS.add(binding);
            return binding;
        }

        public static void process(RegisterKeyMappingsEvent event) {
            MODDED_KEY_BINDINGS.forEach(event::register);
            processed = true;
        }
    }

    private static class NeoForgePlatformHelperHolder {
        @SuppressWarnings("rawtypes")
        static final Map<Class, ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_CLASSES = new HashMap<>();
        static final Map<ResourceLocation, ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = new HashMap<>();
        static final List<Runnable> joinCallbacks = new ArrayList<>();
        static final List<Runnable> disconnectCallbacks = new ArrayList<>();
        static final List<EndTick> clientTickCallbacks = new ArrayList<>();
        static final List<EndWorldTick> worldTickEndCallbacks = new ArrayList<>();
        static final List<HudRenderCallback> hudRenderCallbacks = new ArrayList<>();
        static final List<ServerStopping> serverStoppingCallbacks = new ArrayList<>();
        static final List<WorldLoad> worldLoadCallbacks = new ArrayList<>();
        static final List<AfterKilledOtherEntity> afterKilledOtherEntityCallbacks = new ArrayList<>();
        static final List<CommandRegistrationCallback> commandRegistrationCallbacks = new ArrayList<>();
    }
}
