package net.eaglexvi.bordermod.data;

public class BorderConstants {
    private BorderConstants() {}

    // Time constants
    public static final long SECOND = 1000L;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;

    // Expansion constants
    public static final long EXPAND_INTERVAL = 2 * MINUTE; // 48 * HOUR
    public static final long EXPAND_RADIUS = 200; // 10K blocks
    public static final long EXPAND_DURATION = 20 * SECOND; // 6 * MINUTE

    // Retraction constants
    public static final long RETRACT_INTERVAL = 2 * MINUTE; // 24 * HOUR
    public static final long RETRACT_RADIUS = 100; // 500 blocks
    public static final long RETRACT_DURATION = 10 * SECOND; // 6 * MINUTE
}
