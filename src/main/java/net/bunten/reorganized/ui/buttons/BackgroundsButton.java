package net.bunten.reorganized.ui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
import net.bunten.reorganized.ui.ROInventoryScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BackgroundsButton extends Button {

    private ROInventoryScreen screen;

    public BackgroundsButton(ROInventoryScreen screen, int x, int y, OnPress onPress) {
        super(x, y, 18, 18, Component.literal("PLACEHOLDER"), onPress, DEFAULT_NARRATION);
        this.screen = screen;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.setColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int u = 45 + (isHovered() ? width : 0);
        
        context.blit(Reorganized.id("textures/gui/inventory/buttons.png"), getX(), getY(), u, 0, width, height);
        context.blit(Reorganized.id("textures/gui/inventory/icons/backgrounds.png"), getX() + 1, getY() + 1 + (isHovered() ? 2 : 0), 0, 0, 16, 16, 16, 16);
    }
}