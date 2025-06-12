package xyz.nifeather.morph.client.graphics;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class DrawableSprite extends MDrawable
{
    private final ResourceLocation textureIdentifier;
    private final boolean isGuiTexture;

    public DrawableSprite(ResourceLocation textureIdentifier, boolean isGuiTexture)
    {
        this.textureIdentifier = textureIdentifier;
        this.isGuiTexture = isGuiTexture;
    }

    public DrawableSprite(ResourceLocation textureIdentifier)
    {
        this(textureIdentifier, true);
    }

    @Override
    protected void onRender(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        int texWidth = Math.round(this.getRenderWidth());
        int texHeight = Math.round(this.getRenderHeight());

        if (isGuiTexture)
        {
            context.blitSprite(RenderType::guiTextured, textureIdentifier,
                    0, 0,
                    texWidth, texHeight);
        }
        else
        {
            context.blit(RenderType::guiTextured, textureIdentifier,
                    0, 0,
                    0, 0,
                    texWidth, texHeight,
                    texWidth, texHeight);
        }
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigation)
    {
        return null;
    }
}
