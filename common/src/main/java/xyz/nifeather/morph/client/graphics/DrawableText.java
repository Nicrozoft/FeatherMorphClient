package xyz.nifeather.morph.client.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;
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

    private int backgroundColor = 0x00000000;

    public void setBackgroundColor(Color color)
    {
        setBackgroundColor(color.getColor());
    }

    public void setBackgroundColor(int color)
    {
        this.backgroundColor = color;
    }

    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    private int shadowExpandPixels = 0;

    public void setShadowExpandPixels(int pixels)
    {
        this.shadowExpandPixels = pixels;
    }

    public int getShadowExpandPixels()
    {
        return shadowExpandPixels;
    }

    @Override
    public void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
    {
        if (backgroundColor != 0)
            context.fill(-shadowExpandPixels, -shadowExpandPixels, shadowExpandPixels + renderer.width(text), shadowExpandPixels + renderer.lineHeight, backgroundColor);

        context.text(renderer, text, 0, 0, color, drawShadow);

        if (hovered() && getTooltip() != null)
            context.setTooltipForNextFrame(Minecraft.getInstance().font, getTooltip(), 0, 0);
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigation)
    {
        return null;
    }
}
