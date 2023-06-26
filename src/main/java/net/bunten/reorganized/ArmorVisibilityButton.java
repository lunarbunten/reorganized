package net.bunten.reorganized;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ArmorVisibilityButton extends ButtonWidget {

    public ArmorVisibilityButton(int x, int y) {
        super(x, y, 18, 18, Text.literal("PLACEHOLDER"), (button) -> {}, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void onPress() {
        ROInventoryScreen.hideArmor = !ROInventoryScreen.hideArmor;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.setShaderColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawTexture(Reorganized.id("textures/gui/inventory/main.png"), getX(), getY(), 177 + (ROInventoryScreen.hideArmor ? 18 : 0), 0, width, height);
    }
}