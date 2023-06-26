package net.bunten.reorganized.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {
    @Accessor
    RecipeBookTabButton getSelectedTab();
}
