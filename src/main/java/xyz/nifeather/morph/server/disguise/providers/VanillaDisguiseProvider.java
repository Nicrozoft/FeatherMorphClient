package xyz.nifeather.morph.server.disguise.providers;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.server.MorphServerLoader;
import xyz.nifeather.morph.server.morphs.FabricDisguiseSession;
import xyz.nifeather.morph.server.disguise.animations.provider.VanillaAnimationProvider;

import java.util.List;

public class VanillaDisguiseProvider extends AbstractDisguiseProvider
{
    @Override
    public String namespace()
    {
        return "minecraft";
    }

    private final List<String> availableIdentifiers;

    @Override
    public boolean isValid(String identifier)
    {
        var asIdentifier = Identifier.tryParse(identifier);
        if (asIdentifier == null)
            return false;

        return availableIdentifiers.contains(asIdentifier.getPath());
    }

    public VanillaDisguiseProvider()
    {
        var server = MorphServerLoader.mcserver;
        assert server != null;

        var entityTypes = server.getRegistryManager().getOptional(Registries.ENTITY_TYPE.getKey());

        logger.info("Loading vanilla disguise identifiers...");

        var spawnPos = new BlockPos(0, 2048, 0);

        var list = new ObjectArrayList<String>();
        entityTypes.orElseThrow().forEach(eT ->
        {
            var entity = eT.spawn(server.getOverworld(), spawnPos, SpawnReason.COMMAND);

            //logger.info("Filter " + eT + " --> " + entity);

            if (!(entity instanceof LivingEntity))
            {
                if (entity != null)
                    entity.discard();

                return;
            }

            entity.discard();

            list.add(EntityType.getId(eT).getPath());
        });

        availableIdentifiers = new ObjectImmutableList<>(list);

        logger.info("Loaded %s vanilla disguise identifiers".formatted(list.size()));
    }

    @Override
    public List<String> availableDisguises()
    {
        return availableIdentifiers;
    }

    @Override
    public boolean disguise(PlayerEntity player, String disguiseIdentifier)
    {
        return false;
    }

    @Override
    public void onPostConstructDisguise(FabricDisguiseSession state, @Nullable Entity targetEntity)
    {
    }

    @Override
    public boolean unDisguise(PlayerEntity player)
    {
        return true;
    }

    @Override
    public boolean updateDisguise(PlayerEntity player, FabricDisguiseSession disguiseSession)
    {
        return true;
    }

    @Override
    public void onDisguiseApplied(FabricDisguiseSession disguiseSession)
    {
    }

    @Override
    public Text getDisplayName(String disguiseIdentifier)
    {
        var entityType = EntityType.get(disguiseIdentifier).orElse(null);
        if (entityType == null)
            return Text.of("???(%s)".formatted(disguiseIdentifier));

        return Text.translatable(entityType.getTranslationKey());
    }

    private final VanillaAnimationProvider animationProvider = new VanillaAnimationProvider();

    @Override
    public VanillaAnimationProvider getAnimationProvider()
    {
        return animationProvider;
    }
}
