package xyz.nifeather.morph.client.graphics.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.FeatherMorphClient;
import xyz.nifeather.morph.client.MorphClientObject;
import xyz.nifeather.morph.client.graphics.color.ColorUtils;
import xyz.nifeather.morph.client.graphics.transforms.Recorder;
import xyz.nifeather.morph.client.graphics.transforms.Transformer;
import xyz.nifeather.morph.client.graphics.transforms.easings.Easing;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;

import java.util.concurrent.atomic.AtomicBoolean;

public class LinedToast extends MorphClientObject implements Toast
{
    public LinedToast()
    {
    }

    private final Recorder<Integer> outlineWidth = Recorder.of(0);

    protected final Bindable<Visibility> visibility = new Bindable<Toast.Visibility>(Visibility.HIDE);

    protected boolean fadeInOnEnter()
    {
        return false;
    }

    @Initializer
    private void load()
    {
        outlineWidth.set(this.getWidth());

        this.visibility.onValueChanged((o, visible) ->
        {
            if (visible == Visibility.SHOW)
                Transformer.delay(300).then(() ->
                {
                    Transformer.transform(outlineWidth, 2, 600, Easing.OutQuint);
                });
            else
                Transformer.transform(outlineWidth, this.getWidth(), 600, Easing.OutQuad);
        }, true);
    }

    private final AtomicBoolean layoutValid = new AtomicBoolean(false);

    protected void invalidateLayout()
    {
        layoutValid.set(false);
    }

    protected void updateLayout()
    {
        if (title != null)
        {
            Text titleDisplay = Text.literal(textRenderer.trimToWidth(title, this.getTextWidth()).getString());

            if (!titleDisplay.getString().equalsIgnoreCase(title.getString()))
                titleDisplay = Text.of(titleDisplay.getString() + "...");

            this.titleDisplay = titleDisplay;
        }
        else
            this.titleDisplay = Text.literal("Null title");

        if (description != null)
        {
            Text descDisplay = Text.literal(textRenderer.trimToWidth(description, this.getTextWidth()).getString());

            if (!descDisplay.getString().equalsIgnoreCase(description.getString()))
                descDisplay = Text.of(descDisplay.getString() + "...");

            this.descDisplay = descDisplay;
        }
        else
            this.descDisplay = Text.literal("");

        layoutValid.set(true);
    }

    public void setTitle(Text text)
    {
        this.title = text;
        this.invalidateLayout();
    }

    @Nullable
    public Text getTitle()
    {
        return title;
    }

    public void setDescription(Text text)
    {
        this.description = text;
        this.invalidateLayout();
    }

    @Nullable
    public Text getDescription()
    {
        return description;
    }

    private static final Text defaultText = Text.empty();

    private Text title;
    private Text description;
    private Text titleDisplay = defaultText;
    private Text descDisplay = defaultText;
    private Color lineColor = Color.ofRGB(255, 255, 255);

    @NotNull
    public Color getLineColor()
    {
        return lineColor;
    }

    public void setLineColor(@Nullable Color newColor)
    {
        if (newColor == null) newColor = Color.ofRGB(255, 255, 255);
        this.lineColor = newColor;
    }

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    protected void postTextDrawing(DrawContext context, long startTime)
    {
    }

    protected void postBackgroundDrawing(DrawContext context, long startTime)
    {
    }

    protected void postDraw(DrawContext context, long startTime)
    {
    }

    protected boolean drawProgress()
    {
        return FeatherMorphClient.getInstance().getModConfigData().displayToastProgress;
    }

    protected float getTextStartX()
    {
        return this.getWidth() * 0.25F - 4;
    }

    protected int getTextWidth()
    {
        return (int) (this.getWidth() * 0.65F);
    }

    @Override
    public Visibility getVisibility()
    {
        return this.visibility.get();
    }

    /**
     * Range: 0 ~ 1
     */
    private double progress = 0d;

    @Override
    public void update(ToastManager manager, long startTime)
    {
        this.progress = Math.min(1, startTime / (5000.0 * manager.getNotificationDisplayTimeMultiplier()));

        // Update visibility
        var visibility = this.progress >= 1 ? Visibility.HIDE : Visibility.SHOW;
        this.visibility.set(visibility);
    }

    private final Color progressColor = ColorUtils.fromHex("666666");
    private final Color borderColor = ColorUtils.fromHex("#444444");

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime)
    {
        if (!layoutValid.get())
            updateLayout();

        var xRightPadding = 1;
        var xLeftPadding = 2;
        var yPadding = 1;

        context.enableScissor(xRightPadding, 0, getWidth(), getHeight());

        // Draw background
        context.fill(xRightPadding, yPadding,
                this.getWidth() - xLeftPadding, this.getHeight() - yPadding, 0xFF333333);

        var matrices = context.getMatrices();

        // Draw progress bar
        if (drawProgress())
        {
            var progressDisplay = Math.max(0, 0.95 - progress);

            matrices.push();

            var translateX = (float)this.getWidth() * (1 - progressDisplay);
            matrices.translate(-translateX, 0, 0);

            context.fill(xRightPadding, yPadding,
                    this.getWidth(),
                    this.getHeight() - yPadding,
                    ColorUtils.forOpacity(progressColor, (float)progressDisplay).getColor());

            matrices.pop();
        }

        postBackgroundDrawing(context, startTime);

        // Draw text
        var textStartX = (int)getTextStartX();
        var textStartY = Math.round((this.getHeight()) / 2f) - textRenderer.fontHeight + yPadding;

        context.drawTextWithShadow(textRenderer, titleDisplay, textStartX, textStartY - 1, 0xffffffff);
        context.drawTextWithShadow(textRenderer, descDisplay, textStartX, textStartY + textRenderer.fontHeight + 1, 0xffffffff);

        postTextDrawing(context, startTime);

        // Draw CoverLine
        matrices.push();
        matrices.translate(0, 0, 128);

        var lineWidth = Math.min(outlineWidth.get(), this.getWidth() - xRightPadding);

        context.fill(xRightPadding + 1, yPadding + 1,
                lineWidth, this.getHeight() - yPadding - 1,
                lineColor.getColor());

        context.drawBorder(xRightPadding + 1, yPadding + 1,
                this.getWidth() - xLeftPadding - 2, this.getHeight() - yPadding - 2,
                lineColor.getColor());

        context.drawBorder(xRightPadding, yPadding,
                this.getWidth() - xLeftPadding, this.getHeight() - yPadding,
                borderColor.getColor());

        matrices.pop();

        postDraw(context, startTime);

        context.disableScissor();
    }
}
