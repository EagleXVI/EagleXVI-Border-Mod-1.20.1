package net.eaglexvi.bordermod.data;

import com.mojang.logging.LogUtils;
import net.eaglexvi.bordermod.BorderMod;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.fml.common.Mod;

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

            if (!BorderData.IsBorderStopped())
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

        switch(BorderData.GetCurrentState())
        {
            case "Expanded":
                timeLeft = (data.nextActionTime - currentTime) / 1000;

                if (timeLeft == 10)
                    BorderSounds.PlayThunder(level);

                else if (timeLeft == 30)
                    BorderSounds.PlayGuardian(level);

                else if (timeLeft == 60)
                {
                    BorderMessages message = new BorderMessages("Tu pajauti stiprų gūsį iš pasaulio krašto...");
                    message.MessageAllPlayers(level);

                    ServerLevelData levelData = (ServerLevelData) level.getLevelData();

                    levelData.setClearWeatherTime(0);
                    levelData.setRaining(true);
                    levelData.setRainTime(20 * 90);

                    BorderSounds.PlayThunderLowPitch(level);
                }

                else if (timeLeft == 600)
                {
                    BorderMessages message = new BorderMessages("Iš gelmių kyla duslus dundėjimas...");
                    message.MessageAllPlayers(level);
                }

                else if (timeLeft == 3600)
                {
                    BorderMessages message = new BorderMessages("Oras aplink tave pamažu tampa sunkus...");
                    message.MessageAllPlayers(level);
                }

                if (timeLeft >= 0)
                    LogUtils.getLogger().info("Border will retract in: " + String.valueOf(timeLeft) + " seconds!");

                break;

            case "Retracted":
                timeLeft = (data.nextActionTime - currentTime) / 1000;

                if (timeLeft == 9)
                    BorderSounds.PlayThunder(level);

                else if (timeLeft == 20)
                    BorderSounds.PlayThunder(level);

                else if (timeLeft == 30)
                    BorderSounds.Play30SecondsWarning(level);

                else if (timeLeft == 45)
                    BorderSounds.PlayThunder(level);

                else if (timeLeft == 60)
                {
                    BorderMessages message = new BorderMessages("Vibracijos tampa nepakenčiamos. Pasaulis ruošiasi atsiverti...");
                    message.MessageAllPlayers(level);

                    ServerLevelData levelData = (ServerLevelData) level.getLevelData();

                    levelData.setClearWeatherTime(0);
                    levelData.setRaining(true);
                    levelData.setThundering(true);
                    levelData.setRainTime(20 * 90);
                    levelData.setThunderTime(20 * 60);

                    BorderSounds.PlayThunderLowPitch(level);
                }

                else if (timeLeft == 600)
                {
                    BorderMessages message = new BorderMessages("Barjeras tarp tavęs ir nebūties darosi vis plonesnis...");
                    message.MessageAllPlayers(level);
                }

                else if (timeLeft == 3600)
                {
                    BorderMessages message = new BorderMessages("Horizonte pasirodė plyšys, kurio neturėtų būti...");
                    message.MessageAllPlayers(level);
                }

                if (timeLeft >= 0)
                    LogUtils.getLogger().info("Border will retract in: " + String.valueOf(timeLeft) + " seconds!");

                break;

            default:
                break;
        }
    }

    /// Checks if border needs to be updated
    private static void CheckBorderStatus(ServerLevel level, long currentTime, BorderData data)
    {
        String currentBorderState = BorderData.GetCurrentState();

        // Expand border if set time has elapsed
        if (currentBorderState.equals("Retracted") && currentTime - data.nextActionTime >= 0)
            ExpandBorder(level, currentTime, data);

        // Retract border if set time has elapsed
        else if (currentBorderState.equals("Expanded") && currentTime - data.nextActionTime >= 0)
            RetractBorder(level, currentTime, data);
    }

    /// Retracts border
    public static void RetractBorder(ServerLevel level, long currentTime, BorderData data)
    {
        WorldBorder border = level.getWorldBorder();
        border.lerpSizeBetween(BorderData.GetExpansionSize(), BorderData.GetRetractionSize(), BorderData.GetRetractionDuration());

        BorderMessages Message = new BorderMessages(
                "Pasaulio pakraščiai traukiasi!",
                TextColor.fromRgb(0xFF5555),
                true
        );

        Message.MessageAllPlayers(level);
        BorderSounds.PlayBorderRetract(level);

        BorderData.SetCurrentState("Retracted");
        data.nextActionTime = currentTime + BorderData.GetExpansionInterval();
        data.setDirty();

    }

    /// Expands border
    public static void ExpandBorder(ServerLevel level, long currentTime, BorderData data)
    {
        WorldBorder border = level.getWorldBorder();
        border.lerpSizeBetween(BorderData.GetRetractionSize(), BorderData.GetExpansionSize(), BorderData.GetExpansionDuration());

        BorderMessages Message = new BorderMessages(
                "Pasaulio pakraščiai plečiasi!",
                TextColor.fromRgb(0x56A600),
                true
        );

        Message.MessageAllPlayers(level);
        BorderSounds.PlayBorderExpand(level);

        BorderData.SetCurrentState("Expanded");
        data.nextActionTime = currentTime + BorderData.GetRetractionInterval();
        data.setDirty();
    }

    public static void ReSyncBorder(ServerLevel level, BorderData data, long currentTime)
    {
        if (data.nextActionTime - currentTime >= 0)
            return;

        if (BorderData.GetExpansionInterval() <= 0 || BorderData.GetRetractionInterval() <= 0)
            return;

        WorldBorder border = level.getWorldBorder();

        LogUtils.getLogger().info("Player missed a deadline, recalculating next action!");

        while(data.nextActionTime - currentTime < 0)
        {
            if (BorderData.GetCurrentState().equals("Retracted"))
            {
                BorderData.SetCurrentState("Expanded");
                data.nextActionTime += BorderData.GetRetractionInterval();
                LogUtils.getLogger().info("Border should be expanded");
            }

            else if (BorderData.GetCurrentState().equals("Expanded"))
            {
                BorderData.SetCurrentState("Retracted");
                data.nextActionTime += BorderData.GetExpansionInterval();
                LogUtils.getLogger().info("Border should be retracted");
            }
        }

        // set size
        if (BorderData.GetCurrentState().equals("Retracted"))
            border.setSize(BorderData.GetRetractionSize());
        else if (BorderData.GetCurrentState().equals("Expanded"))
            border.setSize(BorderData.GetExpansionSize());
    }
}
