package xyz.nifeather.morph.server.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

    private void onEntityKilledEntityEvent(ServerWorld world, Entity damager, LivingEntity killed)
    {
        if (!(damager instanceof ServerPlayerEntity player))
            return;

        if (damager.equals(killed))
            return;

        if (killed instanceof ServerPlayerEntity targetedPlayer)
        {
            morphManager.grantDisguiseToPlayer(player, DisguiseTypes.PLAYER.toId(targetedPlayer.getNameForScoreboard()));
        }
        else
        {
            var type = killed.getType();

            if (type != EntityType.CREAKING)
                morphManager.grantDisguiseToPlayer(player, EntityType.getId(type).toString());
        }
    }
}
