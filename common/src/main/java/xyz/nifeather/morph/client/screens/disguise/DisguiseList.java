package xyz.nifeather.morph.client.screens.disguise;

import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.config.ModConfigData;
import xyz.nifeather.morph.client.graphics.IMDrawable;
import xyz.nifeather.morph.client.graphics.MarginPadding;
import xyz.nifeather.morph.client.graphics.transforms.Recorder;
import xyz.nifeather.morph.client.graphics.transforms.Transformer;
import xyz.nifeather.morph.client.graphics.transforms.easings.Easing;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.Mth;
import xyz.nifeather.morph.client.mixin.accessors.AbstractScrollAreaAccessor;

public class DisguiseList extends ContainerObjectSelectionList<EntityDisplayEntry> implements IMDrawable
{
    private static final Logger log = LoggerFactory.getLogger(DisguiseList.class);
    private final ModConfigData modConfig;

    public DisguiseList(Minecraft minecraftClient, int width, int height, int topPadding, int bottomPadding, int itemHeight)
    {
        super(minecraftClient, width, height, 0, itemHeight);
        modConfig = FeatherMorphClientBootstrap.getInstance().getModConfigData();
    }

    public void addChild(EntityDisplayEntry entry)
    {
        entry.updateParentAllowedScreenSpaceWidth(this.getRowWidth());
        this.children().add(entry);
    }

    public void addChildrenRange(List<EntityDisplayEntry> entry)
    {
        entry.forEach(this::addChild);
    }

    public void clearChildren()
    {
        this.clearChildren(true);
    }

    public void clearChildren(boolean disposeChildren)
    {
        if (disposeChildren)
            children().forEach(EntityDisplayEntry::clearChildren);

        clearEntries();
    }

    @Override
    public void setFocused(boolean focused)
    {
        super.setFocused(focused);
    }

    public void setHeight(int nH)
    {
        this.height = nH;
    }

    public void setWidth(int w)
    {
        this.width = w;
        var rowWidth = this.getRowWidth();

        this.children().forEach(entry -> entry.updateParentAllowedScreenSpaceWidth(rowWidth));
    }

    public void scrollTo(EntityDisplayEntry widget)
    {
        if (widget == null || !children().contains(widget)) return;

        var amount = children().indexOf(widget) * itemHeight - itemHeight * 4;
        var maxScroll = this.maxScrollAmount();
        if (amount > maxScroll) amount = maxScroll;

        this.setScrollAmount(amount);
    }

    private boolean mouseDown;

    public void setMouseDown(boolean val)
    {
        mouseDown = val;
    }

    private double lastMouseX;
    private double lastMouseY;

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        super.mouseMoved(mouseX, mouseY);

        if (mouseDown && modConfig.disguiseListMouseDragging)
        {
            double xDiff = mouseX - lastMouseX;
            double yDiff = mouseY - lastMouseY;

            // Accesswindener doesn't work for somehow
            ((AbstractScrollAreaAccessor) this).setScrolling(true);
            super.mouseDragged(getX() + 3, getY() + 3, 1, xDiff, -yDiff);
            ((AbstractScrollAreaAccessor) this).setScrolling(false);
        }

        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        verticalAmount *= 3f * FeatherMorphClientBootstrap.getInstance().getModConfigData().scrollSpeed;
        horizontalAmount *= 3f * FeatherMorphClientBootstrap.getInstance().getModConfigData().scrollSpeed;

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    protected int scrollBarY()
    {
        return Math.max(this.getY(), Math.round((float)this.scrollAmount() * (this.height - this.scrollerHeight()) / this.maxScrollAmount() + this.getY()));
        //return super.getScrollbarThumbY();
    }

    @Override
    protected void renderListSeparators(GuiGraphics context)
    {
    }

    //private double diff;

    @Override
    public int getRowWidth()
    {
        return Math.round(this.getWidth() * 0.7f);
    }

    public void setHeaderHeight(int newHeaderHeight)
    {
        this.headerHeight = newHeaderHeight;
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
    public void setWidth(float width)
    {
        this.setWidth(Math.round(width));
    }

    @Override
    public void setHeight(float height)
    {
        this.setHeight(Math.round(height));
    }

    @Override
    public void setSize(UVPair vector)
    {
        this.setWidth(vector.u());
        this.setHeight(vector.v());
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
}
