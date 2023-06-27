package net.bunten.reorganized.ui;

import com.ibm.icu.text.DecimalFormat;

import net.bunten.reorganized.Reorganized;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;

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

        int x = (width + 150) / 2;
        int y = (height - 175) / 2 + 3;

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x + 9, y + 17, 105, 1, imageWidth, imageHeight, 256, 96);

        renderStats(context, mx, my, delta);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y, 0, 0, imageWidth, imageHeight, 256, 96);

        String msg = "Stats";
        context.drawString(client.font, msg, x + (imageWidth / 2 - (client.font.width(msg) / 2)), y + 6, 0x404040, false);

        context.pose().popPose();
    }

    protected void renderStats(GuiGraphics context, int mx, int my, float delta) {
        int ix = (width + 150) / 2 + 11;
        int iy = (height - 175) / 2 + 22;
        int ity = iy + 1;

        int x = ix;
        int y = iy;
        int ty = ity;

        int u = 0;
        int dif = 11;

        LocalPlayer player = client.player;
        if (player == null) return;
        
        DecimalFormat format = new DecimalFormat("#");

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y, u, 71, 9, 9, 256, 96);
        context.drawString(client.font, format.format(player.getHealth()), x + 12, ty, 0xAAAAAA, false);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y += dif, u += 9, 71, 9, 9, 256, 96);
        context.drawString(client.font, format.format(player.getAbsorptionAmount()), x + 12, ty += dif, 0xAAAAAA, false);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y += dif, u += 9, 71, 9, 9, 256, 96);
        context.drawString(client.font, format.format(player.getFoodData().getFoodLevel()), x + 12, ty += dif, 0xAAAAAA, false);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y += dif, u += 9, 71, 9, 9, 256, 96);
        context.drawString(client.font, format.format(Mth.clamp(player.getAirSupply() / 5, 0, 300)), x + 12, ty += dif, 0xAAAAAA, false);

        x = ix + 40;
        y = iy;
        ty = ity;

        int levels = Mth.clamp(player.experienceLevel, 0, 999);
        String xp = levels + (player.experienceLevel > 999 ? "+" : "");

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y, u += 9, 71, 9, 9, 256, 96);
        context.drawString(client.font, format.format(player.getAttributeValue(Attributes.ARMOR)), x + 12, ty, 0xAAAAAA, false);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y += dif, u += 9, 71, 9, 9, 256, 96);
        context.drawString(client.font, format.format(player.getAttributeValue(Attributes.ARMOR_TOUGHNESS)), x + 12, ty += dif, 0xAAAAAA, false);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y += dif, u += 9, 71, 9, 9, 256, 96);
        context.drawString(client.font, format.format(player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), x + 12, ty += dif, 0xAAAAAA, false);

        context.blit(Reorganized.id("textures/gui/inventory/stats.png"), x, y += dif, u += 9, 71, 9, 9, 256, 96);
        context.drawString(client.font, xp, x + 12, ty += dif, 0xAAAAAA, false);
    }
}