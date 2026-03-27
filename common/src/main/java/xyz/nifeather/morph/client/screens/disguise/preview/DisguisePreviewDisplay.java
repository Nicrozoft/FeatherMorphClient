package xyz.nifeather.morph.client.screens.disguise.preview;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.Nullable;
import xyz.nifeather.morph.client.graphics.EntityDisplay;

public class DisguisePreviewDisplay extends EntityDisplay
{
    public DisguisePreviewDisplay(String rawIdentifier, boolean displayLoadingIfNotValid, InitialSetupMethod initialSetupMethod)
    {
        super(rawIdentifier, displayLoadingIfNotValid, initialSetupMethod);
    }

    public DisguisePreviewDisplay(String id)
    {
        super(id);
    }

    @Override
    protected void onRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
    {
        var yStart = (int)getScreenSpaceY();

        var yCentre = yStart + renderHeight / 2;
        var mX = getScreenSpaceX() + renderWidth * 0.3f;

        super.onRender(context, (int)mX, yCentre, delta);
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigation)
    {
        return null;
    }
}
