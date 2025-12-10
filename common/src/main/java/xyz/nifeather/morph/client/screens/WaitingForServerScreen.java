package xyz.nifeather.morph.client.screens;

import me.shedaniel.math.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.graphics.Anchor;
import xyz.nifeather.morph.client.graphics.DrawableText;
import xyz.nifeather.morph.client.graphics.IMDrawable;
import xyz.nifeather.morph.client.graphics.LoadingSpinner;
import xyz.nifeather.morph.client.graphics.container.DrawableButtonWrapper;
import xyz.nifeather.morph.client.graphics.transforms.Transformer;
import xyz.nifeather.morph.client.graphics.transforms.easings.Easing;
import xyz.nifeather.morph.client.screens.disguise.DisguiseScreen;

import java.awt.*;

public class WaitingForServerScreen extends FeatherScreen
{
    @Nullable
    private final FeatherScreen nextScreen;

    public WaitingForServerScreen(@NotNull FeatherScreen next)
    {
        this(Component.empty(), next);
    }

    protected WaitingForServerScreen(Component title, @NotNull FeatherScreen next)
    {
        super(title);

        this.nextScreen = next;
        closeButton = this.createDrawableButtonWrapper(0, 0, 150, 20, Component.translatable("gui.back"), (button) ->
        {
            this.onClose();
        });
    }

    private final DrawableText notReadyText = new DrawableText(Component.translatable("gui.morphclient.waiting_for_server"));
    private final DrawableButtonWrapper closeButton;

    private final Bindable<Float> backgroundDim = new Bindable<>(0f);

    public float getCurrentDim()
    {
        return backgroundDim.get();
    }

    private final Bindable<Boolean> serverReady = new Bindable<>();

    private final LoadingSpinner loadingSpinner = new LoadingSpinner();

    @Override
    protected void onScreenEnter(Screen last)
    {
        super.onScreenEnter(last);

        var morphClient = FeatherMorphClientBootstrap.getInstance();
        this.serverReady.bindTo(morphClient.serverHandler.serverReady);

        if (serverReady.get())
        {
            this.push(nextScreen);
        }
        else
        {
            serverReady.onValueChanged((o, n) ->
            {
                FeatherMorphClientBootstrap.getInstance().schedule(() ->
                {
                    if (isCurrent() && n)
                        this.push(nextScreen);
                });
            }, true);

            this.addRange(new IMDrawable[]
                    {
                            notReadyText,
                            closeButton,
                            loadingSpinner
                    });

            loadingSpinner.setAnchor(Anchor.Centre);
            loadingSpinner.setY(40);
            notReadyText.setAnchor(Anchor.Centre);

            if (last instanceof DisguiseScreen disguiseScreen)
                backgroundDim.set(disguiseScreen.getBackgroundDim());

            Transformer.transform(backgroundDim, 0.3f, 300, Easing.OutQuint);
        }
    }

    @Override
    protected void onScreenResize()
    {
        loadingSpinner.invalidatePosition();
        notReadyText.invalidatePosition();
        super.onScreenResize();
    }

    @Override
    protected void onScreenExit(@Nullable Screen nextScreen)
    {
        serverReady.dispose();

        super.onScreenExit(nextScreen);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        var color = Color.ofRGBA(0, 0, 0, backgroundDim.get());
        context.fillGradient(0, 0, this.width, this.height, color.getColor(), color.getColor());

        //notReadyText.setScreenY(this.height / 2);
        //notReadyText.setScreenX((this.width -textRenderer.getWidth(notReadyText.getText()))  / 2);

        closeButton.setX(this.width / 2 - 75);
        closeButton.setY(this.height - 29);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderTransparentBackground(GuiGraphics context)
    {
    }

    @Override
    protected void renderMenuBackground(GuiGraphics context)
    {
    }

    @Override
    protected void renderMenuBackground(GuiGraphics context, int x, int y, int width, int height)
    {
    }
}