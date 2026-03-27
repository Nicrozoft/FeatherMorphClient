package xyz.nifeather.morph.client.graphics.container;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import xyz.nifeather.morph.client.graphics.MDrawable;
import xyz.nifeather.morph.client.graphics.color.MaterialColors;

public class DrawableButtonWrapper extends MDrawable
{
    private final Button widget;

    public DrawableButtonWrapper(Button widget)
    {
        this.setHeight(widget.getHeight());
        this.setWidth(widget.getWidth());
        this.widget = widget;
    }

    @Override
    protected void updatePosition()
    {
        super.updatePosition();

        widget.setX(Math.round(this.getScreenSpaceX()));
        widget.setY(Math.round(this.getScreenSpaceY()));
    }

    @Override
    protected void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
    {
        context.pose().translate(-this.getScreenSpaceX(), -this.getScreenSpaceY(), context.pose());
        widget.extractRenderState(context, mouseX, mouseY, delta);

        super.onRender(context, mouseX, mouseY, delta);
    }

    @Override
    public void setFocused(boolean focused)
    {
        widget.setFocused(focused);
    }

    @Override
    public boolean isFocused()
    {
        return widget.isFocused();
    }

    @Override
    public boolean keyReleased(KeyEvent event)
    {
        return widget.keyReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        return widget.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl)
    {
        return widget.mouseClicked(mouseButtonEvent, bl);
    }
}
