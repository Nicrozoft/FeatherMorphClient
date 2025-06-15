package xyz.nifeather.morph.client.network.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixerUpper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.apache.commons.lang3.NotImplementedException;
import xiamomc.pluginbase.Exceptions.NullDependencyException;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.utilties.NbtHelperCopy;
import xyz.nifeather.morph.client.utilties.NbtUtils;
import xyz.nifeather.morph.network.BasicServerHandler;
import xyz.nifeather.morph.network.commands.S2C.AbstractS2CCommand;
import xyz.nifeather.morph.network.commands.S2C.set.S2CSetFakeEquipCommand;
import xyz.nifeather.morph.network.utils.Asserts;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    }

    public static ClientSetEquipCommand fromArguments(Map<String, String> arguments) throws RuntimeException
    {
        // Basic elements: slot, type, count, damage
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

        var damageString = Asserts.getStringOrThrow(arguments, "damage");
        try
        {
            int damage = Integer.parseInt(damageString);
            stack.setDamageValue(damage);
        }
        catch (Throwable t)
        {
            FeatherMorphClientBootstrap.LOGGER.error("Can't decode item damage: " + t.getMessage());
        }

        // Optional elements: name, enchantments, armor_trim, banner_pattern, profile, custom_model_data

        if (arguments.containsKey("name"))
        {
            var nameString = arguments.get("name");
            var name = Component.Serializer.fromJson(nameString, client.level.registryAccess());
            stack.set(DataComponents.ITEM_NAME, name);
        }

        if (arguments.containsKey("enchantments"))
            decodeEnchantments(stack, arguments.get("enchantments"));

        if (arguments.containsKey("armor_trim"))
            decodeArmorTrim(stack, arguments.get("armor_trim"));

        if (arguments.containsKey("banner_pattern"))
            decodeBannerPattern(stack, arguments.get("banner_pattern"));

        if (arguments.containsKey("profile"))
            decodeProfile(stack, arguments.get("profile"));

        if (arguments.containsKey("custom_model_data"))
            decodeCustomModelData(stack, arguments.get("custom_model_data"));

        if (arguments.containsKey("base_color"))
            decodeBaseColor(stack, arguments.get("base_color"));

        return new ClientSetEquipCommand(stack, slot);
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

    private static void decodeBannerPattern(ItemStack stack, String input)
    {
        var client = Minecraft.getInstance();

        List<BannerPatternLayers.Layer> bannerLayers = new ObjectArrayList<>();
        List<?> stringList = gson().fromJson(input, List.class);
        for (Object o : stringList)
        {
            String str = o.toString();

            Map<?, ?> patternConfigureMap = gson().fromJson(str, Map.class);

            if (!patternConfigureMap.containsKey("color") || !patternConfigureMap.containsKey("pattern_type"))
            {
                FeatherMorphClientBootstrap.LOGGER.warn("No color or pattern_type key present, ignoring...");
                continue;
            }

            String patternTypeString = patternConfigureMap.get("pattern_type").toString();
            String colorString = patternConfigureMap.get("color").toString();

            var bannerPatternRegistry = client.level.registryAccess()
                    .lookup(Registries.BANNER_PATTERN)
                    .orElseThrow();

            ResourceLocation typeRl = ResourceLocation.parse(patternTypeString);
            var pattern = Objects.requireNonNull(bannerPatternRegistry.getValue(typeRl), "No banner pattern matches the input '%s'!".formatted(patternTypeString));

            var color = Arrays.stream(DyeColor.values())
                    .filter(name -> name.getName().equalsIgnoreCase(colorString))
                    .findFirst()
                    .orElseThrow(() -> new NullDependencyException("No DyeColor found for input '%s'".formatted(colorString)));

            bannerLayers.add(new BannerPatternLayers.Layer(Holder.direct(pattern), color));
        }

        BannerPatternLayers bannerPatternLayers = new BannerPatternLayers(bannerLayers);
        stack.set(DataComponents.BANNER_PATTERNS, bannerPatternLayers);
    }

    private static void decodeProfile(ItemStack stack, String input)
    {
        try
        {
            var nbt = NbtUtils.parseOrThrow(input);
            var profile = NbtHelperCopy.toGameProfile(nbt);

            if (profile == null)
                return;

            stack.set(DataComponents.PROFILE, new ResolvableProfile(profile));
        }
        catch (CommandSyntaxException ignored)
        {
        }
    }

    private static void decodeCustomModelData(ItemStack stack, String input) throws NumberFormatException
    {
        Map<?, ?> map = gson().fromJson(input, Map.class);

        List<Boolean> flags = new ObjectArrayList<>();
        List<Float> floats = new ObjectArrayList<>();
        List<String> strings = new ObjectArrayList<>();
        List<Integer> colors = new ObjectArrayList<>();

        if (map.containsKey("flags"))
        {
            List<?> raw = gson().fromJson(map.get("flags").toString(), List.class);
            raw.forEach(v -> flags.add(Boolean.parseBoolean(v.toString())));
        }

        if (map.containsKey("floats"))
        {
            List<?> raw = gson().fromJson(map.get("floats").toString(), List.class);
            raw.forEach(v -> floats.add(Float.parseFloat(v.toString())));
        }

        if (map.containsKey("strings"))
        {
            List<?> raw = gson().fromJson(map.get("strings").toString(), List.class);
            raw.forEach(v -> strings.add(v.toString()));
        }

        if (map.containsKey("colors"))
        {
            List<?> raw = gson().fromJson(map.get("colors").toString(), List.class);
            raw.forEach(v -> colors.add(Integer.parseInt(v.toString())));
        }

        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(floats, flags, strings, colors));
    }

    private static void decodeBaseColor(ItemStack stack, String input)
    {
        DyeColor color = DyeColor.valueOf(input.toUpperCase());

        stack.set(DataComponents.BASE_COLOR, color);
    }
}
