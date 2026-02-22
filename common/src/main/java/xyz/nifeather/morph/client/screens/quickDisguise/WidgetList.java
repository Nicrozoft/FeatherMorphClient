package xyz.nifeather.morph.client.screens.quickDisguise;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.joml.Vector2f;
import xyz.nifeather.morph.client.graphics.IMDrawable;
import xyz.nifeather.morph.client.graphics.MarginPadding;
import xyz.nifeather.morph.client.storage.struct.SavedDisguise;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WidgetList extends ContainerObjectSelectionList<QuickDisguiseEntry> implements IMDrawable
{
    public WidgetList(Minecraft minecraft, int x, int y, int width, int widgetHeight)
    {
        super(minecraft, x, y, width, widgetHeight);
    }

    @Nullable
    public BiConsumer<@Nullable String, @Nullable SavedDisguise> onSelect;

    public int addEntry(String name, SavedDisguise savedDisguise)
    {
        var entry = new QuickDisguiseEntry(name, savedDisguise, this);
        var index = super.addEntry(entry);

        entry.initBackground();

        return index;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl)
    {
        var entry = getEntryAtPosition(mouseButtonEvent.x(), mouseButtonEvent.y());
        if (entry == null) return super.mouseClicked(mouseButtonEvent, bl);

        if (mouseButtonEvent.button() == 0)
            this.setSelected(entry);
        else if (mouseButtonEvent.button() == 1)
            this.setSelected(null);

        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public void setSelected(@org.jspecify.annotations.Nullable QuickDisguiseEntry entry)
    {
        var prevSelect = this.getSelected();
        if (prevSelect != null)
            prevSelect.setSelected(false);

        if (entry != null)
            entry.setSelected(true);

        if (onSelect != null)
        {
            if (entry == null)
                onSelect.accept(null, null);
            else
                onSelect.accept(entry.name(), entry.savedDisguise());
        }

        super.setSelected(entry);
    }

    @Override
    protected void renderListSeparators(GuiGraphics guiGraphics)
    {
    }

    @Override
    public void invalidatePosition()
    {
    }

    @Override
    public void invalidateLayout()
    {
    }

    @Override
    public int getRowWidth()
    {
        return Math.round(this.getWidth() * 0.7f);
    }

    @Override
    public void setWidth(float width)
    {
        this.setWidth(Math.round(width));
    }

    public void setWidth(int w)
    {
        super.setWidth(w);
        var rowWidth = this.getRowWidth();

        this.children().forEach(entry -> entry.setWidth(rowWidth));
    }

    @Override
    public void setHeight(float height)
    {
        this.setHeight(Math.round(height));
    }

    @Override
    public void setSize(@UnknownNullability Vector2f vector)
    {
        this.setWidth(vector.x());
        this.setHeight(vector.y());
    }

    @Override
    public float getRenderWidth()
    {
        return this.width;
    }

    @Override
    public float getRenderHeight()
    {
        return this.height;
    }

    @Override
    public @NotNull MarginPadding getPadding()
    {
        return new MarginPadding(0);
    }

    private IMDrawable parent;

    @Override
    public void setParent(@Nullable IMDrawable parent)
    {
        this.parent = parent;
    }

    @Override
    public @Nullable IMDrawable getParent()
    {
        return this.parent;
    }

    @Override
    public float getScreenSpaceX()
    {
        return getX();
    }

    @Override
    public float getScreenSpaceY()
    {
        return getY();
    }

    private int depth = 0;

    /**
     * Depth of this IMDrawable, higher value means this drawable should be rendered below others
     */
    @Override
    public int getDepth()
    {
        return this.depth;
    }

    @Override
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public void clear()
    {
        this.clearEntries();
    }
}
