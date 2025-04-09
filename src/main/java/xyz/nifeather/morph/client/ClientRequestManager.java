package xyz.nifeather.morph.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import xyz.nifeather.morph.client.graphics.toasts.RequestToast;
import xiamomc.morph.network.commands.S2C.S2CRequestCommand;
import xiamomc.pluginbase.Annotations.Initializer;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;

public class ClientRequestManager extends MorphClientObject
{
    private final ToastManager toastManager = Minecraft.getInstance().getToastManager();

    public void addRequest(S2CRequestCommand.Type type, String sourceName)
    {
        if (type == S2CRequestCommand.Type.Unknown) return;

        toastManager.addToast(new RequestToast(type, sourceName));
        requests.add(new Request(plugin.getCurrentTick(), type, sourceName));
    }

    public void scheduleRemoveRequest(String sourceName)
    {
        this.addSchedule(() -> requests.removeIf(r -> r.sourceName.equalsIgnoreCase(sourceName)));
    }

    public List<Request> getRequests()
    {
        return new ObjectArrayList<>(requests);
    }

    @Initializer
    private void load()
    {
        addSchedule(this::update);
    }

    private final List<Request> requests = new ObjectArrayList<>();

    private void update()
    {
        var currentTime = plugin.getCurrentTick();
        requests.removeIf(r -> currentTime - r.beginTime > 90 * 20);
    }

    private record Request(long beginTime, S2CRequestCommand.Type type, String sourceName)
    {
    }
}