package com.ourcraftoncraft.togglecraft;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.LDConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LaunchDarklyManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static LDClient client;
    private static boolean initialized = false;

    public static void initialize() {
        String sdkKey = ToggleCraftConfig.getLaunchDarklySdkKey();
        if (sdkKey == null || sdkKey.isEmpty()) {
            LOGGER.warn("ToggleCraft: LaunchDarkly SDK key not configured. Feature flags will not work.");
            return;
        }

        try {
            LDConfig config = new LDConfig.Builder()
                    .offline(false)
                    .build();
            client = new LDClient(sdkKey, config);
            initialized = true;
            LOGGER.info("ToggleCraft: LaunchDarkly client initialized successfully.");
            MinecraftForge.EVENT_BUS.register(LaunchDarklyManager.class);
        } catch (Exception e) {
            LOGGER.error("ToggleCraft: Failed to initialize LaunchDarkly client", e);
        }
    }

    public static boolean isInitialized() {
        return initialized && client != null;
    }

    public static boolean getFlagValue(String flagKey, boolean defaultValue) {
        if (!isInitialized()) {
            return defaultValue;
        }

        try {
            // Use a default context for evaluation
            LDContext context = LDContext.builder("minecraft-server")
                    .kind("server")
                    .build();
            return client.boolVariation(flagKey, context, defaultValue);
        } catch (Exception e) {
            LOGGER.error("ToggleCraft: Error evaluating flag '{}'", flagKey, e);
            return defaultValue;
        }
    }

    public static void shutdown() {
        if (client != null) {
            try {
                client.close();
                LOGGER.info("ToggleCraft: LaunchDarkly client closed.");
            } catch (IOException e) {
                LOGGER.error("ToggleCraft: Error closing LaunchDarkly client", e);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // Periodic updates to LaunchDarkly (handled automatically by SDK)
    }
}
