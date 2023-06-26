package net.bunten.reorganized.ui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
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

        int v = isHovered() ? 21 : isOpen() ? 42 : 0;
        int a = isHovered() ? 1 : isOpen() ? 2 : 0;

        context.pose().translate(0, 0, 200);

        context.blit(Reorganized.id("textures/gui/inventory/buttons.png"), getX(), getY(), 0 + (isRight ? 23 : 0), v, width, height);
        context.blit(Reorganized.id("textures/gui/inventory/tabs/" + name + ".png"), getX() + (isRight ? 0 : 5) + (a * (isRight ? 1 : -1)), getY() + 2, 0, 0, 16, 16, 16, 16);
        context.pose().popPose();
    }

    private boolean isOpen() {
        return isRight ? screen.selectedRightButton == name : screen.selectedLeftButton == name;
    }

    @Override
    public void onPress() {
        super.onPress();

        if (isOpen()) {
            if (isRight) screen.selectedRightButton = "empty"; else screen.selectedLeftButton = "empty";
        } else {
            if (isRight) screen.selectedRightButton = name; else screen.selectedLeftButton = name;
        }
    }
}