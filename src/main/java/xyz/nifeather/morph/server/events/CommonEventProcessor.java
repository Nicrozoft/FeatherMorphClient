package xyz.nifeather.morph.server.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import xiamomc.pluginbase.Annotations.Resolved;
import xyz.nifeather.morph.server.ServerPluginObject;
import xyz.nifeather.morph.server.misc.DisguiseTypes;
import xyz.nifeather.morph.server.morphs.FabricMorphManager;

public class CommonEventProcessor extends ServerPluginObject
{
    public void initListener()
    {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(this::onEntityKilledEntityEvent);
    }

    @Resolved(shouldSolveImmediately = true)
    private FabricMorphManager morphManager;

    private void onEntityKilledEntityEvent(ServerLevel world, Entity damager, LivingEntity killed)
    {
        if (!(damager instanceof ServerPlayer player))
            return;

        if (damager.equals(killed))
            return;

        if (killed instanceof ServerPlayer targetedPlayer)
        {
            morphManager.grantDisguiseToPlayer(player, DisguiseTypes.PLAYER.toId(targetedPlayer.getScoreboardName()));
        }
        else
        {
            var type = killed.getType();

            if (type != EntityType.CREAKING)
                morphManager.grantDisguiseToPlayer(player, EntityType.getKey(type).toString());
        }
    }
}
