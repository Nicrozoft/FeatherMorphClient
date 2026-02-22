package xyz.nifeather.morph.client.screens.quickDisguise;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.joml.Vector2f;
import xyz.nifeather.morph.client.graphics.DrawableSprite;
import xyz.nifeather.morph.client.graphics.DrawableText;
import xyz.nifeather.morph.client.graphics.MarginPadding;
import xyz.nifeather.morph.client.graphics.container.Container;
import xyz.nifeather.morph.client.graphics.transforms.easings.Easing;
import xyz.nifeather.morph.client.screens.disguise.DisplayWidget;
import xyz.nifeather.morph.client.storage.struct.SavedDisguise;

import java.util.List;

public class QuickDisguiseEntry extends ContainerObjectSelectionList.Entry<QuickDisguiseEntry>
{
    private final SavedDisguise disguise;
    private final String name;

    private final DrawableText describeTextWidget;
    private final WidgetList widgetList;

    public SavedDisguise savedDisguise()
    {
        return disguise;
    }

    public String name()
    {
        return name;
    }

    private final Container backgroundContainer = new Container();
    private final DrawableSprite spriteSelected;
    private final DrawableSprite spriteHover;

    public QuickDisguiseEntry(String name, SavedDisguise savedDisguise, WidgetList widgetList)
    {
        this.disguise = savedDisguise;
        this.name = name;

        this.describeTextWidget = new DrawableText();
        describeTextWidget.setText(Component.literal(name));
        describeTextWidget.setMargin(new MarginPadding(5));

        this.widgetList = widgetList;

        backgroundContainer.add(spriteSelected = new DrawableSprite(DisplayWidget.buttonTextureSelected));
        backgroundContainer.add(spriteHover = new DrawableSprite(DisplayWidget.buttonTextureOverlay));
    }

    @Override
    public void setWidth(int i)
    {
        super.setWidth(i);
        updateBackgroundSize(false);
    }

    @Override
    public List<? extends NarratableEntry> narratables()
    {
        return List.of();
    }

    private boolean wasHovered = false;

    public void initBackground()
    {
        updateBackgroundSize(true);
    }

    public void setSelected(boolean selected)
    {
        spriteSelected.fadeTo(selected ? 1 : 0, 300, Easing.OutExpo);
    }

    private void updateBackgroundSize(boolean makeHidden)
    {
        var backgroundSize = new Vector2f(this.getContentWidth(), this.getContentHeight());
        backgroundContainer.setSize(backgroundSize);

        backgroundContainer.children().forEach(d ->
        {
            if (makeHidden)
                d.setAlpha(0f);

            d.setSize(backgroundSize);
        });
    }

    @Override
    public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean bl, float f)
    {
        context.pose().pushMatrix();
        context.pose().translate(this.getContentX(), this.getContentY());

        var isHovered = isMouseOver(mouseX, mouseY);
        if (wasHovered != isHovered)
        {
            wasHovered = isHovered;

            if (isHovered)
                spriteHover.fadeIn(300, Easing.OutExpo);
            else
                spriteHover.fadeOut(300, Easing.OutExpo);
        }

        //context.fill(0, 0, this.getContentWidth(), this.getContentHeight(), 0xff888888);

        children().forEach(e ->
        {
            if (!(e instanceof Renderable renderable))
                return;

            renderable.render(context, mouseX, mouseY, f);

        });
        context.pose().popMatrix();
    }

    @Override
    public List<? extends GuiEventListener> children()
    {
        return List.of(backgroundContainer, describeTextWidget);
    }
}
