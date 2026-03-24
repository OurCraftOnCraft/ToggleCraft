package com.ourcraftoncraft.togglecraft;

import net.minecraftforge.common.ForgeConfigSpec;

public class ToggleCraftConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> LAUNCHDARKLY_SDK_KEY;

    static {
        BUILDER.push("ToggleCraft Configuration");

        BUILDER.comment("LaunchDarkly SDK Key. Get this from your LaunchDarkly dashboard.");
        LAUNCHDARKLY_SDK_KEY = BUILDER.define("launchdarklySdkKey", "");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static String getLaunchDarklySdkKey() {
        return LAUNCHDARKLY_SDK_KEY.get();
    }
}
