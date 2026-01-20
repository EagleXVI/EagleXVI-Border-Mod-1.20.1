package net.eaglexvi.bordermod.data;

import net.eaglexvi.bordermod.BorderMod;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.awt.*;

@Mod.EventBusSubscriber(modid = BorderMod.MOD_ID)
public class BorderHandler
{
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return;

        Check(server.overworld());
    }

    private static void Check(ServerLevel level)
    {
        WorldBorder border = level.getWorldBorder();
        BorderData data = BorderData.get(level);
        long currentTime = System.currentTimeMillis();
        boolean borderAction = false;

        BorderUtils.MessageRemainingTime(level, currentTime, data.lastActionTime, data.lastState);

        // Expand border if set time has elapsed
        if (data.lastState.equals("Retracted") && currentTime - data.lastActionTime >= BorderConstants.RETRACT_INTERVAL)
        {
            data.lastState = "Expanded";
            data.lastActionTime = currentTime;
            border.lerpSizeBetween(BorderConstants.RETRACT_RADIUS, BorderConstants.EXPAND_RADIUS, BorderConstants.EXPAND_DURATION);
            borderAction = true;
            BorderUtils.MessageAllPlayers(level, "The border is expanding!", TextColor.fromRgb(0x56A600), true);
        }

        // Retract border if set time has elapsed
        else if (data.lastState.equals("Expanded") && currentTime - data.lastActionTime >= BorderConstants.EXPAND_INTERVAL)
        {
            data.lastState = "Retracted";
            data.lastActionTime = currentTime;
            border.lerpSizeBetween(BorderConstants.EXPAND_RADIUS, BorderConstants.RETRACT_RADIUS, BorderConstants.RETRACT_DURATION);
            borderAction = true;
            BorderUtils.MessageAllPlayers(level, "The border is retracting!", TextColor.fromRgb(0xFF5555), true);
        }

        // Save data incase server restarts
        if (borderAction)
            data.setDirty();
    }
}
