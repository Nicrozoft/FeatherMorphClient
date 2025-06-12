package xyz.nifeather.morph.shared;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedValues
{
    public static boolean allowSinglePlayerDebugging = false;
    public static final Logger LOGGER = LoggerFactory.getLogger("FeatherMorph");

    private static final String morphNameSpace = "morphplugin";
    public static final String MOD_ID = "feathermorph_client";

    public static final String newProtocolIdentify = "1_21_3_packetbuf";

    public static ResourceLocation initializeChannelV3 = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "init_v3");
    public static ResourceLocation commandChannelV3 = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "commands_v3");

    @Deprecated
    public static ResourceLocation versionChannelV2 = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "version_v2");
    public static ResourceLocation commandChannelV2 = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "commands_v2");

    public static ResourceLocation initializeChannelV1V2 = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "init");

    public static ResourceLocation versionChannelV1 = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "version");
    public static ResourceLocation commandChannelV1 = ResourceLocation.fromNamespaceAndPath(morphNameSpace, "commands");
}