package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Panda;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Arrays;
import java.util.Optional;

public class PandaPropertyHandler extends EntityPropertyHandler<Panda>
{
    public final ClientProperty<Panda.Gene> MAIN_GENE = ClientProperty.of(PropertyNames.PANDA_MAIN_GENE, this::readGene);
    public final ClientProperty<Panda.Gene> HIDDEN_GENE = ClientProperty.of(PropertyNames.PANDA_HIDDEN_GENE, this::readGene);

    private Optional<Panda.Gene> readGene(String string)
    {
        return Arrays.stream(Panda.Gene.values())
                .filter(g -> g.name().equalsIgnoreCase(string))
                .findFirst();
    }

    public PandaPropertyHandler()
    {
        register(MAIN_GENE, HIDDEN_GENE);
    }

    @Override
    public Optional<Panda> tryCast(Entity entity)
    {
        return Optional.ofNullable(entity instanceof Panda panda ? panda : null);
    }

    @Override
    protected <X> void applyToEntity(Panda entity, ClientProperty<X> property, X value)
    {
        super.applyToEntity(entity, property, value);

        switch (property.identifier())
        {
            case PropertyNames.PANDA_MAIN_GENE -> entity.setMainGene((Panda.Gene) value);
            case PropertyNames.PANDA_HIDDEN_GENE -> entity.setHiddenGene((Panda.Gene) value);
        }
    }
}
