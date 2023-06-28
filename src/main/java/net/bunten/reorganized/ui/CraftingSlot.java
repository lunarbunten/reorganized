package net.bunten.reorganized.ui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class CraftingSlot extends Slot {

    public CraftingSlot(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }
    
    @Override
    public boolean isActive() {
        return super.isActive();
    }
}