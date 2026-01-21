package net.eaglexvi.bordermod.data;

import net.minecraftforge.common.ForgeConfigSpec;

public class BorderConfig {
    private BorderConfig() {}

    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    // Expansion data
    public static ForgeConfigSpec.LongValue EXPANDED_SIZE;
    public static ForgeConfigSpec.LongValue EXPANSION_INTERVAL_IN_SECONDS;
    public static ForgeConfigSpec.LongValue EXPANSION_DURATION_IN_SECONDS;

    // Retraction data
    public static ForgeConfigSpec.LongValue RETRACTED_SIZE;
    public static ForgeConfigSpec.LongValue RETRACTION_INTERVAL_IN_SECONDS;
    public static ForgeConfigSpec.LongValue RETRACTION_DURATION_IN_SECONDS;

    // Other data
    public static ForgeConfigSpec.BooleanValue BORDER_STOPPED;
    public static ForgeConfigSpec.ConfigValue<String> BORDER_STATE;

    static
    {
        BUILDER.push("Expansion Settings");
        EXPANDED_SIZE = BUILDER
                .comment("The radius of the border in blocks when fully expanded")
                .defineInRange("radius", 200L, 1L, Long.MAX_VALUE);
        EXPANSION_INTERVAL_IN_SECONDS = BUILDER
                .comment("How often the border expands in seconds")
                .defineInRange("interval", 120L, 1L, Long.MAX_VALUE);
        EXPANSION_DURATION_IN_SECONDS = BUILDER
                .comment("Time it takes for the border to fully expand")
                .defineInRange("duration", 15L, 1L, Long.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Retraction Settings");
        RETRACTED_SIZE = BUILDER
                .comment("The radius of the border in blocks when fully retracted")
                .defineInRange("radius", 100L, 1L, Long.MAX_VALUE);
        RETRACTION_INTERVAL_IN_SECONDS = BUILDER
                .comment("How often the border retracts in seconds")
                .defineInRange("radius", 120L, 1L, Long.MAX_VALUE);
        RETRACTION_DURATION_IN_SECONDS = BUILDER
                .comment("Time it takes for the border to fully retract")
                .defineInRange("duration", 10L, 1L, Long.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Other Settings");
        BORDER_STOPPED = BUILDER
                .comment("Is the border currently stopped")
                .define("value", true);
        BORDER_STATE = BUILDER
                .comment("Current state of the border")
                .define("state", "Retracted");
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
