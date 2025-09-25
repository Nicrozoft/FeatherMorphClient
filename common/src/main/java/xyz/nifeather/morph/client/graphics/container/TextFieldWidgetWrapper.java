package xyz.nifeather.morph.client.graphics.container;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.graphics.MDrawable;
import xyz.nifeather.morph.client.graphics.color.MaterialColors;

import java.util.function.Consumer;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public class TextFieldWidgetWrapper extends MDrawable
{
    public EditBox widget;

    public TextFieldWidgetWrapper(EditBox fieldWidget)
    {
        this.widget = fieldWidget;

        this.setHeight(fieldWidget.getHeight());
        this.setWidth(fieldWidget.getWidth());
    }

    public EditBox widget()
    {
        return widget;
    }

    public void setChangedListener(Consumer<String> changedListener)
    {
        widget.setResponder(changedListener);
    }

    @Override
    protected void updatePosition()
    {
        super.updatePosition();

        widget.setX(Math.round(this.getScreenSpaceX()));
        widget.setY(Math.round(this.getScreenSpaceY()));
    }

    @Override
    protected void onRender(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        context.pose().translate(-this.getScreenSpaceX(), -this.getScreenSpaceY(), context.pose());
        widget.render(context, mouseX, mouseY, delta);

        super.onRender(context, mouseX, mouseY, delta);
    }

    @Override
    public void setWidth(float w)
    {
        super.setWidth(w);

        widget.setWidth(Math.round(w));
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigation)
    {
        return widget.nextFocusPath(navigation);
    }

    @Override
    public ScreenRectangle getRectangle()
    {
        return widget.getRectangle();
    }

    @Override
    public @Nullable ComponentPath getCurrentFocusPath()
    {
        return widget.getCurrentFocusPath();
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
    public void setHeight(float h)
    {
        super.setHeight(h);

        widget.setHeight(Math.round(h));
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        return widget.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event)
    {
        return widget.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent)
    {
        return widget.charTyped(characterEvent);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl)
    {
        return widget.mouseClicked(mouseButtonEvent, bl);
    }
}
