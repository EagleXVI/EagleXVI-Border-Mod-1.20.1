package net.eaglexvi.bordermod.discordAPI;
import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.eaglexvi.bordermod.BorderMod;
import net.eaglexvi.bordermod.data.BorderData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.awt.*;

@Mod.EventBusSubscriber(modid = BorderMod.MOD_ID)
public class DiscordManager extends ListenerAdapter {
    public static JDA jda;

    public static String ChannelID;
    public static String Token;

    private static long previousCheckTime;

    static
    {
        previousCheckTime = System.currentTimeMillis();
    }

    /// Calls methods every second
    public static void Tick()
    {
        long currentTime = System.currentTimeMillis();

        // 5 minutes passed, so we need to do a check
        if (currentTime - previousCheckTime >= 330000)
        {
            previousCheckTime = currentTime;
            UpdatePlayerCount();
        }

    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event)
    {
        Token = BorderData.GetBotToken();
        ChannelID = BorderData.GetChannelID();

        if (Token.isEmpty() || ChannelID.isEmpty())
            return;

        DiscordManager listener = new DiscordManager();

        jda = JDABuilder.createDefault(Token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                .addEventListeners(listener)
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (!CheckAvailability())
            return;

        if (event.getAuthor().isBot())
            return;

        if (!event.getChannel().getId().equals(ChannelID))
            return;

        String senderName = event.getAuthor().getEffectiveName();
        String messageBody = event.getMessage().getContentDisplay();

        Component mcMessage;

        if (!event.getMessage().getAttachments().isEmpty())
            mcMessage = Component.literal("§9[Discord] §f" + senderName + ": atsiuntė nuotrauką!");
        else
            mcMessage = Component.literal("§9[Discord] §f" + senderName + ": " + messageBody);

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(() -> {
                server.getPlayerList().broadcastSystemMessage(mcMessage, false);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof Player player)
        {
            if (!player.level().isClientSide())
            {
                String playerName = player.getGameProfile().getName();
                String deathMessage = event.getSource().getLocalizedDeathMessage(player).getString();

                MessageDied(playerName, deathMessage);
            }
        }
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

    private static void MessageDied(String playerName, String deathMessage)
    {
        if (!CheckAvailability())
            return;

        TextChannel channel = jda.getTextChannelById(ChannelID);
        MessageEmbed embed = CreateEmbed(Color.WHITE, "\uD83D\uDC80 " + deathMessage + " \uD83D\uDC80");

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(embed).queue();
    }


    private static void MessageJoin(String playerName)
    {
        if (!CheckAvailability())
            return;

        TextChannel channel = jda.getTextChannelById(ChannelID);
        MessageEmbed embed = CreateEmbed(Color.GREEN, "\uD83D\uDEDC " + playerName + " prisijungė į serverį!" + " \uD83D\uDEDC");

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(embed).queue();
    }

    private static void MessageLeave(String playerName)
    {
        if (!CheckAvailability())
            return;

        TextChannel channel = jda.getTextChannelById(ChannelID);
        MessageEmbed embed = CreateEmbed(Color.RED, "\uD83D\uDEDC " + playerName + " atsijungė iš serverio!" + " \uD83D\uDEDC");

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(embed).queue();
    }

    public static void ForwardMessageToDiscord(String playerWhoSent, String message)
    {
        if (!CheckAvailability())
            return;

        TextChannel channel = jda.getTextChannelById(ChannelID);
        MessageEmbed embed = CreateEmbed(Color.GRAY, "\uD83D\uDCAC " + "<" + playerWhoSent + "> " + message + " \uD83D\uDCAC");

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(embed).queue();
    }

    public static void MessageToDiscord(String message)
    {
        if (!CheckAvailability())
            return;

        TextChannel channel = jda.getTextChannelById(ChannelID);
        MessageEmbed embed = CreateEmbed(Color.CYAN,"\uD83D\uDCE3 " + message + " \uD83D\uDCE3");

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(embed).queue();
    }

    public static void MessageBorderStatusToDiscord(String message)
    {
        if (!CheckAvailability())
            return;

        TextChannel channel = jda.getTextChannelById(ChannelID);
        MessageEmbed embed = CreateEmbed(Color.YELLOW, "⚠\uFE0F " + message + " ⚠\uFE0F");

        if (channel != null && channel.canTalk())
            channel.sendMessageEmbeds(embed).queue();
    }

    private static void UpdatePlayerCount()
    {
        if (!CheckAvailability())
            return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server != null)
        {
            int onlineCount = server.getPlayerCount();
            int maxPlayers = server.getMaxPlayers();

            String borderStatus = BorderData.GetCurrentState();

            // 2. Run the Discord logic asynchronously to prevent server lag
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    TextChannel channel = jda.getTextChannelById(ChannelID);
                    if (channel == null) return;

                    String topic;
                    if (borderStatus.equals("Expanded"))
                        topic = "Borderis: Atviras | Žaidžia: " + onlineCount + "/" + maxPlayers;
                    else if (borderStatus.equals("Retracted"))
                        topic = "Borderis: Uždaras | Žaidžia: " + onlineCount + "/" + maxPlayers;
                    else
                        topic = "Žaidžia: " + onlineCount + "/" + maxPlayers;

                    // Only send if the topic actually changed to save on rate limits
                    if (topic.equals(channel.getTopic())) return;

                    channel.getManager().setTopic(topic).queue(
                            success -> LogUtils.getLogger().info("[Discord] Sėkmingai atnaujintas statusas."),
                            failure -> LogUtils.getLogger().warn("[Discord] Nepavyko atnaujinti (Tikriausiai Rate Limit): " + failure.getMessage())
                    );
                } catch (Exception e) {
                    LogUtils.getLogger().error("[Discord] Klaida atnaujinant žaidėjų kiekį: " + e.getMessage());
                }
            });
        }
    }

    private static MessageEmbed CreateEmbed(Color color, String text)
    {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(color);
        embed.setAuthor(text, null, null);

        return embed.build();
    }

    private static boolean CheckAvailability()
    {
        if (jda == null || ChannelID.isEmpty() || Token.isEmpty())
        {
            LogUtils.getLogger().info("Discord is not connected!");
            return false;
        }

        return true;
    }


}
