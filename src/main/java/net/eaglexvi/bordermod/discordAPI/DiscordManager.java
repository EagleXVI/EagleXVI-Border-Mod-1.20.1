package net.eaglexvi.bordermod.discordAPI;
import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.eaglexvi.bordermod.BorderMod;
import net.eaglexvi.bordermod.data.BorderData;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber(modid = BorderMod.MOD_ID)
public class DiscordManager {
    public static JDA jda;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event)
    {
        String botToken = BorderData.GetBotToken();

        if (botToken.isEmpty())
        {
            LogUtils.getLogger().info("No JDA detected. Did user add bot token to config?");
            return;
        }

        jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

    }

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event)
    {
        String playerName = event.getPlayer().getName().getString();
        String message = event.getRawText();

        ForwardMessageToDiscord(playerName, message);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        String playerName = event.getEntity().getName().getString();

        MessageJoin(playerName);
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        String playerName = event.getEntity().getName().getString();

        MessageLeave(playerName);
    }

    private static void MessageJoin(String playerName)
    {
        if (jda == null)
        {
            LogUtils.getLogger().info("No JDA detected. Did user add bot token to config?");
            return;
        }

        String channelID = BorderData.GetChannelID();

        if (channelID.isEmpty())
        {
            LogUtils.getLogger().info("No Channel detected. Did user add channel ID to config?");
        }

        TextChannel channel = jda.getTextChannelById(channelID);
        EmbedBuilder joinEmbed = new EmbedBuilder();
        joinEmbed.setColor(Color.GREEN);
        joinEmbed.setAuthor("\uD83D\uDEDC " + playerName + " prisijungė į serverį!" + " \uD83D\uDEDC", null, null);

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(joinEmbed.build()).queue();
    }

    private static void MessageLeave(String playerName)
    {
        if (jda == null)
        {
            LogUtils.getLogger().info("No JDA detected. Did user add bot token to config?");
            return;
        }

        String channelID = BorderData.GetChannelID();

        if (channelID.isEmpty())
        {
            LogUtils.getLogger().info("No Channel detected. Did user add channel ID to config?");
        }

        TextChannel channel = jda.getTextChannelById(channelID);
        EmbedBuilder leaveEmbed = new EmbedBuilder();
        leaveEmbed.setColor(Color.RED);
        leaveEmbed.setAuthor("\uD83D\uDEDC " + playerName + " atsijungė iš serverio!" + " \uD83D\uDEDC", null, null);

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(leaveEmbed.build()).queue();
    }

    public static void ForwardMessageToDiscord(String playerWhoSent, String message)
    {
        if (jda == null)
        {
            LogUtils.getLogger().info("No JDA detected");
            return;
        }

        String channelID = BorderData.GetChannelID();

        if (channelID.isEmpty())
        {
            LogUtils.getLogger().info("No Channel detected. Did user add channel ID to config?");
        }

        TextChannel channel = jda.getTextChannelById(channelID);
        EmbedBuilder messageEmbed = new EmbedBuilder();
        messageEmbed.setColor(Color.GRAY);
        messageEmbed.setAuthor("\uD83D\uDCAC " + "<" + playerWhoSent + "> " + message + " \uD83D\uDCAC", null, null);

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(messageEmbed.build()).queue();
    }

    public static void MessageToDiscord(String message)
    {
        if (jda == null)
        {
            LogUtils.getLogger().info("No JDA detected. Did user add bot token to config?");
            return;
        }

        String channelID = BorderData.GetChannelID();

        if (channelID.isEmpty())
        {
            LogUtils.getLogger().info("No Channel detected. Did user add channel ID to config?");
        }

        TextChannel channel = jda.getTextChannelById(channelID);
        EmbedBuilder messageEmbed = new EmbedBuilder();
        messageEmbed.setColor(Color.CYAN);
        messageEmbed.setAuthor("\uD83D\uDCE3 " + message + " \uD83D\uDCE3", null, null);

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(messageEmbed.build()).queue();
    }

    public static void MessageBorderStatusToDiscord(String message)
    {
        if (jda == null)
        {
            LogUtils.getLogger().info("No JDA detected. Did user add bot token to config?");
            return;
        }

        String channelID = BorderData.GetChannelID();

        if (channelID.isEmpty())
        {
            LogUtils.getLogger().info("No Channel detected. Did user add channel ID to config?");
        }

        TextChannel channel = jda.getTextChannelById(channelID);
        EmbedBuilder messageEmbed = new EmbedBuilder();
        messageEmbed.setColor(Color.YELLOW);
        messageEmbed.setAuthor("⚠\uFE0F " + message + " ⚠\uFE0F", null, null);

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(messageEmbed.build()).queue();
    }


}
