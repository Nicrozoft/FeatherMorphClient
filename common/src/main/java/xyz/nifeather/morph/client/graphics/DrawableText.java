package xyz.nifeather.morph.client.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.graphics.color.ColorUtils;
import xyz.nifeather.morph.client.graphics.color.Colors;
import xyz.nifeather.morph.client.graphics.color.MaterialColors;

import java.util.List;

public class DrawableText extends MDrawable
{
    private static final Component defaultText = Component.literal("");
    private static final Font renderer = Minecraft.getInstance().font;

    private Component text = defaultText;

    public void setText(Component text)
    {
        this.text = text;
        updateTextWidth();
    }

    public void setText(String text)
    {
        this.text = Component.literal(text);
        updateTextWidth();
    }

    private void updateTextWidth()
    {
        if (RenderSystem.isOnRenderThread())
            this.setWidth(renderer.width(text));
        else
            this.addSchedule(() -> this.setWidth(renderer.width(text)));
    }

    public Component getText()
    {
        return text;
    }

    public DrawableText(String text)
    {
        this();
        this.setText(text);
    }

    public DrawableText(Component text)
    {
        this();
        this.setText(text);
    }

    public DrawableText()
    {
        this.setHeight(renderer.lineHeight);
    }

    private int color = 0xffffffff;

    public void setColor(int c)
    {
        this.color = c;
    }

    public int getColor()
    {
        return color;
    }

    private boolean drawShadow = false;

    public boolean drawShadow()
    {
        return drawShadow;
    }

    public void setDrawShadow(boolean val)
    {
        this.drawShadow = val;
    }

    @Nullable
    private Component tooltip;

    public void setTooltip(@Nullable Component tooltip)
    {
        this.tooltip = tooltip;
    }

    @Nullable
    public Component getTooltip()
    {
        return tooltip;
    }

    @Override
    public void onRender(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        context.drawString(renderer, text, 0, 0, color, drawShadow);

        if (hovered() && getTooltip() != null)
            context.setTooltipForNextFrame(Minecraft.getInstance().font, getTooltip(), 0, 0);
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigation)
    {
        return null;
    }
}
