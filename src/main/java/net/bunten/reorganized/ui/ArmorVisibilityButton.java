package net.bunten.reorganized.ui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ArmorVisibilityButton extends Button {

    public ArmorVisibilityButton(int x, int y) {
        super(x, y, 18, 18, Component.literal("PLACEHOLDER"), (button) -> {}, DEFAULT_NARRATION);
    }

    @Override
    public void onPress() {
        ROInventoryScreen.hideArmor = !ROInventoryScreen.hideArmor;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.setColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.blit(Reorganized.id("textures/gui/inventory/main.png"), getX(), getY(), 177 + (ROInventoryScreen.hideArmor ? 18 : 0), 0, width, height);
    }
}