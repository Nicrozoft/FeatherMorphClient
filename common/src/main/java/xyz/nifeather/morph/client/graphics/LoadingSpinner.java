package xyz.nifeather.morph.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class LoadingSpinner extends MDrawable
{
    private static final Identifier LOADING_TEX = Identifier.fromNamespaceAndPath("morphclient", "loading");

    public LoadingSpinner()
    {
        this.setWidth(16);
        this.setHeight(16);
    }

    @Override
    protected void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
    {
        //RenderSystem.enableBlend();

        context.blitSprite(RenderPipelines.GUI_TEXTURED, LOADING_TEX, 0, 0, this.renderWidth, this.renderHeight);
        super.onRender(context, mouseX, mouseY, delta);
    }
}
