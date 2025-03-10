package xyz.nifeather.morph.shared.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Allows ":" in the string, and stop when we met whitespace
 */
public class RelaxedStringArgumentType implements ArgumentType<String>
{
    private static final List<String> EXAMPLES = List.of("allay", "minecraft:allay", "player:Icalingua");
    public static final RelaxedStringArgumentType INSTANCE = new RelaxedStringArgumentType();

    @Override
    @NotNull
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        int begin = reader.getCursor();

        if (!reader.canRead())
            reader.skip();

        while (reader.canRead() && !Character.isWhitespace(reader.peek()))
            reader.skip();

        return reader.getString().substring(begin, reader.getCursor());
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }
}
