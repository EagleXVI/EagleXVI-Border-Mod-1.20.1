package net.eaglexvi.bordermod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class BorderData extends SavedData {
    public long nextActionTime = Long.MAX_VALUE;

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

        data.nextActionTime = tag.getLong("NextActionTime");

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        tag.putLong("NextActionTime", nextActionTime);

        return tag;
    }

    public static long GetExpansionInterval()
    {
        return 1000L * BorderConfig.EXPANSION_INTERVAL_IN_SECONDS.get();
    }

    public static void SetExpansionInterval(long newInterval)
    {
        BorderConfig.EXPANSION_INTERVAL_IN_SECONDS.set(newInterval);
        BorderConfig.SPEC.save();
    }

    public static long GetExpansionDuration()
    {
        return 1000L * BorderConfig.EXPANSION_DURATION_IN_SECONDS.get();
    }

    public static void SetExpansionDuration(long newInterval)
    {
        BorderConfig.EXPANSION_DURATION_IN_SECONDS.set(newInterval);
        BorderConfig.SPEC.save();
    }

    public static long GetExpansionSize()
    {
        return BorderConfig.EXPANDED_SIZE.get();
    }

    public static void SetExpansionSize(long value)
    {
        BorderConfig.EXPANDED_SIZE.set(value);
        BorderConfig.SPEC.save();
    }


    public static long GetRetractionInterval()
    {
        return 1000L * BorderConfig.RETRACTION_INTERVAL_IN_SECONDS.get();
    }

    public static void SetRetractionInterval(long newInterval)
    {
        BorderConfig.RETRACTION_INTERVAL_IN_SECONDS.set(newInterval);
        BorderConfig.SPEC.save();
    }

    public static long GetRetractionDuration()
    {
        return 1000L * BorderConfig.RETRACTION_DURATION_IN_SECONDS.get();
    }

    public static void SetRetractionDuration(long newInterval)
    {
        BorderConfig.RETRACTION_DURATION_IN_SECONDS.set(newInterval);
        BorderConfig.SPEC.save();
    }

    public static long GetRetractionSize()
    {
        return BorderConfig.RETRACTED_SIZE.get();
    }

    public static void SetRetractionSize(long value)
    {
        BorderConfig.RETRACTED_SIZE.set(value);
        BorderConfig.SPEC.save();
    }

    public static String GetCurrentState()
    {
        return BorderConfig.BORDER_STATE.get();
    }

    public static void SetCurrentState(String state)
    {
        BorderConfig.BORDER_STATE.set(state);
        BorderConfig.SPEC.save();
    }

    public static boolean IsBorderStopped()
    {
        return BorderConfig.BORDER_STOPPED.get();
    }

    public static void SetIsBorderStopped(boolean value)
    {
        BorderConfig.BORDER_STOPPED.set(value);
        BorderConfig.SPEC.save();
    }

}
