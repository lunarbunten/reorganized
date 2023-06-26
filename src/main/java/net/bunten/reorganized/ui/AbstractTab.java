package net.bunten.reorganized.ui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public abstract class AbstractTab extends AbstractWidget {

    public AbstractTab(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }
    
}