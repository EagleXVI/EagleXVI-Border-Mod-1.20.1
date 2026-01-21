package net.eaglexvi.bordermod.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class BorderMessages
{
    private final Component Text;

    public BorderMessages(String message)
    {
        Text = Component.literal(message)
                .withStyle(style -> style
                        .withColor(0xC6C6C6)
                        .withBold(false)
                );
    }

    public BorderMessages(String message, TextColor messageColor, boolean isBold)
    {
        Text = Component.literal(message)
                .withStyle(style -> style
                        .withColor(messageColor)
                        .withBold(isBold)
                );
    }

    public void MessageAllPlayers(ServerLevel level)
    {
        for(ServerPlayer player : level.getPlayers(player -> true))
        {
            player.sendSystemMessage(Text, false);
        }
    }

}
