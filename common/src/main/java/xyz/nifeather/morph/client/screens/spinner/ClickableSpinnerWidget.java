package xyz.nifeather.morph.client.screens.spinner;

import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import xyz.nifeather.morph.client.graphics.Axes;
import xyz.nifeather.morph.client.graphics.DrawableSprite;
import xyz.nifeather.morph.client.graphics.container.Container;
import xyz.nifeather.morph.client.graphics.transforms.easings.Easing;

public class ClickableSpinnerWidget extends Container
{
    protected final DrawableSprite spriteBackground;
    protected final DrawableSprite spriteBorder;
    protected final DrawableSprite spriteHover;

    protected ResourceLocation getPathOf(String variant)
    {
        return ResourceLocation.fromNamespaceAndPath("morphclient", "spinner_default/" + variant);
    }

    protected long getFadeDuration()
    {
        return 300;
    }

    protected DrawableSprite createDrawableSprite(ResourceLocation textureIdentifier)
    {
        var drawableSprite = new DrawableSprite(textureIdentifier);

        drawableSprite.setRelativeSizeAxes(Axes.Both);

        return drawableSprite;
    }

    public ClickableSpinnerWidget()
    {
        spriteBackground = createDrawableSprite(getPathOf("background"));
        spriteBorder = createDrawableSprite(getPathOf("border"));
        spriteHover = createDrawableSprite(getPathOf("hover"));

        spriteHover.setAlpha(0);
        spriteHover.setDepth(-1);

        spriteBorder.setDepth(-100);

        spriteBackground.setDepth(10);

        this.add(spriteBackground);
        this.add(spriteBorder);
        this.add(spriteHover);
    }

    private Runnable onClick;

    public void onClick(Runnable runnable)
    {
        this.onClick = runnable;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl)
    {
        if (!hovered() && !isFocused()) return false;

        if (mouseButtonEvent.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT)
        {
            if (this.onClick != null)
                this.onClick.run();

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }

        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    protected void onHover()
    {
        super.onHover();

        spriteHover.fadeTo(0.5f, getFadeDuration(), Easing.OutQuint);
    }

    @Override
    protected void onHoverLost()
    {
        super.onHoverLost();

        spriteHover.fadeOut(getFadeDuration(), Easing.OutQuint);
    }
}
