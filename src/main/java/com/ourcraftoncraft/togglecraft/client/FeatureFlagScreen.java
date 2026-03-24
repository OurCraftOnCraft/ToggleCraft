package com.ourcraftoncraft.togglecraft.client;

import com.ourcraftoncraft.togglecraft.ToggleCraftMod;
import com.ourcraftoncraft.togglecraft.menu.FeatureFlagMenu;
import com.ourcraftoncraft.togglecraft.blocks.FeatureFlagBlockEntity;
import com.ourcraftoncraft.togglecraft.network.UpdateFlagKeyPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FeatureFlagScreen extends AbstractContainerScreen<FeatureFlagMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("togglecraft", "textures/gui/feature_flag.png");
    private EditBox flagKeyField;
    private Button saveButton;

    public FeatureFlagScreen(FeatureFlagMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Get current flag key from block entity (client-side, may be null)
        String currentFlagKey = "";
        try {
            FeatureFlagBlockEntity be = menu.getBlockEntity();
            if (be != null) {
                currentFlagKey = be.getFlagKey();
            }
        } catch (Exception e) {
            // Block entity may not be available on client side
        }

        this.flagKeyField = new EditBox(this.font, x + 10, y + 50, 156, 20, Component.literal("Flag Key"));
        this.flagKeyField.setMaxLength(256);
        this.flagKeyField.setValue(currentFlagKey);
        this.flagKeyField.setBordered(true);
        this.flagKeyField.setVisible(true);
        this.addRenderableWidget(this.flagKeyField);

        // Get block position from menu
        BlockPos blockPos = menu.getBlockPos();
        this.saveButton = Button.builder(Component.literal("Save"), button -> {
            if (blockPos != null) {
                ToggleCraftMod.CHANNEL.sendToServer(new UpdateFlagKeyPacket(blockPos, this.flagKeyField.getValue()));
                this.minecraft.player.closeContainer();
            }
        }).bounds(x + 10, y + 80, 156, 20).build();
        this.addRenderableWidget(this.saveButton);

        this.setInitialFocus(this.flagKeyField);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFFC6C6C6);
        graphics.fill(x + 1, y + 1, x + this.imageWidth - 1, y + this.imageHeight - 1, 0xFF000000);
        graphics.drawString(this.font, this.title, x + 8, y + 6, 0x404040, false);
        graphics.drawString(this.font, Component.literal("Enter Feature Flag Key:"), x + 10, y + 35, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.flagKeyField.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.flagKeyField.isFocused() && this.flagKeyField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.flagKeyField.isFocused() && this.flagKeyField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}
