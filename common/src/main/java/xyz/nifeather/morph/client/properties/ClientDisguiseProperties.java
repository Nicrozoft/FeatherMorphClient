package xyz.nifeather.morph.client.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import xyz.nifeather.morph.client.properties.impl.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ClientDisguiseProperties
{
    public static final ClientDisguiseProperties INSTANCE = new ClientDisguiseProperties();

    private final Map<EntityType<?>, AbstractPropertyCollection> handlerMap = new ConcurrentHashMap<>();

    public void reset()
    {
        handlerMap.clear();
    }
    public void init()
    {
        if (Minecraft.getInstance().level == null)
            throw new RuntimeException("Client level is not available, cannot initialize disguise properties!");

        reset();

        register(EntityType.PLAYER, new PlayerPropertyCollection());
        register(EntityType.ARMOR_STAND, new ArmorStandPropertyCollection());
        register(EntityType.AXOLOTL, new AxolotlPropertyCollection());
        register(EntityType.CAT, new CatPropertyCollection());
        register(EntityType.CHICKEN, new ChickenPropertyCollection());
        register(EntityType.COW, new CowPropertyCollection());
        register(EntityType.CREEPER, new CreeperPropertyCollection());
        register(EntityType.ENDER_DRAGON, new EnderDragonPropertyCollection());
        register(EntityType.FOX, new FoxPropertyCollection());
        register(EntityType.FROG, new FrogPropertyCollection());
        register(EntityType.GOAT, new GoatPropertyCollection());
        register(EntityType.HAPPY_GHAST, new HappyGhastPropertyCollection());

        register(EntityType.HOGLIN, new HoglinPropertyCollection());
        register(EntityType.ZOGLIN, new ZoglinPropertyCollection());

        register(EntityType.HORSE, new HorsePropertyCollection());

        register(EntityType.LLAMA, new LlamaPropertyCollection());
        register(EntityType.MOOSHROOM, new MooshroomPropertyCollection());

        register(EntityType.PANDA, new PandaPropertyCollection());
        register(EntityType.PARROT, new ParrotPropertyCollection());
        register(EntityType.PHANTOM, new PhantomPropertyCollection());

        register(EntityType.PIG, new PigPropertyCollection());
        register(EntityType.RABBIT, new RabbitPropertyCollection());

        register(EntityType.SHEEP, new SheepPropertyCollection());
        register(EntityType.SHULKER, new ShulkerPropertyCollection());

        register(EntityType.SLIME, new SlimePropertyCollection());
        register(EntityType.MAGMA_CUBE, new MagmaPropertyCollection());

        register(EntityType.SNOW_GOLEM, new SnowGolemPropertyCollection());

        register(EntityType.TRADER_LLAMA, new TraderLlamaPropertyCollection());
        register(EntityType.TROPICAL_FISH, new TropicalFishPropertyCollection());

        register(EntityType.VILLAGER, new VillagerPropertyCollection());
        register(EntityType.WOLF, new WolfPropertyCollection());

        register(EntityType.ZOMBIE, new ZombiePropertyhandler());
        register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerPropertyhandler());

        register(EntityType.GUARDIAN, new GuardianPropertyCollection());

        register(EntityType.MANNEQUIN, new MannequinPropertyCollection());
        register(EntityType.COPPER_GOLEM, new CopperGolemPropertyCollection());

        register(EntityType.ITEM_DISPLAY, new ItemDisplayPropertyCollection());
    }

    private final FallbackPropertyCollection fallbackPropertyHandler = new FallbackPropertyCollection();

    public FallbackPropertyCollection fallbackHandler()
    {
        return fallbackPropertyHandler;
    }

    public <E extends Entity> Optional<AbstractPropertyCollection> getHandler(E entity)
    {
        var match = handlerMap.getOrDefault(entity.getType(), null);

        return Optional.of((AbstractPropertyCollection) Objects.requireNonNullElse(match, fallbackPropertyHandler));
    }

    public <E extends Entity> void register(EntityType<E> entityType, AbstractPropertyCollection handler)
    {
        handlerMap.put(entityType, handler);
    }
}
