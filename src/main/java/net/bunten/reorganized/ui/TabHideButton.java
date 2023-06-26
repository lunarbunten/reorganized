package net.bunten.reorganized.ui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class TabHideButton extends Button {

    public TabHideButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.literal("PLACEHOLDER"), onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.pose().pushPose();
        context.setColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.pose().translate(0, 0, 200);
        context.blit(Reorganized.id("textures/gui/inventory/close.png"), getX(), getY(), isHovered() ? width : 0, 0, width, height);
        context.pose().popPose();
    }
}