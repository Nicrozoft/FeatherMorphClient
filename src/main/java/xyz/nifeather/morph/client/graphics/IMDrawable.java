package xyz.nifeather.morph.client.graphics;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.model.geom.builders.UVPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMDrawable extends Renderable, GuiEventListener, NarratableEntry
{
    public void invalidatePosition();
    public void invalidateLayout();

    public void setWidth(float width);
    public void setHeight(float height);
    public void setSize(UVPair vector);

    public float getRenderWidth();
    public float getRenderHeight();

    @NotNull
    public MarginPadding getPadding();

    public void setParent(@Nullable IMDrawable parent);

    @Nullable
    public IMDrawable getParent();

    public float getScreenSpaceX();
    public float getScreenSpaceY();

    /**
     * Depth of this IMDrawable, higher value means this drawable should be rendered below others
     */
    public int getDepth();
    public void setDepth(int depth);

    default void dispose()
    {
    }
}
