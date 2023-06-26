package net.bunten.reorganized.ui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ArmorVisibilityButton extends Button {

    private ROInventoryScreen screen;

    public ArmorVisibilityButton(ROInventoryScreen screen, int x, int y, OnPress onPress) {
        super(x, y, 18, 18, Component.literal("PLACEHOLDER"), onPress, DEFAULT_NARRATION);
        this.screen = screen;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.setColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int u = 1 + (screen.hideArmor ? 36 : 0) + (isHovered() ? 18 : 0);
        
        context.blit(Reorganized.id("textures/gui/inventory/main.png"), getX(), getY(), u, 237, width, height);
        context.blit(Reorganized.id("textures/gui/inventory/main.png"), getX(), getY(), screen.hideArmor ? 19 : 1, 219 - (screen.hideArmor ? 1 : 0), width, height);
    }
}