package net.bunten.reorganized.mixin;

import net.bunten.reorganized.ROScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;playerScreenHandler:Lnet/minecraft/screen/PlayerScreenHandler;", opcode = Opcodes.GETFIELD))
    private PlayerScreenHandler getScreenHandler(PlayerEntity instance) {
        return new ROScreenHandler(instance.getInventory(), !instance.getWorld().isClient, instance);
    }

}
