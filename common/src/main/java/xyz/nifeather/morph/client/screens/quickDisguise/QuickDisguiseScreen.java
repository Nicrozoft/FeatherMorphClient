package xyz.nifeather.morph.client.screens.quickDisguise;

import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.network.chat.Component;
import xyz.nifeather.morph.client.ClientMorphManager;
import xyz.nifeather.morph.client.FeatherMorphClientBootstrap;
import xyz.nifeather.morph.client.graphics.Anchor;
import xyz.nifeather.morph.client.graphics.Axes;
import xyz.nifeather.morph.client.graphics.container.FlowContainer;
import xyz.nifeather.morph.client.screens.spinner.SpinnerScreen;

public class QuickDisguiseScreen extends SpinnerScreen<QDWidget>
{
    private final FeatherMorphClientBootstrap morphClient;

    private final ClientMorphManager morphManager;

    public QuickDisguiseScreen()
    {
        super(Component.literal("Quick disguise configuration screen"));

        this.morphClient = FeatherMorphClientBootstrap.getInstance();
        this.morphManager = morphClient.morphManager;

        var flow = new FlowContainer();
        flow.addRange(
                this.createWidget(),
                this.createWidget(),
                this.createWidget(),
                this.createWidget()
        );
        flow.setFlowAxes(Axes.Y);
        flow.setSpacing(5);
        flow.setRelativeSizeAxes(Axes.Both);
        flow.setSize(new UVPair(0.7f, 0.5f));
        flow.setAnchor(Anchor.Centre);

        this.add(flow);
    }

    @Override
    protected QDWidget createWidget()
    {
        var widget = new QDWidget();

        widget.setRelativeSizeAxes(Axes.X);
        widget.setHeight(this.getWidgetHeight());
        //widget.setAnchor(Anchor.Centre);

        return widget;
    }

    @Override
    protected int getWidgetWidth()
    {
        return 100;
    }

    @Override
    protected int getWidgetHeight()
    {
        return 30;
    }
}
