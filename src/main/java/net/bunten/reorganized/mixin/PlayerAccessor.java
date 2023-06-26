package net.bunten.reorganized.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface PlayerAccessor {

    @Accessor("playerScreenHandler")
    void setScreenHandler(PlayerScreenHandler playerScreenHandler);

}
