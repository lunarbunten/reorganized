package net.bunten.reorganized.ui;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

@Environment(value=EnvType.CLIENT)
public class ROInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {

    private float mouseX;
    private float mouseY;
    private final RecipeBookComponent recipeBook = new RecipeBookComponent();
    private boolean narrow;
    private boolean mouseDown;

    static boolean hideArmor = false;

    public ROInventoryScreen(Player player) {
        super(player.inventoryMenu, player.getInventory(), Component.translatable(""));
        this.titleLabelX = 97;
        this.imageWidth = 176;
        this.imageHeight = 180;
    }

    @Override
    public void containerTick() {
        this.recipeBook.tick();
    }

    @Override
    protected void init() {
        super.init();
        
        this.narrow = this.width < 379;
        this.recipeBook.init(this.width, this.height, this.minecraft, this.narrow, this.menu);
        this.leftPos = this.recipeBook.updateScreenPosition(this.width, this.imageWidth);

        this.addWidget(this.recipeBook);
        this.setInitialFocus(this.recipeBook);

        addRenderableWidget(new InventoryTabButton("recipes", false, leftPos + 16, topPos + 8, Component.literal("um"), (button) -> {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.COW_DEATH, 1));
        }));

        addRenderableWidget(new InventoryTabButton("crafting", true, leftPos + 140, topPos + 8, Component.literal("um"), (button) -> {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.CHICKEN_HURT, 1));  
        }));

        addRenderableWidget(new InventoryTabButton("stats", true, leftPos + 140, topPos + 30, Component.literal("um"), (button) -> {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PIG_HURT, 1)); 
        }));

        addRenderableWidget(new ArmorVisibilityButton(leftPos + 117, topPos + 9));
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        context.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        if (this.recipeBook.isVisible() && this.narrow) {
            this.renderBg(context, delta, mouseX, mouseY);
            this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            this.recipeBook.render(context, mouseX, mouseY, delta);
            super.render(context, mouseX, mouseY, delta);
            this.recipeBook.renderGhostRecipe(context, this.leftPos, this.topPos, false, delta);
        }
        this.renderTooltip(context, mouseX, mouseY);
        this.recipeBook.renderTooltip(context, this.leftPos, this.topPos, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        context.blit(Reorganized.id("textures/gui/inventory/main.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
        context.blit(Reorganized.id("textures/gui/inventory/backgrounds/blank.png"), leftPos + 64, topPos + 8, 0, 0, 50, 70, 50, 70);
        ROInventoryScreen.drawEntity(context, i + 90, j + 72, 30, (float)(i + 90) - this.mouseX, (float)(j + 72 - 50) - this.mouseY, this.minecraft.player);
    }

    public static void drawEntity(GuiGraphics context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)Math.atan(mouseX / 40);
        float g = (float)Math.atan(mouseY / 40);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(g * 20 * ((float)Math.PI / 180));
        quaternionf.mul(quaternionf2);
        float h = entity.yBodyRot;
        float i = entity.getYRot();
        float j = entity.getXRot();
        float k = entity.yHeadRotO;
        float l = entity.yHeadRot;
        entity.yBodyRot = 180 + f * 20;
        entity.setYRot(180 + f * 40);
        entity.setXRot(-g * 20);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        ROInventoryScreen.drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
        entity.yBodyRot = h;
        entity.setYRot(i);
        entity.setXRot(j);
        entity.yHeadRotO = k;
        entity.yHeadRot = l;
    }

    public static void drawEntity(GuiGraphics context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        context.pose().pushPose();
        context.pose().translate(x, y, 50.0);
        context.pose().mulPoseMatrix(new Matrix4f().scaling(size, size, -size));
        context.pose().mulPose(quaternionf);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            dispatcher.overrideCameraOrientation(quaternionf2);
        }
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0, 0.0, 0.0, 0, 1, context.pose(), context.bufferSource(), 0xF000F0));
        context.flush();
        dispatcher.setRenderShadow(true);
        context.pose().popPose();
        Lighting.setupFor3DItems();
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isVisible()) && super.isHovering(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        }
        if (this.narrow && this.recipeBook.isVisible()) {
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
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.imageWidth) || mouseY >= (double)(top + this.imageHeight);
        return this.recipeBook.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, button) && bl;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType actionType) {
        super.slotClicked(slot, slotId, button, actionType);
        this.recipeBook.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBook.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBook;
    }
}