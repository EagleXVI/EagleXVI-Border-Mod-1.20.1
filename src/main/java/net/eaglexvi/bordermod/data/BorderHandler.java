package net.eaglexvi.bordermod.data;

import com.mojang.logging.LogUtils;
import net.eaglexvi.bordermod.BorderMod;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.jline.utils.Log;

import javax.swing.border.Border;
import java.awt.*;

@Mod.EventBusSubscriber(modid = BorderMod.MOD_ID)
public class BorderHandler
{
    private static long previousCheckTime;

    static
    {
        previousCheckTime = System.currentTimeMillis();
    }

    private BorderHandler() {}

    /// Calls methods every second
    public static void Tick(ServerLevel level)
    {
        long currentTime = System.currentTimeMillis();

        // 1 Second passed, so we need to do a check
        if (currentTime - previousCheckTime >= 1000)
        {
            previousCheckTime = currentTime;

            BorderData data = BorderData.get(level);

            if (!data.isStopped)
            {
                CheckMessageStatus(level, currentTime, data);
                CheckBorderStatus(level, currentTime, data);
            }

        }

    }

    /// Checks/Informs players of upcoming border changes
    private static void CheckMessageStatus(ServerLevel level, long currentTime, BorderData data)
    {
        long timeLeft = 0;

        switch(data.lastState)
        {
            case "Retracted":
                timeLeft = ((data.lastActionTime + BorderData.GetExpansionInterval()) - currentTime) / 1000;

                if (timeLeft == 60)
                {
                    BorderMessages message = new BorderMessages("The border will expand in 1 minute!");
                    message.MessageAllPlayers(level);
                }
                else if (timeLeft == 600)
                {
                    BorderMessages message = new BorderMessages("The border will expand in 10 minutes!");
                    message.MessageAllPlayers(level);
                }
                else if (timeLeft == 3600)
                {
                    BorderMessages message = new BorderMessages("The border will expand in 1 hour!");
                    message.MessageAllPlayers(level);
                }
                break;

            case "Expanded":
                timeLeft = ((data.lastActionTime + BorderData.GetRetractionInterval()) - currentTime) / 1000;

                if (timeLeft == 60)
                {
                    BorderMessages message = new BorderMessages("The border will retract in 1 minute!");
                    message.MessageAllPlayers(level);

                    ServerLevelData levelData = (ServerLevelData) level.getLevelData();

                    levelData.setClearWeatherTime(0);
                    levelData.setRaining(true);
                    levelData.setThundering(true);
                    levelData.setRainTime(20 * 90);
                    levelData.setThunderTime(20 * 60);
                }
                else if (timeLeft == 600)
                {
                    BorderMessages message = new BorderMessages("The border will retract in 10 minutes!");
                    message.MessageAllPlayers(level);
                }
                else if (timeLeft == 3600)
                {
                    BorderMessages message = new BorderMessages("The border will retract in 1 hour!");
                    message.MessageAllPlayers(level);
                }
                break;
            default:
                break;
        }

        LogUtils.getLogger().info("Time left before border expands/retracts: " + String.valueOf(timeLeft));
    }

    /// Checks if border needs to be updated
    private static void CheckBorderStatus(ServerLevel level, long currentTime, BorderData data)
    {
        // Expand border if set time has elapsed
        if (data.lastState.equals("Retracted") && currentTime - data.lastActionTime >= BorderData.GetExpansionInterval())
            ExpandBorder(level, currentTime, data);

        // Retract border if set time has elapsed
        else if (data.lastState.equals("Expanded") && currentTime - data.lastActionTime >= BorderData.GetRetractionInterval())
            RetractBorder(level, currentTime, data);

        LogUtils.getLogger().info("Border status: " + String.valueOf(data.lastState));
    }

    /// Retracts border
    private static void RetractBorder(ServerLevel level, long currentTime, BorderData data)
    {
        WorldBorder border = level.getWorldBorder();
        data.lastState = "Retracted";
        data.lastActionTime = currentTime;
        border.lerpSizeBetween(BorderData.GetExpansionSize(), BorderData.GetRetractionSize(), BorderData.GetRetractionDuration());
        data.setDirty();

        BorderMessages Message = new BorderMessages(
                "The border is retracting!",
                TextColor.fromRgb(0xFF5555),
                true
        );

        Message.MessageAllPlayers(level);
        BorderSounds.PlayBorderRetract(level);

    }

    /// Expands border
    private static void ExpandBorder(ServerLevel level, long currentTime, BorderData data)
    {
        WorldBorder border = level.getWorldBorder();

        data.lastState = "Expanded";
        data.lastActionTime = currentTime;
        border.lerpSizeBetween(BorderData.GetRetractionSize(), BorderData.GetExpansionSize(), BorderData.GetExpansionDuration());
        data.setDirty();

        BorderMessages Message = new BorderMessages(
                "The border is expanding!",
                TextColor.fromRgb(0x56A600),
                true
        );

        Message.MessageAllPlayers(level);
        BorderSounds.PlayBorderExpand(level);
    }


    ///  Restarts Border
    public static void RestartBorder(ServerLevel level, long currentTime, BorderData data)
    {
        if (data.lastState.equals("Retracted"))
            BorderHandler.ExpandBorder(level, System.currentTimeMillis(), data);
        else if (data.lastState.equals("Expanded"))
            BorderHandler.RetractBorder(level, System.currentTimeMillis(), data);
    }
}
