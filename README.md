# ToggleCraft

A Minecraft mod that adds a feature flag block to Minecraft using LaunchDarkly.

## Features

- **Feature Flag Block**: A block that looks like a lever with a flag
- **LaunchDarkly Integration**: Connects to LaunchDarkly to check feature flag values
- **Redstone Output**: When a feature flag is `true`, the block emits a redstone signal of 15
- **Configurable**: Right-click the block to enter a feature flag key

## Setup

1. **Configure LaunchDarkly SDK Key**:
   - Edit the server config file: `config/togglecraft-server.toml`
   - Add your LaunchDarkly SDK key to the `launchdarklySdkKey` field

2. **Place the Block**:
   - Place the Feature Flag Block in your world
   - Right-click to open the configuration GUI
   - Enter your feature flag key
   - Click "Save"

3. **Redstone Output**:
   - When the feature flag is `true`, the block emits a redstone signal of 15
   - When the feature flag is `false`, the block emits no signal (0)

## Building from Source

1. Clone this repository
2. Build the mod:
   ```bash
   ./gradlew build
   ```
3. The built JAR will be in `build/libs/`

## Development

### Running in Development

- **Client**: `./gradlew runClient`
- **Server**: `./gradlew runServer`

### Project Structure

```
src/main/java/com/ourcraftoncraft/togglecraft/
├── ToggleCraftMod.java           # Main mod class
├── ToggleCraftConfig.java        # Configuration
├── LaunchDarklyManager.java      # LaunchDarkly SDK integration
├── blocks/
│   ├── FeatureFlagBlock.java    # The feature flag block
│   └── FeatureFlagBlockEntity.java  # Block entity to store flag key
├── menu/
│   └── FeatureFlagMenu.java      # Menu container
└── client/
    ├── FeatureFlagScreen.java    # GUI screen
    └── ClientSetup.java          # Client-side registration
```

## Technical Details

- **Minecraft Version**: 1.20.1
- **Forge Version**: 47.4.10
- **LaunchDarkly SDK**: Java Server SDK 7.4.0
- **Redstone Power**: Emits power level 15 when flag is true, 0 when false

## License

MIT

## Credits

Created for the OurCraftOnCraft server.
