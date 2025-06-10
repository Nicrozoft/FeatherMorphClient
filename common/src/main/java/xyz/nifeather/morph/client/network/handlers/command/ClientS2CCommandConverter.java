package xyz.nifeather.morph.client.network.handlers.command;

import xyz.nifeather.fmccl.converter.S2CCommandConverter;
import xyz.nifeather.fmccl.network.commands.S2C.NetheriteS2CCommandNames;
import xyz.nifeather.fmccl.network.commands.S2C.set.NetheriteS2CSetFakeEquipCommand;
import xyz.nifeather.morph.client.network.commands.ClientSetEquipCommand;
import xyz.nifeather.morph.network.commands.S2C.S2CCommandNames;
import xyz.nifeather.morph.network.commands.S2C.set.S2CSetFakeEquipCommand;

public class ClientS2CCommandConverter extends S2CCommandConverter {
    @Override
    protected void registerFakeEquipCommandConversions() {
        this.registerNetheriteToModern(NetheriteS2CCommandNames.SetFakeEquip, LegacyS2CClientSetEquipCommand.class, cmd ->
        {
            var netheriteSlot = cmd.getSlot();

            S2CSetFakeEquipCommand.ProtocolEquipmentSlot modernSlot = switch (netheriteSlot) {
                case MAINHAND -> S2CSetFakeEquipCommand.ProtocolEquipmentSlot.MAINHAND;
                case OFF_HAND -> S2CSetFakeEquipCommand.ProtocolEquipmentSlot.OFF_HAND;
                case HELMET -> S2CSetFakeEquipCommand.ProtocolEquipmentSlot.HELMET;
                case CHESTPLATE -> S2CSetFakeEquipCommand.ProtocolEquipmentSlot.CHESTPLATE;
                case LEGGINGS -> S2CSetFakeEquipCommand.ProtocolEquipmentSlot.LEGGINGS;
                case BOOTS -> S2CSetFakeEquipCommand.ProtocolEquipmentSlot.BOOTS;
            };

            return new ClientSetEquipCommand(cmd.getItemStack(), modernSlot);
        });

        this.registerModernToNetherite(S2CCommandNames.SetFakeEquip, ClientSetEquipCommand.class, cmd ->
        {
            var modernSlot = cmd.getSlot();

            NetheriteS2CSetFakeEquipCommand.ProtocolEquipmentSlot netheriteSlot = switch (modernSlot) {
                case MAINHAND -> NetheriteS2CSetFakeEquipCommand.ProtocolEquipmentSlot.MAINHAND;
                case OFF_HAND -> NetheriteS2CSetFakeEquipCommand.ProtocolEquipmentSlot.OFF_HAND;
                case HELMET -> NetheriteS2CSetFakeEquipCommand.ProtocolEquipmentSlot.HELMET;
                case CHESTPLATE -> NetheriteS2CSetFakeEquipCommand.ProtocolEquipmentSlot.CHESTPLATE;
                case LEGGINGS -> NetheriteS2CSetFakeEquipCommand.ProtocolEquipmentSlot.LEGGINGS;
                case BOOTS -> NetheriteS2CSetFakeEquipCommand.ProtocolEquipmentSlot.BOOTS;
            };

            return new LegacyS2CClientSetEquipCommand(cmd.getItemStack(), netheriteSlot);
        });
    }
}
