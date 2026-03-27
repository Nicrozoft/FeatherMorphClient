package xyz.nifeather.morph.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import xyz.nifeather.morph.client.graphics.color.Colors;

public class Box extends MDrawable
{
    public int color = Colors.WHITE.getColor();

    @Override
    protected void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
    {
        super.onRender(context, mouseX, mouseY, delta);

        context.fill(0, 0,
                renderWidth, renderHeight,
                color);
    }
}