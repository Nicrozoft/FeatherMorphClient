package xyz.nifeather.morph.client.utilties;

import com.google.gson.Gson;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ClientItemUtils
{
    public static ItemStack itemOrAir(ItemStack stack)
    {
        return stack == null ? air : stack;
    }

    public static String itemToStr(ItemStack stack)
    {
        var item = ClientItemUtils.itemOrAir(stack);

        if (isAir(stack))
            return "{\"id\":\"minecraft:air\",\"Count\":1}";

        //CODEC
        var nmsCodec = ItemStack.CODEC;
        var json = nmsCodec.encode(item, JsonOps.INSTANCE, JsonOps.INSTANCE.empty())
                .result();

        if (json.isPresent())
        {
            var gson = new Gson();
            return gson.toJson(json.get());
        }

        return "{\"id\":\"minecraft:air\",\"Count\":1}";
    }

    public static boolean isAir(ItemStack stack)
    {
        return stack.getItemHolder().is(ResourceLocation.fromNamespaceAndPath("minecraft", "air"));
    }

    private static final ItemStack air = new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "air")));
}
