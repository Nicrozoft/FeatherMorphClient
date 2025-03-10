package xyz.nifeather.morph;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.commands.arguments.AllAvailableDisguisesArgumentType;
import xyz.nifeather.morph.server.commands.arguments.DisguiseIdentifierArgumentType;
import xyz.nifeather.morph.shared.payload.*;

public class MorphFabricInitializer implements ModInitializer
{
    private final MorphServerLoader morphServerLoader = new MorphServerLoader();

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize()
    {
        PayloadTypeRegistry.playS2C().register(MorphInitChannelPayload.id, MorphInitChannelPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(MorphVersionChannelPayload.id, MorphVersionChannelPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MorphCommandPayload.id, MorphCommandPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(LegacyMorphVersionChannelPayload.id, LegacyMorphVersionChannelPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LegacyMorphCommandPayload.id, LegacyMorphCommandPayload.CODEC);

        morphServerLoader.onModLoad();

        MorphServerLoader.LOGGER.info("Register argument types...");
        MorphServerLoader.LOGGER.info("Sadly, we can't register these at runtime.");

        ArgumentTypeRegistry.registerArgumentType(Identifier.of("feathermorph:disguise_identifier_for_player"),
                DisguiseIdentifierArgumentType.class,
                ConstantArgumentSerializer.of(() -> DisguiseIdentifierArgumentType.INSTANCE));

        ArgumentTypeRegistry.registerArgumentType(Identifier.of("feathermorph:all_available"),
                AllAvailableDisguisesArgumentType.class,
                ConstantArgumentSerializer.of(() -> AllAvailableDisguisesArgumentType.INSTANCE));
    }
}
