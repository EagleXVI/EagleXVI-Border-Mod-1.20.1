package net.eaglexvi.bordermod.data;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Random;


public class BorderSounds
{
    private BorderSounds() {}

    private static void PlaySound(ServerLevel level, SoundEvent sound, float volume, float pitch)
    {
        Holder<SoundEvent> soundHolder = level.registryAccess()
                .registryOrThrow(Registries.SOUND_EVENT)
                .wrapAsHolder(sound);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers())
        {
            player.connection.send(new ClientboundSoundPacket(
                    soundHolder,
                    SoundSource.AMBIENT,
                    player.getX(),
                    player.getY() + 20,
                    player.getZ(),
                    volume,
                    pitch,
                    level.getRandom().nextLong()
            ));
        }
    }

    public static void PlayBorderExpand(ServerLevel level)
    {
        PlaySound(level, SoundEvents.WITHER_SPAWN, 100.0F, 0.1F);
    }

    public static void PlayBorderRetract(ServerLevel level)
    {
        PlaySound(level, SoundEvents.ENDER_DRAGON_DEATH, 1.0F, 0.1F);
    }

    public static void Play30SecondsWarning(ServerLevel level)
    {
        PlaySound(level, SoundEvents.SCULK_SHRIEKER_SHRIEK, 100.0F, 0.1F);
    }

    public static void PlayThunder(ServerLevel level)
    {
        Random random = new Random();

        Holder<SoundEvent> soundHolder = level.registryAccess()
                .registryOrThrow(Registries.SOUND_EVENT)
                .wrapAsHolder(SoundEvents.LIGHTNING_BOLT_THUNDER);

        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers())
        {
            player.connection.send(new ClientboundSoundPacket(
                    soundHolder,
                    SoundSource.AMBIENT,
                    player.getX(),
                    player.getY() + 20,
                    player.getZ(),
                    100.0F,
                    0.1f + random.nextFloat() * (0.7f),
                    level.getRandom().nextLong()
            ));
        }
    }

    public static void PlayThunderLowPitch(ServerLevel level)
    {
        PlaySound(level, SoundEvents.LIGHTNING_BOLT_THUNDER, 100.0f, 0.1f);
    }


    public static void PlayGuardian(ServerLevel level)
    {
        PlaySound(level, SoundEvents.ELDER_GUARDIAN_AMBIENT, 100.0F, 0.1F);
    }
}
