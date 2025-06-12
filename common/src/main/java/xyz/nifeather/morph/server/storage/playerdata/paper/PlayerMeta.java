package xyz.nifeather.morph.server.storage.playerdata.paper;

import com.google.gson.annotations.Expose;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import xyz.nifeather.morph.server.misc.DisguiseMeta;

import java.util.List;
import java.util.UUID;

public class PlayerMeta
{
    /**
     * 玩家的UUID
     */
    @Expose(serialize = false)
    public UUID uniqueId;

    /**
     * 浏览JSON时参考用的数据
     */
    @Expose
    @Nullable
    public String playerName;

    /**
     * 此玩家解锁的所有伪装
     *
     * @apiNote 移除或添加伪装请使用addDisguise和removeDisguise
     */
    @Expose(serialize = false)
    private final ObjectArrayList<DisguiseMeta> unlockedDisguises = new ObjectArrayList<>();

    @Unmodifiable
    public List<DisguiseMeta> getUnlockedDisguises()
    {
        return new ObjectImmutableList<>(unlockedDisguises);
    }

    public void setUnlockedDisguises(ObjectArrayList<DisguiseMeta> newList)
    {
        unlockedDisguises.clear();
        unlockedDisguises.addAll(newList);

        unlockedDisguiseIdentifiers.clear();
        unlockedDisguises.forEach(info -> unlockedDisguiseIdentifiers.add(info.getKey()));
    }

    public void addDisguise(DisguiseMeta info)
    {
        unlockedDisguiseIdentifiers.add(info.getKey());
        unlockedDisguises.add(info);
    }

    public void removeDisguise(DisguiseMeta info)
    {
        unlockedDisguiseIdentifiers.remove(info.getKey());
        unlockedDisguises.remove(info);
    }

    /**
     * 此玩家解锁的所有伪装（原始数据）
     */
    @Expose
    private final ObjectArrayList<String> unlockedDisguiseIdentifiers = new ObjectArrayList<>();

    /**
     * @return Gets copy of unlocked disguise identifiers
     */
    public List<String> getUnlockedDisguiseIdentifiers()
    {
        return new ObjectArrayList<>(unlockedDisguiseIdentifiers);
    }

    @Override
    public String toString()
    {
        return "PlayerMeta{ UUID=%s, Name=%s }".formatted(this.uniqueId, this.playerName);
    }

    /**
     * 伪装是否对自身可见？
     */
    @Expose
    public boolean showDisguiseToSelf = false;

    /**
     * 是否显示过一次自身可见提示？
     */
    @Expose
    public boolean shownDisplayToSelfHint = false;

    @Expose
    public boolean shownClientSkillHint;

    @Expose
    public boolean shownMorphHint;

    @Expose
    public boolean shownMorphClientHint;
}
