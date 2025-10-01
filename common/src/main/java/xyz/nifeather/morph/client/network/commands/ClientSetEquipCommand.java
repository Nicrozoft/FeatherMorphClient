package xyz.nifeather.morph.client.network.commands;

import com.google.gson.JsonParser;
import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.utilties.NbtUtils;
import xyz.nifeather.morph.network.commands.S2C.set.S2CSetFakeEquipCommand;
import xyz.nifeather.morph.network.utils.Asserts;
import xyz.nifeather.morph.network.utils.ProtocolEquipmentSlot;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("removal")
public class ClientSetEquipCommand extends S2CSetFakeEquipCommand<ItemStack>
{
    public ClientSetEquipCommand(ItemStack item, ProtocolEquipmentSlot slot)
    {
        super(item, slot);
    }

    public static ClientSetEquipCommand fromArguments(Map<String, String> arguments) throws RuntimeException
    {
        var slot = ProtocolEquipmentSlot.valueOf(Asserts.getStringOrThrow(arguments, "slot").toUpperCase());

        int dataVersion = SharedConstants.getCurrentVersion().dataVersion().version();

        if (arguments.containsKey("data_version"))
            dataVersion = Integer.parseInt(arguments.get("data_version"));

        var stack = jsonToStack(Asserts.getStringOrThrow(arguments, "item"), dataVersion);

        Objects.requireNonNull(stack, "No item stack for input NBT '%s'".formatted(Asserts.getStringOrThrow(arguments, "item")));

        return new ClientSetEquipCommand(stack, slot);
    }

    @Override
    public Map<String, String> generateArgumentMap()
    {
        var registry = Minecraft.getInstance().level.registryAccess();
        var json = ItemStack.CODEC.encodeStart(registry.createSerializationContext(JsonOps.INSTANCE), getItemStack()).result();

        if (json.isEmpty())
            throw new RuntimeException("Failed to encode item!");

        return Map.of(
                "slot", getSlot().toString(),
                "item", gson().toJson(json.get()),
                "data_version", "" + SharedConstants.getCurrentVersion().dataVersion().version()
        );
    }


    @Nullable
    public static ItemStack jsonToStack(String rawJson, int sourceDataVersion)
    {
        var world = Minecraft.getInstance().level;
        if (world == null)
            throw new NullPointerException("Called jsonToStack but client world is null?!");

        var registry = Minecraft.getInstance().level.registryAccess();

        CompoundTag tag = NbtUtils.parseSNbt(rawJson);
        if (tag != null && tag.getStringOr("id", "no").equals("minecraft:air"))
            return new ItemStack(Items.AIR, 1);

        var ops = registry.createSerializationContext(NbtOps.INSTANCE);
        int currentDataVersion = SharedConstants.getCurrentVersion().dataVersion().version();

        if (sourceDataVersion >= currentDataVersion)
            sourceDataVersion = currentDataVersion;

        var fixer = Minecraft.getInstance().getFixerUpper()
                .update(References.ITEM_STACK, new Dynamic<>(ops, tag), sourceDataVersion, currentDataVersion);

        var item = ItemStack.CODEC.decode(fixer);

        if (item.result().isPresent())
            return item.result().get().getFirst();

        return null;
    }
}