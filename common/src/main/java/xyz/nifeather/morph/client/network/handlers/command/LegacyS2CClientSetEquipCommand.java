package xyz.nifeather.morph.client.network.handlers.command;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.fmccl.network.commands.S2C.set.NetheriteS2CSetFakeEquipCommand;

public class LegacyS2CClientSetEquipCommand extends NetheriteS2CSetFakeEquipCommand<ItemStack>
{
    private static final Logger log = LoggerFactory.getLogger(LegacyS2CClientSetEquipCommand.class);

    public LegacyS2CClientSetEquipCommand(ItemStack item, ProtocolEquipmentSlot slot)
    {
        super(item, slot);
    }

    @Override
    public String serializeArguments()
    {
        return "";
    }

    public static LegacyS2CClientSetEquipCommand from(String rawArguments)
    {
        //temp to array
        var dat = rawArguments.split(" ", 2);

        if (dat.length != 2) return null;

        var stack = jsonToStack(dat[1]);
        if (stack == null) return null;

        var slot = ProtocolEquipmentSlot.valueOf(dat[0].toUpperCase());

        return new LegacyS2CClientSetEquipCommand(stack, slot);
    }

    @Nullable
    private static ItemStack jsonToStack(String rawJson)
    {
        var world = Minecraft.getInstance().level;
        if (world == null)
            throw new NullPointerException("Called jsonToStack but client world is null?!");

        var registry = Minecraft.getInstance().level.registryAccess();

        var item = ItemStack.CODEC.decode(registry.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(rawJson));

        if (item.result().isPresent())
            return item.result().get().getFirst();

        return null;
    }
}