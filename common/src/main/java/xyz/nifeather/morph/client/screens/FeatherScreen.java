package xyz.nifeather.morph.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.graphics.IMDrawable;
import xyz.nifeather.morph.client.graphics.MButtonWidget;
import xyz.nifeather.morph.client.graphics.MarginPadding;
import xyz.nifeather.morph.client.graphics.container.DrawableButtonWrapper;
import xyz.nifeather.morph.client.graphics.transforms.Recorder;
import xyz.nifeather.morph.client.graphics.transforms.Transformer;
import xyz.nifeather.morph.client.graphics.transforms.easings.Easing;
import xyz.nifeather.morph.client.utilties.MathUtils;
import xyz.nifeather.morph.client.utilties.Screens;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.network.chat.Component;

public abstract class FeatherScreen extends Screen implements IMDrawable
{
    protected FeatherScreen(Component title) {
        super(title);
    }

    protected boolean isInitialInitialize = true;

    private Screen lastScreen;
    private Screen nextScreen;

    @Override
    public void setWidth(float width)
    {
        FeatherMorphClientBootstrap.LOGGER.warn("setWidth() for FeatherScreen is not implemented!!!");
    }

    @Override
    public void setHeight(float height)
    {
        FeatherMorphClientBootstrap.LOGGER.warn("setHeight() for FeatherScreen is not implemented!!!");
    }

    @Override
    public void setSize(UVPair vector)
    {
        FeatherMorphClientBootstrap.LOGGER.warn("setSize() for FeatherScreen is not implemented!!!");
    }

    @Override
    public float getRenderWidth()
    {
        return this.width;
    }

    @Override
    public float getRenderHeight()
    {
        return this.height;
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
        return 0;
    }

    @Override
    public float getScreenSpaceY()
    {
        return 0;
    }

    @Override
    public int getDepth()
    {
        return 0;
    }

    @Override
    public void setDepth(int depth)
    {
        FeatherMorphClientBootstrap.LOGGER.warn("setDepth() for FeatherScreen is not implemented!!!");
    }

    @Override
    protected void init()
    {
        var last = lastScreen;

        if (last != null && last == nextScreen)
            this.onScreenResume(last);
        else if (isInitialInitialize)
            this.onScreenEnter(last);

        lastScreen = null;
        nextScreen = null;

        this.mChildren().forEach(IMDrawable::invalidatePosition);

        if (isInitialInitialize)
        {
            this.onScreenResize();
            isInitialInitialize = false;
        }
        else
        {
            this.onScreenResize();

            clearWidgets();
            this.children.forEach(super::addRenderableWidget);
        }

        super.init();
    }

    @Override
    public void added()
    {
        lastScreen = Screens.getInstance().last;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        if (!layoutValid.get())
            this.rebuildWidgets();

        var shaderColor = RenderSystem.getShaderColor();
        shaderColor = new float[]
                {
                        shaderColor[0],
                        shaderColor[1],
                        shaderColor[2],
                        shaderColor[3]
                };

        RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], shaderColor[3] * this.alpha.get());

        super.render(context, mouseX, mouseY, delta);

        RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], shaderColor[3]);
    }

    @Override
    protected void rebuildWidgets()
    {
        super.rebuildWidgets();
        layoutValid.set(true);
    }

    @Override
    public void removed()
    {
        var next = Screens.getInstance().next;
        if (next == null)
            isInitialInitialize = true;

        nextScreen = next;
        this.onScreenExit(next);

        invalidateLayout();

        this.clearWidgets();
        super.removed();
    }

    //region ChildrenV2

    private final AtomicBoolean layoutValid = new AtomicBoolean(false);

    @Override
    public void invalidateLayout()
    {
        layoutValid.set(false);
    }

    protected boolean layoutValidate()
    {
        return layoutValid.get();
    }

    private final AtomicBoolean positionValid = new AtomicBoolean(false);

    @Override
    public void invalidatePosition() { positionValid.set(false); }

    public boolean positionValid()
    {
        return positionValid.get();
    }

    private final List<IMDrawable> children = new ObjectArrayList<>();

    protected List<IMDrawable> mChildren()
    {
        return new ObjectArrayList<>(children);
    }

    protected void add(IMDrawable drawable)
    {
        this.add(drawable, true);
    }

    private void add(IMDrawable drawable, boolean invalidateLayout)
    {
        if (this.contains(drawable)) return;

        children.add(drawable);

        if (invalidateLayout)
            invalidateLayout();
    }

    protected void addRange(IMDrawable[] drawables)
    {
        for (var drawable : drawables)
            this.add(drawable, false);

        invalidateLayout();
    }

    protected void addRange(List<IMDrawable> drawables)
    {
        drawables.forEach(this::add);
        invalidateLayout();
    }

    protected void remove(IMDrawable drawable)
    {
        children.remove(drawable);
        invalidateLayout();
    }

    protected boolean contains(IMDrawable drawable)
    {
        return children.contains(drawable);
    }

    //endregion

    //region Minecraft interface children handling

    private static class InvalidOperationException extends RuntimeException
    {
        public InvalidOperationException() {
        }

        public InvalidOperationException(String message) {
            super(message);
        }

        public InvalidOperationException(String message, Throwable cause) {
            super(message, cause);
        }

        public InvalidOperationException(Throwable cause) {
            super(cause);
        }
    }

    //endregion

    protected boolean isCurrent()
    {
        return Minecraft.getInstance().screen == this;
    }

    protected void onScreenResize()
    {
    }

    protected void onScreenEnter(@Nullable Screen lastScreen)
    {
    }

    protected void onScreenExit(@Nullable Screen nextScreen)
    {
    }

    protected void onScreenResume(@Nullable Screen lastScreen)
    {
    }

    protected DrawableButtonWrapper createDrawableWrapper(int x, int y, int width, int height, Component text, Button.OnPress action)
    {
        return new DrawableButtonWrapper(this.buildButtonWidget(x, y, width, height, text, action));
    }

    protected MButtonWidget buildButtonWidget(int x, int y, int width, int height, Component text, Button.OnPress action)
    {
        var builder = Button.builder(text, action);

        builder.bounds(x, y, width, height);

        return MButtonWidget.from(builder.build(), action);
    }

    protected void push(FeatherScreen screen)
    {
        Minecraft.getInstance().setScreen(screen);
    }

    //region Alpha

    protected final Recorder<Float> alpha = new Recorder<Float>(1f);

    public void setAlpha(float newVal)
    {
        this.alpha.set(newVal);
    }

    public void fadeTo(float newVal, long duration, Easing easing)
    {
        Transformer.transform(alpha, MathUtils.clamp(0f, 1f, newVal), duration, easing);
    }

    public void fadeIn(long duration, Easing easing)
    {
        this.fadeTo(1, duration, easing);
    }

    public void fadeOut(long duration, Easing easing)
    {
        this.fadeTo(0, duration, easing);
    }

    //endregion Alpha

    //region Narratable Selectable

    @Override
    public void updateNarration(NarrationElementOutput builder)
    {
    }

    @Override
    public NarrationPriority narrationPriority()
    {
        return NarrationPriority.HOVERED;
    }

    //endregion
}
