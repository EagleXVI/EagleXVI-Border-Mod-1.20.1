package net.eaglexvi.bordermod;

import com.mojang.logging.LogUtils;
import net.eaglexvi.bordermod.data.BorderCommands;
import net.eaglexvi.bordermod.data.BorderData;
import net.eaglexvi.bordermod.data.BorderHandler;
import net.eaglexvi.bordermod.discordAPI.DiscordManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.eaglexvi.bordermod.data.BorderConfig;
import org.slf4j.Logger;

// made by eaglexvi

@Mod(BorderMod.MOD_ID)
@Mod.EventBusSubscriber(modid = BorderMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BorderMod {

    public static final String MOD_ID = "eaglexvi_border";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BorderMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the config of the mod
        context.registerConfig(ModConfig.Type.COMMON, BorderConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    /// Checks border state each tick
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END)
            return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            return;

        BorderHandler.Tick(server.overworld());
        DiscordManager.Tick();
    }

    /// Registers Custom Commands
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        BorderCommands.Register(event.getDispatcher());

        LOGGER.info("Sucessfully registered custom commands!");
    }

    /// On server start
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        if (server == null)
            return;

        ServerLevel level = server.overworld().getLevel();
        BorderData data = BorderData.get(level);

        BorderHandler.ReSyncBorder(level, data, System.currentTimeMillis());
    }
}