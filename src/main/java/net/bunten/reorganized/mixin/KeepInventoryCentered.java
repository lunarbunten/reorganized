package net.bunten.reorganized.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

@Mixin(RecipeBookComponent.class)
public class KeepInventoryCentered {

    @Shadow private int xOffset;
    @Shadow private boolean widthTooNarrow;

    @Inject(
            method = "initVisuals",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;xOffset:I")
    )
    public void center(CallbackInfo ci) {
        this.xOffset = this.widthTooNarrow ? 0 : 162;
    }

    @Inject(method = "updateScreenPosition", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "RETURN"), cancellable = true)
    public void findLeftEdge(int width, int backgroundWidth, CallbackInfoReturnable<Integer> cir, int j) {
        j = (width - backgroundWidth) / 2;
        cir.setReturnValue(j);
    }

    @Inject(method = "isOffsetNextToMainGUI", at = @At("RETURN"), cancellable = true)
    public void isWide(CallbackInfoReturnable<Boolean> cir) {
        if (this.xOffset == 162 || this.xOffset == 86) {
            cir.setReturnValue(true);
        }
    }
}