package xyz.nifeather.morph.server.morphs;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.network.commands.S2C.S2CPlayAnimationCommand;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.disguise.animations.AnimationSequence;
import xyz.nifeather.morph.server.disguise.animations.SingleAnimation;
import xyz.nifeather.morph.server.disguise.providers.AbstractDisguiseProvider;
import xyz.nifeather.morph.server.network.ClientHandler;

import java.util.List;

public class DisguiseSession extends ServerPluginObject {
    private final ServerPlayer bindingPlayer;
    private final String disguiseIdentifier;
    @NotNull
    private final AbstractDisguiseProvider disguiseProvider;
    private final AnimationSequence animationSequence = new AnimationSequence();
    @Resolved(shouldSolveImmediately = true)
    private ClientHandler clientHandler;

    public DisguiseSession(ServerPlayer bindingPlayer,
                           String disguiseIdentifier,
                           @NotNull AbstractDisguiseProvider disguiseProvider) {
        this.disguiseIdentifier = disguiseIdentifier;
        this.bindingPlayer = bindingPlayer;
        this.disguiseProvider = disguiseProvider;

        animationSequence.onNewAnimation(anim ->
        {
            var animSubId = anim.subId();

            if (anim.availableForClient())
                clientHandler.sendCommand(player(), new S2CPlayAnimationCommand(animSubId));

            //this.getDisguiseWrapper().playAnimation(animSubId);

            //if (animSubId.startsWith("exec_"))
            //    handleInternalExec(animSubId);
        });
    }

    public ServerPlayer player() {
        return bindingPlayer;
    }

    public String disguiseIdentifier() {
        return disguiseIdentifier;
    }

    public AbstractDisguiseProvider disguiseProvider() {
        return disguiseProvider;
    }

    public boolean tryScheduleSequence(@NotNull String sequenceIdentifier,
                                       List<SingleAnimation> sequence) {
        this.animationSequence.scheduleNext(sequenceIdentifier, sequence);

        var player = player();
        var message = Component.translatableWithFallback("morph.commands.going_to_play_animation",
                "Going to play animation: %s",
                Component.translatable("emote.morphclient." + sequenceIdentifier));

        player.displayClientMessage(message, false);

        return true;
    }

    public void update() {
        animationSequence.update();
    }
}
