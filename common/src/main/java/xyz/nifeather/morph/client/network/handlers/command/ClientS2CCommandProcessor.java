package xyz.nifeather.morph.client.network.handlers.command;

import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CSetCommandsAgent;
import xyz.nifeather.fmccl.processor.S2CCommandProcessor;

public class ClientS2CCommandProcessor extends S2CCommandProcessor
{
    @Override
    protected void registerSetEquipCommand(NetheriteS2CSetCommandsAgent agent)
    {
        agent.register(NetheriteS2CCommandNames.SetFakeEquip, LegacyS2CClientSetEquipCommand::from);
    }
}