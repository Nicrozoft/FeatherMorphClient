package xyz.nifeather.morph.client.properties;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import xyz.nifeather.morph.client.properties.impl.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PropertyHandlers
{
    public static void init()
    {
        INSTANCE.bruh();
    }

    public static final PropertyHandlers INSTANCE = new PropertyHandlers();

    private void bruh()
    {
        // yes this is empty
    }

    private final Map<EntityType<?>, AbstractPropertyHandler<?>> handlerMap = new ConcurrentHashMap<>();

    private final FallbackPropertyHandler fallbackPropertyHandler = new FallbackPropertyHandler();

    public PropertyHandlers()
    {
        register(EntityType.PLAYER, new PlayerPropertyHandler());
        register(EntityType.ARMOR_STAND, new ArmorStandPropertyHandler());
        register(EntityType.AXOLOTL, new AxolotlPropertyHandler());
        register(EntityType.CAT, new CatPropertyHandler());
        register(EntityType.CHICKEN, new ChickenPropertyHandler());
        register(EntityType.COW, new CowPropertyHandler());
        register(EntityType.CREEPER, new CreeperPropertyHandler());
        register(EntityType.ENDER_DRAGON, new EnderDragonPropertyHandler());
        register(EntityType.FOX, new FoxPropertyHandler());
        register(EntityType.FROG, new FrogPropertyHandler());
        register(EntityType.GOAT, new GoatPropertyHandler());
        register(EntityType.HAPPY_GHAST, new HappyGhastPropertyHandler());

        register(EntityType.HOGLIN, new HoglinPropertyHandler());
        register(EntityType.ZOGLIN, new ZoglinPropertyHandler());

        register(EntityType.HORSE, new HorsePropertyHandler());

        register(EntityType.LLAMA, new LlamaPropertyHandler());
        register(EntityType.MOOSHROOM, new MooshroomPropertyHandler());

        register(EntityType.PANDA, new PandaPropertyHandler());
        register(EntityType.PARROT, new ParrotPropertyHandler());
        register(EntityType.PHANTOM, new PhantomPropertyHandler());

        register(EntityType.PIG, new PigPropertyHandler());
        register(EntityType.RABBIT, new RabbitPropertyHandler());

        register(EntityType.SHEEP, new SheepPropertyHandler());
        register(EntityType.SHULKER, new ShulkerPropertyHandler());

        register(EntityType.SLIME, new SlimePropertyHandler());
        register(EntityType.MAGMA_CUBE, new MagmaPropertyHandler());

        register(EntityType.SNOW_GOLEM, new SnowGolemPropertyHandler());

        register(EntityType.TRADER_LLAMA, new TraderLlamaPropertyHandler());
        register(EntityType.TROPICAL_FISH, new TropicalFishPropertyHandler());

        register(EntityType.VILLAGER, new VillagerPropertyHandler());
        register(EntityType.WOLF, new WolfPropertyHandler());

        register(EntityType.ZOMBIE, new ZombiePropertyhandler());
        register(EntityType.ZOMBIE_VILLAGER, new ZombieVillagerPropertyhandler());

        register(EntityType.GUARDIAN, new GuardianPropertyHandler());

        register(EntityType.MANNEQUIN, new MannequinPropertyHandler());
        register(EntityType.COPPER_GOLEM, new CopperGolemPropertyHandler());
    }

    public FallbackPropertyHandler fallbackHandler()
    {
        return fallbackPropertyHandler;
    }

    public <E extends Entity> Optional<AbstractPropertyHandler<E>> getHandler(E entity)
    {
        var match = handlerMap.getOrDefault(entity.getType(), null);

        return Optional.of((AbstractPropertyHandler<E>) Objects.requireNonNullElse(match, fallbackPropertyHandler));
    }

    public <E extends Entity> void register(EntityType<E> entityType, AbstractPropertyHandler<E> handler)
    {
        handlerMap.put(entityType, handler);
    }
}
