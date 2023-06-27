package net.bunten.reorganized.ui;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.world.inventory.Slot;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractTabComponent implements Renderable, GuiEventListener, NarratableEntry {

    protected boolean visible;

    protected int imageWidth;
    protected int imageHeight;

    protected int width;
    protected int height;

    protected Minecraft client;

    public AbstractTabComponent(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public void init(int width, int height, Minecraft client) {
        this.client = client;
        this.width = width;
        this.height = height;
    }

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
}