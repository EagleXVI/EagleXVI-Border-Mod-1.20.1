package net.eaglexvi.bordermod.data;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class BorderCommands
{
    private BorderCommands() {}

    public static void Register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("customBorder")
                .requires(source -> source.hasPermission(2))

                // ./customBorder expansionIntervalSeconds
                .then(Commands.literal("expansionIntervalSeconds")
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1))
                            .executes(context -> {
                                long expansionInterval = LongArgumentType.getLong(context, "seconds");
                                return ChangeExpansionInterval(context.getSource(), expansionInterval);
                            })
                        )
                )

                // ./customBorder expansionDurationSeconds
                .then(Commands.literal("expansionDurationSeconds")
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1))
                            .executes(context -> {
                                long expansionDuration = LongArgumentType.getLong(context, "seconds");
                                return ChangeExpansionDuration(context.getSource(), expansionDuration);
                            })
                        )
                )

                // ./customBorder expandedSize
                .then(Commands.literal("expandedSize")
                        .then(Commands.argument("size", LongArgumentType.longArg(1))
                            .executes(context -> {
                                long expansionSize = LongArgumentType.getLong(context, "size");
                                return ChangeExpandedSize(context.getSource(), expansionSize);
                            })
                        )
                )

                // ./customBorder retractionIntervalSeconds
                .then(Commands.literal("retractionIntervalSeconds")
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1))
                            .executes(context -> {
                                long retractionInterval = LongArgumentType.getLong(context, "seconds");
                                return ChangeRetractionInterval(context.getSource(), retractionInterval);
                            })
                        )
                )

                // ./customBorder retractionDurationSeconds
                .then(Commands.literal("retractionDurationSeconds")
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1))
                            .executes(context -> {
                                long retractionDuration = LongArgumentType.getLong(context, "seconds");
                                return ChangeRetractionDuration(context.getSource(), retractionDuration);
                            })
                        )
                )

                // ./customBorder retractedSize
                .then(Commands.literal("retractedSize")
                        .then(Commands.argument("size", LongArgumentType.longArg(1))
                            .executes(context -> {
                                long retractedSize = LongArgumentType.getLong(context, "size");
                                return ChangeRetractedSize(context.getSource(), retractedSize);
                            })
                        )
                )

                // ./customBorder state
                .then(Commands.literal("state")
                        .then(Commands.literal("expanded")
                                .executes(context -> {
                                    return ChangeState(context.getSource(), true);
                                })
                        )

                        .then(Commands.literal("retracted")
                                .executes(context -> {
                                    return ChangeState(context.getSource(), false);
                                })
                        )
                )

                // ./customBorder stop
                .then(Commands.literal("stop")
                    .executes(context -> {
                        return StopBorder(context.getSource());
                     })
                )


                // ./customBorder start
                .then(Commands.literal("start")
                        .executes(context -> {
                            return StartBorder(context.getSource());
                        })
                )


                // ./customBorder setNewActionDate
                .then(Commands.literal("setNewActionDate")
                        .then(Commands.argument("year", IntegerArgumentType.integer(1))
                                .then(Commands.argument("month", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("day", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("hour", IntegerArgumentType.integer(0))
                                                        .then(Commands.argument("minute", IntegerArgumentType.integer(0))
                                                                .then(Commands.argument("second", IntegerArgumentType.integer(0))
                                                                        .executes(context -> {
                                                                            int year = IntegerArgumentType.getInteger(context,"year");
                                                                            int month = IntegerArgumentType.getInteger(context,"month");
                                                                            int day = IntegerArgumentType.getInteger(context,"day");
                                                                            int hour = IntegerArgumentType.getInteger(context,"hour");
                                                                            int minute = IntegerArgumentType.getInteger(context,"minute");
                                                                            int second = IntegerArgumentType.getInteger(context,"second");

                                                                            return SetNewActionDate(context.getSource(), year, month, day, hour, minute, second);
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )

                        )
                )

                .then(Commands.literal("currentDate")
                        .executes(context -> {
                            return GetCurrentDate(context.getSource());
                        })
                )

                .then(Commands.literal("skipState")
                        .executes(context -> {
                            return SkipState(context.getSource());
                        })
                )
        );

    }

    private static int SkipState(CommandSourceStack source)
    {
        ServerLevel level = source.getLevel();
        BorderData data = BorderData.get(source.getLevel());

        if (BorderData.GetCurrentState().equals("Expanded"))
            BorderHandler.RetractBorder(level, System.currentTimeMillis(), data);
        else if (BorderData.GetCurrentState().equals("Retracted"))
            BorderHandler.ExpandBorder(level, System.currentTimeMillis(), data);

        return 1;
    }

    private static int GetCurrentDate(CommandSourceStack source)
    {
        long currentTime = System.currentTimeMillis();

        Instant instant = Instant.ofEpochMilli(currentTime);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        source.sendSuccess(() -> Component.literal("Current time is: " + dateTime.format(formatter)), true);

        return 1;
    }

    private static int SetNewActionDate(CommandSourceStack source, int year, int month, int day, int hour, int minute, int second)
    {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        long systemTime = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        ServerLevel level = source.getLevel();
        BorderData data = BorderData.get(level);

        data.nextActionTime = systemTime;
        data.setDirty();

        return 1;
    }

    private static int StopBorder(CommandSourceStack source)
    {
        ServerLevel level = source.getLevel();
        WorldBorder border = level.getWorldBorder();

        // Stop border's retraction and expansion
        border.setSize(border.getSize());

        // Fix BorderConfig and Data
        BorderData.SetIsBorderStopped(true);

        return 1;
    }

    private static int StartBorder(CommandSourceStack source)
    {
        ServerLevel level = source.getLevel();
        BorderData data = BorderData.get(level);

        // Fix BorderConfig and Data
        BorderData.SetIsBorderStopped(false);

        // Re-Sync
        BorderHandler.ReSyncBorder(level, data, System.currentTimeMillis());

        return 1;
    }

    private static int ChangeState(CommandSourceStack source, boolean val)
    {
        // Border is retracted
        if (!val)
            BorderData.SetCurrentState("Retracted");

        // Border is expanded
        else
            BorderData.SetCurrentState("Expanded");

        return 1;
    }

    private static int ChangeExpansionInterval(CommandSourceStack source, long value)
    {
        BorderData.SetExpansionInterval(value);

        return 1;
    }

    private static int ChangeExpansionDuration(CommandSourceStack source, long value)
    {
        BorderData.SetExpansionDuration(value);

        return 1;
    }

    private static int ChangeExpandedSize(CommandSourceStack source, long value)
    {
        BorderData.SetExpansionSize(value);

        return 1;
    }

    private static int ChangeRetractionInterval(CommandSourceStack source, long value)
    {
        BorderData.SetRetractionInterval(value);

        return 1;
    }

    private static int ChangeRetractionDuration(CommandSourceStack source, long value)
    {
        BorderData.SetRetractionDuration(value);

        return 1;
    }

    private static int ChangeRetractedSize(CommandSourceStack source, long value)
    {
        BorderData.SetRetractionSize(value);

        return 1;
    }
}
