package net.eaglexvi.bordermod.data;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;


public class BorderSounds
{
    public static void PlayBorderExpand(ServerLevel level)
    {
        Holder<SoundEvent> soundHolder = level.registryAccess()
                .registryOrThrow(Registries.SOUND_EVENT)
                .wrapAsHolder(SoundEvents.WARDEN_DEATH);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers())
        {
            player.connection.send(new ClientboundSoundPacket(
                    soundHolder,
                    SoundSource.AMBIENT,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    1.0F,
                    0.1F,
                    level.getRandom().nextLong()
            ));
        }
    }

    public static void PlayBorderRetract(ServerLevel level)
    {
        Holder<SoundEvent> soundHolder = level.registryAccess()
                .registryOrThrow(Registries.SOUND_EVENT)
                .wrapAsHolder(SoundEvents.ENDER_DRAGON_DEATH);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers())
        {
            player.connection.send(new ClientboundSoundPacket(
                    soundHolder,
                    SoundSource.AMBIENT,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    1.0F,
                    0.1F,
                    level.getRandom().nextLong()
            ));
        }
    }
}
