package xyz.nifeather.morph.client.screens.disguise;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.Identifier;

public class EntityDisplayEntry extends ContainerObjectSelectionList.Entry<EntityDisplayEntry> implements Comparable<EntityDisplayEntry>
{
    private DisplayWidget displayWidget;

    private String identifier = "???";

    public String getEntityName()
    {
        return displayWidget.entityName() == null ? "???" : displayWidget.entityName();
    }

    public String getIdentifierString()
    {
        return identifier;
    }

    private Identifier identifierAsNms;

    private static final Identifier defaultIdentifier = Identifier.fromNamespaceAndPath("morph", "unknown");

    private int parentWidth = 0;
    private int expectedWidth = 0;

    public void updateParentAllowedScreenSpaceWidth(int allowedWidth)
    {
        this.parentWidth = allowedWidth;
        this.expectedWidth = parentWidth;

        this.children.forEach(displayWidget -> displayWidget.setWidth(expectedWidth));
    }

    public Identifier getIdentifier()
    {
        if (identifierAsNms != null)
            return identifierAsNms;

        var id = Identifier.tryParse(identifier.toLowerCase());
        if (id == null)
            id = defaultIdentifier;

        this.identifierAsNms = id;
        return id;
    }

    private final List<DisplayWidget> children = new ObjectArrayList<>();

    @Override
    public List<? extends NarratableEntry> narratables()
    {
        return children;
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        return children;
    }

    public EntityDisplayEntry(String name)
    {
        initFields(name);
    }

    public void clearChildren()
    {
        children.forEach(DisplayWidget::dispose);
        children.clear();
    }

    private void initFields(String name)
    {
        this.identifier = name;
        displayWidget = new DisplayWidget(0, 0, 180, 20, name);

        if (expectedWidth > 0)
            displayWidget.setWidth(expectedWidth);

        children.add(displayWidget);
    }

    // i -> mouseX
    // j -> mouseY
    // f -> timeDelta
    // bl -> hovered
    @Override
    public void extractContent(GuiGraphicsExtractor guiGraphics, int i, int j, boolean bl, float f)
    {
        displayWidget.screenSpaceX = this.getContentX();
        displayWidget.screenSpaceY = this.getContentY();

        displayWidget.extractRenderState(guiGraphics, i, j, f);
    }

    @Override
    public int compareTo(@NotNull EntityDisplayEntry entityDisplayEntry)
    {
        return identifier.compareTo(entityDisplayEntry.identifier);
    }
}
