package xyz.nifeather.morph.client.network.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
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

    public static ClientSetEquipCommand fromArguments(Map<String, String> arguments) throws RuntimeException
    {
        var slot = S2CSetFakeEquipCommand.ProtocolEquipmentSlot.valueOf(Asserts.getStringOrThrow(arguments, "slot").toUpperCase());

        var typeString = Asserts.getStringOrThrow(arguments, "type");
        var nameString = Asserts.getStringOrThrow(arguments, "name");
        var enchMapString = Asserts.getStringOrThrow(arguments, "enchantments");

        var client = Minecraft.getInstance();
        var itemRegistry = client.level.registryAccess()
                .lookup(Registries.ITEM)
                .orElseThrow();

        var itemType = ResourceLocation.tryParse(typeString);
        if (itemType == null)
            throw new NullDependencyException("No item match for server input '%s'!".formatted(typeString));

        var item = itemRegistry.getOptional(itemType)
                .orElseThrow(() -> new NullDependencyException("No item available for server input '%s'!".formatted(itemType)));

        var stack = new ItemStack(item);
        stack.setCount(1);

        var name = Component.Serializer.fromJson(nameString, client.level.registryAccess());
        stack.set(DataComponents.ITEM_NAME, name);

        Map<?, ?> enchMap = gson().fromJson(enchMapString, Map.class);
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

        return new ClientSetEquipCommand(stack, slot);
    }
}
