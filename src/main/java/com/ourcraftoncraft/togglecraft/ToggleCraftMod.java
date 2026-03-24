package com.ourcraftoncraft.togglecraft;

import com.ourcraftoncraft.togglecraft.blocks.FeatureFlagBlock;
import com.ourcraftoncraft.togglecraft.blocks.FeatureFlagBlockEntity;
import com.ourcraftoncraft.togglecraft.menu.FeatureFlagMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import com.ourcraftoncraft.togglecraft.network.UpdateFlagKeyPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(ToggleCraftMod.MODID)
public class ToggleCraftMod {
    public static final String MODID = "togglecraft";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<MenuType<FeatureFlagMenu>> FEATURE_FLAG_MENU = MENUS.register("feature_flag_menu",
            () -> IForgeMenuType.create(FeatureFlagMenu::new));

    public static final RegistryObject<Block> FEATURE_FLAG_BLOCK = BLOCKS.register("feature_flag_block",
            () -> new FeatureFlagBlock(Block.Properties.of()
                    .strength(0.5F)
                    .sound(net.minecraft.world.level.block.SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<BlockEntityType<FeatureFlagBlockEntity>> FEATURE_FLAG_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("feature_flag_block",
                    () -> BlockEntityType.Builder.of(FeatureFlagBlockEntity::new, FEATURE_FLAG_BLOCK.get()).build(null));

    public static final RegistryObject<Item> FEATURE_FLAG_BLOCK_ITEM = ITEMS.register("feature_flag_block",
            () -> new BlockItem(FEATURE_FLAG_BLOCK.get(), new Item.Properties()));

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public ToggleCraftMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register config
        FMLJavaModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ToggleCraftConfig.SPEC);

        // Register blocks, block entities, items, and menus
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        MENUS.register(modEventBus);

        // Register common setup
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Register network packets
            int id = 0;
            CHANNEL.registerMessage(id++, UpdateFlagKeyPacket.class,
                    UpdateFlagKeyPacket::encode,
                    UpdateFlagKeyPacket::new,
                    UpdateFlagKeyPacket::handle);

            // Initialize LaunchDarkly client if SDK key is configured
            LaunchDarklyManager.initialize();
        });
    }
}
