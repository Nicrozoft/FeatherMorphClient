package xyz.nifeather.morph.server.misc;

import java.util.Arrays;
import net.minecraft.resources.Identifier;

/**
 * 伪装类型
 */
public enum DisguiseTypes
{
    /**
     * 原版伪装
     */
    VANILLA("minecraft"),

    /**
     * 玩家伪装
     */
    PLAYER("player"),

    EXTERNAL("external"),

    /**
     * 未知类型或未指定类型
     */
    UNKNOWN("unknown");

    private final String nameSpace;

    private DisguiseTypes(String namespace)
    {
        this.nameSpace = namespace;
    }

    public String getNameSpace()
    {
        return nameSpace;
    }

    public static DisguiseTypes fromNameSpace(String namespace)
    {
        var types = DisguiseTypes.values();

        var optional = Arrays.stream(types).filter(t -> t.getNameSpace().equals(namespace)).findFirst();

        return optional.orElse(UNKNOWN);
    }

    /**
     * 获取某个ID的伪装类型
     * @param id 目标ID
     * @return 伪装类型，如果找不到Provider则返回null
     * @apiNote minecraft:player不能作为ID传入，请使用player:xxx
     */
    public static DisguiseTypes fromId(String id)
    {
        //将minecraft:player视作外部伪装
        if (id.equals("minecrcaft:player"))
            return DisguiseTypes.EXTERNAL;

        var str = id + ":";
        var idSplited = str.split(":", 3);
        var result = fromNameSpace(idSplited[0]);

        if (result == UNKNOWN)
            result = EXTERNAL;

        return result;
    }

    public static DisguiseTypes fromId(Identifier key)
    {
        return fromId(key.toString());
    }

    public String toId(String id)
    {
        return this.getNameSpace() + ":" + id;
    }

    public Identifier toIdentifier(String idWithoutNamespace)
    {
        return Identifier.parse(toId(idWithoutNamespace));
    }

    public String toStrippedId(String rawString)
    {
        return rawString.replace(this.getNameSpace() + ":", "");
    }
}
