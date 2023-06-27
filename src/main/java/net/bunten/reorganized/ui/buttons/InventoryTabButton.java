package net.bunten.reorganized.ui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
import net.bunten.reorganized.ui.ROInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class InventoryTabButton extends Button {
    
    private boolean isRight;
    private String name;
    private ROInventoryScreen screen;

    public InventoryTabButton(ROInventoryScreen screen, String name, boolean isRight, int x, int y, OnPress onPress) {
        super(x, y, 22, 20, Component.translatable("reorganized.tab." + name), onPress, DEFAULT_NARRATION);
        this.screen = screen;
        this.isRight = isRight;
        this.name = name;
    }
    
    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.pose().pushPose();
        context.setColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int mult = isRight ? 1 : -1;

        context.pose().translate(0, 0, 200);

        context.blit(Reorganized.id("textures/gui/inventory/buttons.png"), getX(), getY(), isRight ? width + 1 : 0, isOpen() ? height + 1 : 0, width, height);
        context.blit(Reorganized.id("textures/gui/inventory/tabs/" + name + ".png"), getX() + (isRight ? 0 : 6) + ((isOpen() ? 2 : 0) * mult), getY() + 2, 0, 0, 16, 16, 16, 16);

        context.pose().popPose();
    }

    private boolean isOpen() {
        return isRight ? screen.selectedRightButton == this : screen.selectedLeftButton == this;
    }

    @Override
    public void onPress() {
        super.onPress();

        if (isOpen()) {
            if (isRight) screen.selectedRightButton = null; else screen.selectedLeftButton = null;
        } else {
            if (isRight) screen.selectedRightButton = this; else screen.selectedLeftButton = this;
        }
    }
}