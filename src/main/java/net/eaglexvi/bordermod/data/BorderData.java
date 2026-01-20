package net.eaglexvi.bordermod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class BorderData extends SavedData {
    public long lastActionTime = 0;
    public String lastState = "Retracted";

    public static BorderData get(ServerLevel level)
    {
        return level.getDataStorage().computeIfAbsent(
                BorderData::load,
                BorderData::new,
                "border_data"
        );
    }

    public static BorderData load(CompoundTag tag)
    {
        BorderData data = new BorderData();
        data.lastActionTime = tag.getLong("LastActionTime");
        data.lastState = tag.getString("LastState");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        tag.putLong("LastActionTime", lastActionTime);
        tag.putString("LastState", lastState);
        return tag;
    }
}
