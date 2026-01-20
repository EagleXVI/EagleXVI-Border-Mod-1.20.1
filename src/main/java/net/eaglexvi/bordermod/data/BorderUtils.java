package net.eaglexvi.bordermod.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ServerLevelData;

public class BorderUtils {
    public static boolean minutesInformed = false;
    public static boolean hoursInformed = false;

    public static void MessageAllPlayers(ServerLevel level, String message, TextColor color, boolean isBold)
    {
        // Create a styled component
        Component text = Component.literal(message)
                .withStyle(style -> style
                        .withColor(color)
                        .withBold(isBold)
                );

        // Send to all players in the world
        for (ServerPlayer player : level.getPlayers(player -> true)) {
            player.sendSystemMessage(text, false); // false = not action bar
        }
    }

    public static void MessageRemainingTime(ServerLevel level, long currentTime, long lastTime, String lastState)
    {
        long timeElapsed = currentTime - lastTime;
        long timeOfInterest = (lastState.equals("Expanded")) ? BorderConstants.RETRACT_INTERVAL : BorderConstants.EXPAND_INTERVAL;
        String messageEnding = (lastState.equals("Expanded")) ? "expanding!" : "retracting!";
        TextColor color = TextColor.fromRgb(0xC6C6C6);

        long timeLeft = timeOfInterest - timeElapsed;

        if (Math.round(timeLeft - timeElapsed) <= BorderConstants.MINUTE && !minutesInformed)
        {
            minutesInformed = true;
            MessageAllPlayers(level, "1 Minute left until border starts " + messageEnding, color, false);

            ServerLevelData serverData = (ServerLevelData) level.getLevelData();
            serverData.setClearWeatherTime(0);
            serverData.setThundering(true);
            serverData.setRaining(true);
            serverData.setRainTime(20 * 90);
            serverData.setThunderTime(20 * 60);
        }

        else if (timeLeft <= BorderConstants.HOUR && !hoursInformed)
        {
            hoursInformed = true;
            MessageAllPlayers(level, "1 Hour left until border starts " + messageEnding, color, false);
        }

        if (timeLeft <= 0)
        {
            minutesInformed = false;
            hoursInformed = false;
        }
    }
}
