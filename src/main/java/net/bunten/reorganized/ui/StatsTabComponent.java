package net.bunten.reorganized.ui;

import net.bunten.reorganized.Reorganized;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;

@Environment(value=EnvType.CLIENT)
public class StatsTabComponent extends AbstractTabComponent {

    public StatsTabComponent() {
        super(104, 71);
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
    }

    @Override
    public void render(GuiGraphics context, int mx, int my, float delta) {
        if (!isVisible()) return;
        context.pose().pushPose();
        context.pose().translate(0, 0, 100);
        int x = (width + 150) / 2 ;
        int y = (height - 175) / 2;

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x + 9, y + 14, 105, 1, imageWidth, imageHeight, 256, 96);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y, 0, 0, imageWidth, imageHeight, 256, 96);

        String msg = "Stats";
        context.drawString(client.font, msg, x + (imageWidth / 2 - (client.font.width(msg) / 2)), y + 6, 0x404040, false);

        context.pose().popPose();
    }
}