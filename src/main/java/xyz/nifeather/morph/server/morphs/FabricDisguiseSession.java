package xyz.nifeather.morph.server.morphs;

import org.jetbrains.annotations.NotNull;
import xyz.nifeather.morph.network.commands.S2C.S2CAnimationCommand;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.network.FabricClientHandler;
import xyz.nifeather.morph.server.disguise.animations.AnimationSequence;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.server.disguise.providers.AbstractDisguiseProvider;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class FabricDisguiseSession extends ServerPluginObject
{
    private final ServerPlayer bindingPlayer;

    public ServerPlayer player()
    {
        return bindingPlayer;
    }

    private final String disguiseIdentifier;

    public String disguiseIdentifier()
    {
        return disguiseIdentifier;
    }

    @NotNull
    private final AbstractDisguiseProvider disguiseProvider;

    public AbstractDisguiseProvider disguiseProvider()
    {
        return disguiseProvider;
    }

    public FabricDisguiseSession(ServerPlayer bindingPlayer,
                                 String disguiseIdentifier,
                                 @NotNull AbstractDisguiseProvider disguiseProvider)
    {
        this.disguiseIdentifier = disguiseIdentifier;
        this.bindingPlayer = bindingPlayer;
        this.disguiseProvider = disguiseProvider;

        animationSequence.onNewAnimation(anim ->
        {
            var animSubId = anim.subId();

            if (anim.availableForClient())
                clientHandler.sendCommand(player(), new S2CAnimationCommand(animSubId));

            //this.getDisguiseWrapper().playAnimation(animSubId);

            //if (animSubId.startsWith("exec_"))
            //    handleInternalExec(animSubId);
        });
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricClientHandler clientHandler;

    private final AnimationSequence animationSequence = new AnimationSequence();

    public boolean tryScheduleSequence(@NotNull String sequenceIdentifier,
                                    List<SingleAnimation> sequence)
    {
        this.animationSequence.scheduleNext(sequenceIdentifier, sequence);

        var player = player();
        var message = Component.translatableWithFallback("morph.commands.going_to_play_animation",
                "Going to play animation: %s",
                Component.translatable("emote.morphclient." + sequenceIdentifier));

        player.displayClientMessage(message, false);

        return true;
    }

    public void update()
    {
        animationSequence.update();
    }
}
