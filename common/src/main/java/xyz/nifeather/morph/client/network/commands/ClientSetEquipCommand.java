package xyz.nifeather.morph.client.network.commands;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.network.commands.S2C.set.S2CSetFakeEquipCommand;
import xyz.nifeather.morph.network.utils.Asserts;

import java.util.Map;
import java.util.Objects;

public class ClientSetEquipCommand extends S2CSetFakeEquipCommand<ItemStack> {
    public ClientSetEquipCommand(ItemStack item, ProtocolEquipmentSlot slot) {
        super(item, slot);
    }

    public static ClientSetEquipCommand fromArguments(Map<String, String> arguments) throws RuntimeException {
        var slot = ProtocolEquipmentSlot.valueOf(Asserts.getStringOrThrow(arguments, "slot").toUpperCase());
        var stack = jsonToStack(Asserts.getStringOrThrow(arguments, "item"));

        Objects.requireNonNull(stack, "No item stack for input NBT '%s'".formatted(Asserts.getStringOrThrow(arguments, "item")));

        return new ClientSetEquipCommand(stack, slot);
    }

    @Nullable
    private static ItemStack jsonToStack(String rawJson) {
        var world = Minecraft.getInstance().level;
        if (world == null)
            throw new NullPointerException("Called jsonToStack but client world is null?!");

        var registry = Minecraft.getInstance().level.registryAccess();

        var item = ItemStack.CODEC.decode(registry.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(rawJson));

        if (item.result().isPresent())
            return item.result().get().getFirst();

        return null;
    }

    @Override
    public Map<String, String> generateArgumentMap() {
        var registry = Minecraft.getInstance().level.registryAccess();
        var json = ItemStack.CODEC.encodeStart(registry.createSerializationContext(JsonOps.INSTANCE), getItemStack()).result();

        if (json.isEmpty())
            throw new RuntimeException("Failed to encode item!");

        return Map.of(
                "slot", getSlot().toString(),
                "item", gson().toJson(json.get())
        );
    }
}
