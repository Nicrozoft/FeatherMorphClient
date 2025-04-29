package xyz.nifeather.morph.shared;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedValues
{
    public static boolean allowSinglePlayerDebugging = false;
    public static final Logger LOGGER = LoggerFactory.getLogger("FeatherMorph");

    private static final String morphNameSpace = "morphplugin";
    public static final String MOD_ID = "feathermorph-client";

    public static ResourceLocation initializeChannelIdentifier = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "init_v3");

    @Deprecated
    public static ResourceLocation versionChannelIdentifier = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "version_v2");

    public static ResourceLocation commandChannelIdentifier = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "commands_v3");

    public static final String newProtocolIdentify = "1_21_3_packetbuf";

    public static ResourceLocation versionChannelIdentifierLegacy = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "version");
    public static ResourceLocation commandChannelIdentifierLegacy = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "commands");

    public static boolean client_UseNewPacketSerializeMethod = false;
}
