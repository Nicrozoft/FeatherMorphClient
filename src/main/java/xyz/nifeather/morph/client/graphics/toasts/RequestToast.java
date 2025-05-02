package xyz.nifeather.morph.client.graphics.toasts;

import xyz.nifeather.morph.client.graphics.color.MaterialColors;
import net.minecraft.network.chat.Component;
import xyz.nifeather.morph.network.commands.S2C.S2CUpdateRequestStatusCommand;

public class RequestToast extends LinedToast
{
    @Override
    protected boolean fadeInOnEnter()
    {
        return true;
    }

    public RequestToast(S2CUpdateRequestStatusCommand.Type type, String sourceName)
    {
        var color = switch (type)
        {
            case RequestExpired, RequestExpiredOwner -> MaterialColors.Orange500;
            case RequestAccepted -> MaterialColors.Green400;
            case RequestDenied -> MaterialColors.Red400;
            default -> MaterialColors.Indigo500;
        };

        this.setLineColor(color);

        Component text, desc;

        if (type == S2CUpdateRequestStatusCommand.Type.RequestSend)
            text = Component.translatable("text.morphclient.toast.request.send");
        else if (type == S2CUpdateRequestStatusCommand.Type.RequestExpired || type == S2CUpdateRequestStatusCommand.Type.RequestExpiredOwner)
            text = Component.translatable("text.morphclient.toast.request.expire");
        else if (type == S2CUpdateRequestStatusCommand.Type.NewRequest)
            text = Component.translatable("text.morphclient.toast.request.receive");
        else
            text = Component.translatable(type == S2CUpdateRequestStatusCommand.Type.RequestAccepted
                    ? "text.morphclient.toast.request.accepted"
                    : "text.morphclient.toast.request.denied");

        this.setTitle(text);

        desc = Component.translatable(type.isRequestOwner()
                ? "text.morphclient.toast.request.to"
                : "text.morphclient.toast.request.from", sourceName);

        this.setDescription(desc);
    }

    @Override
    protected float getTextStartX()
    {
        return 8;
    }

    @Override
    protected int getTextWidth()
    {
        return (int) (this.width() * 0.85F);
    }
}
