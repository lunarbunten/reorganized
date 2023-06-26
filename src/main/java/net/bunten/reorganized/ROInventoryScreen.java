package net.bunten.reorganized;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ROInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    private float mouseX;
    private float mouseY;
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();
    private boolean narrow;
    private boolean mouseDown;

    static boolean hideArmor = false;

    public ROInventoryScreen(PlayerEntity player) {
        super(player.playerScreenHandler, player.getInventory(), Text.translatable(""));
        this.titleX = 97;
        this.backgroundWidth = 176;
        this.backgroundHeight = 180;
    }

    @Override
    public void handledScreenTick() {
        this.recipeBook.update();
    }

    @Override
    protected void init() {
        super.init();
        
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
        this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);

        this.addSelectableChild(this.recipeBook);
        this.setInitialFocus(this.recipeBook);

        addDrawableChild(new TabButton("recipes", false, x + 16, y + 8, Text.literal("um"), (button) -> {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_COW_DEATH, 1));
        }));

        addDrawableChild(new TabButton("crafting", true, x + 140, y + 8, Text.literal("um"), (button) -> {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_CHICKEN_HURT, 1));  
        }));

        addDrawableChild(new TabButton("stats", true, x + 140, y + 30, Text.literal("um"), (button) -> {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PIG_HURT, 1)); 
        }));

        addDrawableChild(new ArmorVisibilityButton(x + 117, y + 9));
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(context, delta, mouseX, mouseY);
            this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            this.recipeBook.render(context, mouseX, mouseY, delta);
            super.render(context, mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(context, this.x, this.y, false, delta);
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(Reorganized.id("textures/gui/inventory/main.png"), x, y, 0, 0, backgroundWidth, backgroundHeight);
        context.drawTexture(Reorganized.id("textures/gui/inventory/backgrounds/blank.png"), x + 64, y + 8, 0, 0, 50, 70, 50, 70);
        ROInventoryScreen.drawEntity(context, i + 90, j + 72, 30, (float)(i + 90) - this.mouseX, (float)(j + 72 - 50) - this.mouseY, this.client.player);
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan(mouseX / 40);
        float g = (float)Math.atan(mouseY / 40);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(g * 20 * ((float)Math.PI / 180));
        quaternionf.mul(quaternionf2);
        float h = entity.bodyYaw;
        float i = entity.getYaw();
        float j = entity.getPitch();
        float k = entity.prevHeadYaw;
        float l = entity.headYaw;
        entity.bodyYaw = 180 + f * 20;
        entity.setYaw(180 + f * 40);
        entity.setPitch(-g * 20);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        ROInventoryScreen.drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.bodyYaw = h;
        entity.setYaw(i);
        entity.setPitch(j);
        entity.prevHeadYaw = k;
        entity.headYaw = l;
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 50.0);
        context.getMatrices().multiplyPositionMatrix(new Matrix4f().scaling(size, size, -size));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            dispatcher.setRotation(quaternionf2);
        }
        dispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0, 0.0, 0.0, 0, 1, context.getMatrices(), context.getVertexConsumers(), 0xF000F0));
        context.draw();
        dispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        }
        if (this.narrow && this.recipeBook.isOpen()) {
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}