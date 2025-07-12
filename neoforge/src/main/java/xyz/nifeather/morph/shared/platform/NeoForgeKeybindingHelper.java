package xyz.nifeather.morph.shared.platform;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

public class NeoForgeKeybindingHelper
{
    public NeoForgeKeybindingHelper()
    {
    }

    public void register(IEventBus modBus)
    {
        modBus.register(this);
    }

    private final List<KeyMapping> keyMappings = new ObjectArrayList<>();

    @SubscribeEvent
    public void onKeymapInit(RegisterKeyMappingsEvent event)
    {
        keyMapRegistered = true;
        this.keyMappings.forEach(event::register);
    }

    private boolean keyMapRegistered = false;

    public void addKeybind(KeyMapping key)
    {
        if (keyMapRegistered)
            throw new RuntimeException("Keymap event already fired, cannot register more keys!");

        keyMappings.add(key);
    }
}
