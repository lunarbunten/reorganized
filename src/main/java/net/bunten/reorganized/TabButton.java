package net.bunten.reorganized;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TabButton extends ButtonWidget {
    
    private boolean isRight;
    private String name;

    public TabButton(String name, boolean isRight, int x, int y, Text message, PressAction onPress) {
        super(x, y, 22, 20, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.isRight = isRight;
        this.name = name;
    }
    
    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.setShaderColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        boolean bl = isHovered();

        context.drawTexture(Reorganized.id("textures/gui/inventory/main.png"), getX(), getY(), 177 + (isRight ? 23 : 0), 44 + (bl ? 21 : 0), width, height);
        context.drawTexture(Reorganized.id("textures/gui/inventory/tabs/" + name + ".png"), getX() + (isRight ? 0 : 5) + (bl ? isRight ? 2 : -2 : 0), getY() + 2, 0, 0, 16, 16, 16, 16);
    }
}