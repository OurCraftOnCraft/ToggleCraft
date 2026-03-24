package com.ourcraftoncraft.togglecraft.blocks;

import com.ourcraftoncraft.togglecraft.LaunchDarklyManager;
import com.ourcraftoncraft.togglecraft.ToggleCraftMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class FeatureFlagBlockEntity extends BlockEntity implements MenuProvider {
    private String flagKey = "";

    public FeatureFlagBlockEntity(BlockPos pos, BlockState state) {
        super(ToggleCraftMod.FEATURE_FLAG_BLOCK_ENTITY.get(), pos, state);
    }

    public String getFlagKey() {
        return flagKey;
    }

    public void setFlagKey(String flagKey) {
        this.flagKey = flagKey;
        setChanged();
        if (level != null) {
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("FlagKey", flagKey);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        flagKey = tag.getString("FlagKey");
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Feature Flag Block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FeatureFlagMenu(containerId, playerInventory, worldPosition);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FeatureFlagBlockEntity be) {
        if (level.isClientSide) {
            return;
        }

        // Periodically update redstone output based on flag value
        if (be.flagKey != null && !be.flagKey.isEmpty()) {
            boolean flagValue = LaunchDarklyManager.getFlagValue(be.flagKey, false);
            // Trigger block update to refresh redstone signal
            if (level.getGameTime() % 20 == 0) { // Every second
                level.updateNeighborsAt(pos, state.getBlock());
            }
        }
    }
}
