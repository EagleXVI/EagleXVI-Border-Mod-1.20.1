package net.eaglexvi.bordermod.data;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;

public class BorderCommands
{
    public static boolean IsBorderStopped;

    private BorderCommands() {}

    public static void Instantiate(ServerLevel level)
    {
        BorderData data = BorderData.get(level);
        IsBorderStopped = data.isStopped;
    }

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

                // ./customBorder border stop
                .then(Commands.literal("border")
                        .then(Commands.literal("stop")
                            .executes(context -> {
                                return StopBorder(context.getSource());
                             })
                        )
                )

                // ./customBorder border stop
                .then(Commands.literal("border")
                        .then(Commands.literal("start")
                                .executes(context -> {
                                    return StartBorder(context.getSource());
                                })
                        )
                )
        );

    }

    private static int StopBorder(CommandSourceStack source)
    {
        IsBorderStopped = true;

        ServerLevel level = source.getLevel();
        WorldBorder border = level.getWorldBorder();
        BorderData data = BorderData.get(level);

        // Stop border's retraction and expansion
        border.setSize(border.getSize());

        // Fix BorderConfig and Data
        BorderConfig.BORDER_STOPPED.set(true);
        BorderConfig.SPEC.save();

        data.isStopped = true;

        return 1;
    }

    private static int StartBorder(CommandSourceStack source)
    {
        IsBorderStopped = false;

        ServerLevel level = source.getLevel();
        BorderData data = BorderData.get(level);

        // Fix BorderConfig and Data
        BorderConfig.BORDER_STOPPED.set(false);
        BorderConfig.SPEC.save();

        data.isStopped = false;

        // Restart the border from state (since stop could've caused it to stop in the middle of expansion/retraction)
        BorderHandler.RestartBorder(level, System.currentTimeMillis(), data);

        return 1;
    }

    private static int ChangeState(CommandSourceStack source, boolean val)
    {
        if (!IsBorderStopped)
        {
            source.sendSuccess(() -> Component.literal("Border must be stopped!"), true);
            return 0;
        }

        BorderData data = BorderData.get(source.getLevel());

        // Border is retracted
        if (!val)
        {
            data.lastState = "Retracted";

            BorderConfig.BORDER_STATE.set("Retracted");
            BorderConfig.SPEC.save();
        }

        // Border is expanded
        else
        {
            data.lastState = "Expanded";

            BorderConfig.BORDER_STATE.set("Expanded");
            BorderConfig.SPEC.save();
        }

        return 1;
    }

    private static int ChangeExpansionInterval(CommandSourceStack source, long value)
    {
        if (!IsBorderStopped)
        {
            source.sendSuccess(() -> Component.literal("Border must be stopped!"), true);
            return 0;
        }

        BorderConfig.EXPANSION_INTERVAL_IN_SECONDS.set(value);
        BorderConfig.SPEC.save();

        return 1;
    }

    private static int ChangeExpansionDuration(CommandSourceStack source, long value)
    {
        if (!IsBorderStopped)
        {
            source.sendSuccess(() -> Component.literal("Border must be stopped!"), true);
            return 0;
        }

        BorderConfig.EXPANSION_DURATION_IN_SECONDS.set(value);
        BorderConfig.SPEC.save();

        return 1;
    }

    private static int ChangeExpandedSize(CommandSourceStack source, long value)
    {
        if (!IsBorderStopped)
        {
            source.sendSuccess(() -> Component.literal("Border must be stopped!"), true);
            return 0;
        }

        BorderConfig.EXPANDED_SIZE.set(value);
        BorderConfig.SPEC.save();

        return 1;
    }

    private static int ChangeRetractionInterval(CommandSourceStack source, long value)
    {
        if (!IsBorderStopped)
        {
            source.sendSuccess(() -> Component.literal("Border must be stopped!"), true);
            return 0;
        }

        BorderConfig.RETRACTION_INTERVAL_IN_SECONDS.set(value);
        BorderConfig.SPEC.save();

        return 1;
    }

    private static int ChangeRetractionDuration(CommandSourceStack source, long value)
    {
        if (!IsBorderStopped)
        {
            source.sendSuccess(() -> Component.literal("Border must be stopped!"), true);
            return 0;
        }

        BorderConfig.RETRACTION_DURATION_IN_SECONDS.set(value);
        BorderConfig.SPEC.save();

        return 1;
    }

    private static int ChangeRetractedSize(CommandSourceStack source, long value)
    {
        if (!IsBorderStopped)
        {
            source.sendSuccess(() -> Component.literal("Border must be stopped!"), true);
            return 0;
        }

        BorderConfig.RETRACTED_SIZE.set(value);
        BorderConfig.SPEC.save();

        return 1;
    }
}
