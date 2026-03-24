package com.ourcraftoncraft.togglecraft.menu;

import com.ourcraftoncraft.togglecraft.ToggleCraftMod;
import com.ourcraftoncraft.togglecraft.blocks.FeatureFlagBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class FeatureFlagMenu extends AbstractContainerMenu {
    private final FeatureFlagBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    private BlockPos blockPos;

    public FeatureFlagMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readBlockPos());
    }

    public FeatureFlagMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, ContainerLevelAccess.create(playerInventory.player.level(), pos));
        // Store the position for client-side access
        this.blockPos = pos;
    }

    private BlockPos blockPos;

    public FeatureFlagMenu(int containerId, Inventory playerInventory, ContainerLevelAccess levelAccess) {
        super(ToggleCraftMod.FEATURE_FLAG_MENU.get(), containerId);
        this.levelAccess = levelAccess;
        this.blockEntity = null;
        this.blockPos = levelAccess.evaluate((level, pos) -> pos, BlockPos.ZERO);
    }

    public FeatureFlagMenu(int containerId, Inventory playerInventory, ContainerLevelAccess levelAccess, FeatureFlagBlockEntity blockEntity) {
        super(ToggleCraftMod.FEATURE_FLAG_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.levelAccess = levelAccess;
        this.blockPos = blockEntity != null ? blockEntity.getBlockPos() : BlockPos.ZERO;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return levelAccess.evaluate((level, pos) -> {
            if (blockEntity != null) {
                return stillValid(levelAccess, player, blockEntity.getBlockState().getBlock());
            }
            return level.getBlockEntity(pos) instanceof FeatureFlagBlockEntity;
        }, true);
    }

    public FeatureFlagBlockEntity getBlockEntity() {
        if (blockEntity != null) {
            return blockEntity;
        }
        return levelAccess.evaluate((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof FeatureFlagBlockEntity be) {
                return be;
            }
            return null;
        }, null);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }
}
