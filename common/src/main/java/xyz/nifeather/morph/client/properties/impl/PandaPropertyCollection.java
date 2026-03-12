package xyz.nifeather.morph.client.properties.impl;

import net.minecraft.world.entity.animal.panda.Panda;
import xyz.nifeather.morph.client.properties.ClientProperty;
import xyz.nifeather.morph.client.properties.PropertyNames;

import java.util.Arrays;
import java.util.Optional;

public class PandaPropertyCollection extends EntityPropertyCollection<Panda>
{
    public final ClientProperty<Panda.Gene, Panda> MAIN_GENE =
            ClientProperty.builder(PropertyNames.PANDA_MAIN_GENE, Panda.Gene.NORMAL, Panda.class)
                    .inputHandle(this::readGene)
                    .entityHandle(Panda::setMainGene)
                    .build();

    public final ClientProperty<Panda.Gene, Panda> HIDDEN_GENE =
            ClientProperty.builder(PropertyNames.PANDA_HIDDEN_GENE, Panda.Gene.NORMAL, Panda.class)
                    .inputHandle(this::readGene)
                    .entityHandle(Panda::setHiddenGene)
                    .build();

    private Optional<Panda.Gene> readGene(String string)
    {
        return Arrays.stream(Panda.Gene.values())
                .filter(g -> g.name().equalsIgnoreCase(string))
                .findFirst();
    }

    public PandaPropertyCollection()
    {
        register(MAIN_GENE, HIDDEN_GENE);
    }
}
