package xyz.nifeather.morph.client.graphics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.geom.builders.UVPair;

public class MGridWidget extends GridLayout implements IMDrawable
{
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
    }

    @Override
    public void setWidth(float width)
    {
        this.width = Math.round(width);
    }

    @Override
    public void setHeight(float height)
    {
        this.height = Math.round(height);
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
        return getWidth();
    }

    @Override
    public float getRenderHeight()
    {
        return getHeight();
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
        return parent;
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

    //region wtf

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        IMDrawable.super.mouseMoved(mouseX, mouseY);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
        return IMDrawable.super.nextFocusPath(navigation);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return IMDrawable.super.isMouseOver(mouseX, mouseY);
    }

    private boolean focused;

    @Override
    public void setFocused(boolean focused)
    {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath() {
        return IMDrawable.super.getCurrentFocusPath();
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.HOVERED;
    }

    @Override
    public boolean isActive() {
        return IMDrawable.super.isActive();
    }

    @Override
    public void updateNarration(NarrationElementOutput builder)
    {
    }

    @Override
    public int getTabOrderGroup() {
        return IMDrawable.super.getTabOrderGroup();
    }

    @Override
    public ScreenRectangle getRectangle() {
        return super.getRectangle();
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);
    }

    @Override
    public void invalidatePosition()
    {
    }

    @Override
    public void invalidateLayout()
    {
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

    //endregion
}
