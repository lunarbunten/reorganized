package net.bunten.reorganized.ui;

import org.jetbrains.annotations.Nullable;

import net.bunten.reorganized.Reorganized;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.world.inventory.Slot;

@Environment(value=EnvType.CLIENT)
public class CraftingTabComponent implements Renderable, GuiEventListener, NarratableEntry {

    private int width;
    private int height;

    private int leftPos;
    private int topPos;

    public CraftingTabComponent(int leftPos, int topPos) {
        this.width = 104;
        this.height = 71;
        this.leftPos = leftPos;
        this.topPos = topPos;
    }

    private boolean visible;

    public void toggleVisibility() {
        setVisible(!isVisible());
    }

    private void setVisible(boolean value) {
        visible = value;
    }

    public boolean isVisible() {
        return visible;
    }

    public void slotClicked(@Nullable Slot slot) {
        
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
        
    }

    @Override
    public NarrationPriority narrationPriority() {
        return visible ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public void setFocused(boolean value) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void render(GuiGraphics context, int mx, int my, float delta) {
        if (!isVisible()) return;
        context.pose().pushPose();
        context.pose().translate(0, 0, 100);
        int k = leftPos;
        int l = topPos;
        context.blit(Reorganized.id("textures/gui/inventory/crafting.png"), k, l, 0, 0, width, height, 128, 96);
        context.pose().popPose();
    }
}