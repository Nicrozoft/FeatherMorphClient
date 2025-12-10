package xyz.nifeather.morph.shared;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedValues
{
    public static boolean allowSinglePlayerDebugging = false;
    public static final Logger LOGGER = LoggerFactory.getLogger("FeatherMorph");

    private static final String morphNameSpace = "morphplugin";
    public static final String MOD_ID = "feathermorph_client";

    public static final String newProtocolIdentify = "1_21_3_packetbuf";

    public static Identifier initializeChannelV3 = Identifier.fromNamespaceAndPath(morphNameSpace, "init_v3");
    public static Identifier commandChannelV3 = Identifier.fromNamespaceAndPath(morphNameSpace, "commands_v3");

    @Deprecated
    public static Identifier versionChannelV2 = Identifier.fromNamespaceAndPath(morphNameSpace, "version_v2");
    public static Identifier commandChannelV2 = Identifier.fromNamespaceAndPath(morphNameSpace, "commands_v2");

    public static Identifier initializeChannelV1V2 = Identifier.fromNamespaceAndPath(morphNameSpace, "init");

    public static Identifier versionChannelV1 = Identifier.fromNamespaceAndPath(morphNameSpace, "version");
    public static Identifier commandChannelV1 = Identifier.fromNamespaceAndPath(morphNameSpace, "commands");
}