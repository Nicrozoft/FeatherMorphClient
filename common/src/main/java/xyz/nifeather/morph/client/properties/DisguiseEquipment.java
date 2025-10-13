package xyz.nifeather.morph.client.properties;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import xyz.nifeather.morph.client.utilties.ClientItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DisguiseEquipment implements ISupportDiffs<DisguiseEquipment>
{
    private final Map<EquipmentSlot, ItemStack> itemStackMap = new ConcurrentHashMap<>();

    public DisguiseEquipment(Map<EquipmentSlot, ItemStack> items)
    {
        itemStackMap.putAll(items);
    }

    @Unmodifiable
    public Map<EquipmentSlot, ItemStack> contents()
    {
        return ImmutableMap.copyOf(itemStackMap);
    }

    /**
     * @param slot the slot to get the ItemStack
     * @return An {@link ItemStack}
     */
    public @NotNull ItemStack getItem(@NotNull EquipmentSlot slot)
    {
        return itemStackMap.getOrDefault(slot, ClientItemUtils.air);
    }

    public @Nullable ItemStack getItemOrNull(@Nullable EquipmentSlot slot)
    {
        return itemStackMap.getOrDefault(slot, null);
    }

    public @NotNull ItemStack getItemInMainHand()
    {
        return getItem(EquipmentSlot.MAINHAND);
    }

    public @NotNull ItemStack getItemInOffHand()
    {
        return getItem(EquipmentSlot.OFFHAND);
    }

    public ItemStack getHelmet()
    {
        return getItem(EquipmentSlot.HEAD);
    }

    public ItemStack getChestplate()
    {
        return getItem(EquipmentSlot.CHEST);
    }

    public ItemStack getLeggings()
    {
        return getItem(EquipmentSlot.LEGS);
    }

    public ItemStack getBoots()
    {
        return getItem(EquipmentSlot.FEET);
    }

    public boolean filterAll(Function<ItemStack, Boolean> filter)
    {
        boolean pass = true;
        for (Map.Entry<EquipmentSlot, ItemStack> entry : itemStackMap.entrySet())
            pass = filter.apply(entry.getValue()) && pass;

        return pass;
    }

    public DisguiseEquipmentBuilder cloneForEdit()
    {
        return builder(this);
    }

    public static DisguiseEquipmentBuilder builder()
    {
        return new DisguiseEquipmentBuilder();
    }

    public static DisguiseEquipmentBuilder builder(EntityEquipment other)
    {
        return new DisguiseEquipmentBuilder(other);
    }

    public static DisguiseEquipmentBuilder builder(DisguiseEquipment other)
    {
        return new DisguiseEquipmentBuilder(other.itemStackMap);
    }

    public static DisguiseEquipmentBuilder builder(Map<EquipmentSlot, ItemStack> map)
    {
        return new DisguiseEquipmentBuilder(map);
    }

    public static DisguiseEquipment copy(EntityEquipment other)
    {
        return builder(other).build();
    }

    public static DisguiseEquipment empty()
    {
        return builder().build();
    }

    @Override
    public DisguiseEquipment diff(DisguiseEquipment other)
    {
        Map<EquipmentSlot, ItemStack> changes = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values())
        {
            var ours = getItemOrNull(slot);
            var theirs = other.getItemOrNull(slot);

            if (!Objects.equals(ours, theirs))
                changes.put(slot, ClientItemUtils.itemOrAir(theirs));
        }

        return new DisguiseEquipment(changes);
    }

    public static class DisguiseEquipmentBuilder
    {
        private final Map<EquipmentSlot, ItemStack> itemMap = new ConcurrentHashMap<>();

        private static Map<EquipmentSlot, ItemStack> airMap()
        {
            var itemMap = new ConcurrentHashMap<EquipmentSlot, ItemStack>();
            for (EquipmentSlot value : EquipmentSlot.values())
                itemMap.put(value, ClientItemUtils.air);

            return itemMap;
        }

        public DisguiseEquipmentBuilder()
        {
            this(airMap());
        }

        public DisguiseEquipmentBuilder(@NotNull EntityEquipment entityEquipment)
        {
            this(Map.of(
                    EquipmentSlot.HEAD, ClientItemUtils.itemOrAir(entityEquipment.get(EquipmentSlot.HEAD)),
                    EquipmentSlot.CHEST, ClientItemUtils.itemOrAir(entityEquipment.get(EquipmentSlot.CHEST)),
                    EquipmentSlot.LEGS, ClientItemUtils.itemOrAir(entityEquipment.get(EquipmentSlot.LEGS)),
                    EquipmentSlot.FEET, ClientItemUtils.itemOrAir(entityEquipment.get(EquipmentSlot.FEET)),

                    EquipmentSlot.MAINHAND, ClientItemUtils.itemOrAir(entityEquipment.get(EquipmentSlot.MAINHAND)),
                    EquipmentSlot.OFFHAND, ClientItemUtils.itemOrAir(entityEquipment.get(EquipmentSlot.OFFHAND))
            ));
        }

        public DisguiseEquipmentBuilder(Map<EquipmentSlot, ItemStack> existing)
        {
            itemMap.putAll(existing);
        }

        public DisguiseEquipmentBuilder helmet(@NotNull ItemStack stack)
        {
            return forSlot(EquipmentSlot.HEAD, stack);
        }

        public DisguiseEquipmentBuilder chestplate(@NotNull ItemStack stack)
        {
            return forSlot(EquipmentSlot.CHEST, stack);
        }

        public DisguiseEquipmentBuilder leggings(@NotNull ItemStack stack)
        {
            return forSlot(EquipmentSlot.LEGS, stack);
        }

        public DisguiseEquipmentBuilder boots(@NotNull ItemStack stack)
        {
            return forSlot(EquipmentSlot.FEET, stack);
        }

        public DisguiseEquipmentBuilder mainHand(@NotNull ItemStack stack)
        {
            return forSlot(EquipmentSlot.MAINHAND, stack);
        }

        public DisguiseEquipmentBuilder offHand(@NotNull ItemStack stack)
        {
            return forSlot(EquipmentSlot.OFFHAND, stack);
        }

        public DisguiseEquipmentBuilder forSlot(EquipmentSlot slot, @NotNull ItemStack stack)
        {
            Objects.requireNonNull(stack, "Null item is not accepted");
            itemMap.put(slot, stack);
            return this;
        }

        public DisguiseEquipment build()
        {
            return new DisguiseEquipment(itemMap);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DisguiseEquipment other)) return false;
        return other.itemStackMap.equals(this.itemStackMap);
    }
}
