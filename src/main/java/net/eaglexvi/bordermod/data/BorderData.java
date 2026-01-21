package net.eaglexvi.bordermod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class BorderData extends SavedData {
    public long lastActionTime = 0;
    public String lastState = "Retracted";
    public boolean isStopped = true;

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
        data.isStopped = tag.getBoolean("IsStopped");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        tag.putLong("LastActionTime", lastActionTime);
        tag.putString("LastState", lastState);
        tag.putBoolean("IsStopped", isStopped);
        return tag;
    }

    public static long GetExpansionInterval()
    {
        return 1000L * BorderConfig.EXPANSION_INTERVAL_IN_SECONDS.get();
    }

    public static long GetExpansionDuration()
    {
        return 1000L * BorderConfig.EXPANSION_DURATION_IN_SECONDS.get();
    }

    public static long GetExpansionSize()
    {
        return BorderConfig.EXPANDED_SIZE.get();
    }

    public static long GetRetractionInterval()
    {
        return 1000L * BorderConfig.RETRACTION_INTERVAL_IN_SECONDS.get();
    }

    public static long GetRetractionDuration()
    {
        return 1000L * BorderConfig.RETRACTION_DURATION_IN_SECONDS.get();
    }

    public static long GetRetractionSize()
    {
        return BorderConfig.RETRACTED_SIZE.get();
    }
}
