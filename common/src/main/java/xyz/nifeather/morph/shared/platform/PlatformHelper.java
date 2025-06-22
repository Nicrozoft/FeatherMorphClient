package xyz.nifeather.morph.shared.platform;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/***
 * @author 404
 * @reason FabricAPI compatibility approach
 */
public interface PlatformHelper
{
    boolean fabric();

    void registerClientTickEndEvent(EndTick callback);

    void registerWorldTickEndEvent(EndWorldTick callback);

    void registerHudRenderEvent(HudRenderCallback callback);

    void registerServerStoppingEvent(ServerStopping callback);

    void registerWorldLoadEvent(WorldLoad callback);

    void registerServerStartTickEvent(ResourceLocation phase, ServerStartTick callback);

    void registerAfterKilledOtherEntityEvent(AfterKilledOtherEntity callback);

    void registerCommandRegistrationEvent(CommandRegistrationCallback callback);

    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> void registerArgumentType(
            ResourceLocation id, Class<? extends A> clazz, ArgumentTypeInfo<A, T> serializer);

    @FunctionalInterface
    interface HudRenderCallback
    {
        void onRender(GuiGraphics context, DeltaTracker renderTickCounter);
    }

    @FunctionalInterface
    interface WorldLoad
    {
        void onWorldLoad(MinecraftServer server, ServerLevel world);
    }

    @FunctionalInterface
    interface EndTick {
        void onEndTick(Minecraft client);
    }

    @FunctionalInterface
    interface EndWorldTick
    {
        void onEndTick(ClientLevel world);
    }

    @FunctionalInterface
    interface ServerStopping
    {
        void onServerStopping(MinecraftServer server);
    }

    @FunctionalInterface
    interface ServerStartTick
    {
        void onStartTick(MinecraftServer server);
    }

    @FunctionalInterface
    interface AfterKilledOtherEntity
    {
        /**
         * Called after an entity has killed another entity.
         *
         * @param world        the world
         * @param entity       the entity
         * @param killedEntity the entity which was killed by the {@code entity}
         */
        void afterKilledOtherEntity(ServerLevel world, Entity entity, LivingEntity killedEntity);
    }

    @FunctionalInterface
    interface CommandRegistrationCallback
    {
        /**
         * Called when the server is registering commands.
         *
         * @param dispatcher     the command dispatcher to register commands to
         * @param registryAccess object exposing access to the game's registries
         * @param environment    environment the registrations should be done for, used for commands that are dedicated or integrated server only
         */
        void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment);
    }
}
