package xyz.nifeather.morph.client.network.handlers.record;

import xyz.nifeather.morph.network.commands.S2C.S2CCommandRecord;

import java.util.Map;

public record CommandHandleResult(boolean success, S2CCommandRecord result) {
    private static final CommandHandleResult resultFailed = new CommandHandleResult(false, new S2CCommandRecord("failed", Map.of()));

    public static CommandHandleResult fail() {
        return resultFailed;
    }

    public static CommandHandleResult from(S2CCommandRecord input) {
        return new CommandHandleResult(true, input);
    }
}
