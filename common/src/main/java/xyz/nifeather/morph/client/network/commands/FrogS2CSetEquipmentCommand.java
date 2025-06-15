package xyz.nifeather.morph.client.network.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.apache.commons.lang3.NotImplementedException;
import xiamomc.pluginbase.Exceptions.NullDependencyException;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.network.BasicServerHandler;
import xyz.nifeather.morph.network.commands.S2C.AbstractS2CCommand;
import xyz.nifeather.morph.network.commands.S2C.set.S2CSetFakeEquipCommand;
import xyz.nifeather.morph.network.utils.Asserts;

import java.util.Map;

public class FrogS2CSetEquipmentCommand extends AbstractS2CCommand<ItemStack>
{
    private final S2CSetFakeEquipCommand.ProtocolEquipmentSlot slot;
    private final ItemStack item;

    public FrogS2CSetEquipmentCommand(ItemStack item, S2CSetFakeEquipCommand.ProtocolEquipmentSlot slot)
    {
        this.slot = slot;
        this.item = item;
    }

    @Override
    public String getBaseName()
    {
        return "v2_set_fake_equip";
    }

    @Override
    public void onCommand(BasicServerHandler<?> handler)
    {
    }

    @Override
    public Map<String, String> generateArgumentMap()
    {
        throw new NotImplementedException("Not implemented on mod side yet.");

        //return Map.of(
        //        "slot", this.slot.toString(),
        //        "type", this.item.getType().key().asString(),
        //        "name", ItemUtils.getItemJsonName(this.item),
        //        "enchantments", gson().toJson(item.getEnchantments().keySet().stream().toList())
        //);
    }

    private static ItemStack decodeItemType(String input)
    {
        var client = Minecraft.getInstance();
        var itemRegistry = client.level.registryAccess()
                .lookup(Registries.ITEM)
                .orElseThrow();

        var itemType = ResourceLocation.tryParse(input);
        if (itemType == null)
            throw new NullDependencyException("No item match for server input '%s'!".formatted(input));

        var item = itemRegistry.getOptional(itemType)
                .orElseThrow(() -> new NullDependencyException("No item available for server input '%s'!".formatted(itemType)));

        var stack = new ItemStack(item);
        stack.setCount(1);

        return stack;
    }

    private static void decodeEnchantments(ItemStack stack, String input)
    {
        var client = Minecraft.getInstance();

        Map<?, ?> enchMap = gson().fromJson(input, Map.class);
        var enchantmentRegistry = client.level.registryAccess()
                .lookup(Registries.ENCHANTMENT)
                .orElseThrow();

        enchMap.forEach((k, v) ->
        {
            String id = k.toString();
            int lvl;

            try
            {
                lvl = Integer.parseInt(v.toString());
            }
            catch (Throwable ignored)
            {
                FeatherMorphClientBootstrap.LOGGER.warn("Unable to parse enchantment level, skipping '%s'".formatted(id));
                return;
            }

            ResourceLocation rl = ResourceLocation.tryParse(id);
            if (rl == null) return;

            var enchOptional = enchantmentRegistry.getOptional(rl);
            if (enchOptional.isEmpty()) return;

            Holder<Enchantment> enchantment = Holder.direct(enchOptional.get());
            stack.enchant(enchantment, lvl);
        });
    }

    private static void decodeArmorTrim(ItemStack stack, String input)
    {
        var client = Minecraft.getInstance();

        Map<?, ?> trimMap = gson().fromJson(input, Map.class);
        if (!trimMap.containsKey("material") || !trimMap.containsKey("pattern"))
            throw new NullDependencyException("Not enough arguments! Missing 'material' or 'pattern' field in 'armor_trim' section.");

        String materialString = trimMap.get("material").toString();
        String patternString = trimMap.get("pattern").toString();

        var trimMaterialRegistry = client.level.registryAccess()
                .lookup(Registries.TRIM_MATERIAL)
                .orElseThrow();

        ResourceLocation materialRl = ResourceLocation.tryParse(materialString);
        if (materialRl == null)
            throw new RuntimeException("No trim material found for input '%s'".formatted(materialString));

        var material = trimMaterialRegistry.getOptional(materialRl)
                .orElseThrow(() -> new NullDependencyException("No trim available for input '%s'!".formatted(materialString)));
        var materialHolder = Holder.direct(material);

        var patternMaterialRegistry = client.level.registryAccess()
                .lookup(Registries.TRIM_PATTERN)
                .orElseThrow();

        ResourceLocation patternRl = ResourceLocation.tryParse(patternString);
        if (patternRl == null)
            throw new RuntimeException("No trim material found for input '%s'".formatted(materialString));

        var pattern = patternMaterialRegistry.getOptional(patternRl)
                .orElseThrow(() -> new NullDependencyException("No pattern available for input '%s'!".formatted(patternString)));
        var patternHolder = Holder.direct(pattern);

        stack.set(DataComponents.TRIM, new ArmorTrim(materialHolder, patternHolder));
    }

    public static ClientSetEquipCommand fromArguments(Map<String, String> arguments) throws RuntimeException
    {
        var slot = S2CSetFakeEquipCommand.ProtocolEquipmentSlot.valueOf(Asserts.getStringOrThrow(arguments, "slot").toUpperCase());

        var client = Minecraft.getInstance();
        var stack = decodeItemType(Asserts.getStringOrThrow(arguments, "type"));

        try
        {
            int count = Integer.parseInt(Asserts.getStringOrThrow(arguments, "count"));
            stack.setCount(count);
        }
        catch (Throwable t)
        {
            FeatherMorphClientBootstrap.LOGGER.error("Can't decode item count: " + t.getMessage());
        }

        var nameString = Asserts.getStringOrThrow(arguments, "name");
        var name = Component.Serializer.fromJson(nameString, client.level.registryAccess());
        stack.set(DataComponents.ITEM_NAME, name);

        decodeEnchantments(stack, Asserts.getStringOrThrow(arguments, "enchantments"));

        if (arguments.containsKey("armor_trim"))
            decodeArmorTrim(stack, arguments.get("armor_trim"));

        return new ClientSetEquipCommand(stack, slot);
    }
}
