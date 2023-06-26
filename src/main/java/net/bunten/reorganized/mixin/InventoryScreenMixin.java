package net.bunten.reorganized.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.bunten.reorganized.ui.ROInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

	public InventoryScreenMixin(InventoryMenu menu, Inventory inventory, Component text) {
		super(menu, inventory, text);
	}

	@Inject(at = @At("HEAD"), method = "init", cancellable = true)
	private void init(CallbackInfo info) {
		if (!minecraft.gameMode.hasInfiniteItems()) info.cancel();
		minecraft.setScreen(new ROInventoryScreen(minecraft.player));
	}
}