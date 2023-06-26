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
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

@Environment(value=EnvType.CLIENT)
public class ROInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {

    private final RecipeBookComponent recipeBook = new RecipeBookComponent();

    private float mouseX;
    private float mouseY;

    private boolean narrow;
    private boolean mouseDown;

    public boolean hideArmor = false;

    public String selectedLeftButton;
    public String selectedRightButton;

    private InventoryTabButton recipeTabButton;
    private InventoryTabButton craftingTabButton;
    private InventoryTabButton statsTabButton;
    private ArmorVisibilityButton armorVisibilityButton;

    public ROInventoryScreen(Player player) {
        super(player.inventoryMenu, player.getInventory(), Component.translatable(""));
        titleLabelX = 97;
        imageWidth = 176;
        imageHeight = 180;
    }

    @Override
    public void containerTick() {
        recipeBook.tick();
    }

    @Override
    protected void init() {
        super.init();

        craftingTabButton = new InventoryTabButton(this, "crafting", true, leftPos + 140, topPos + 58, (button) -> {
            Reorganized.playUi(SoundEvents.CHICKEN_HURT);
        });

        craftingTabButton.setTooltip(Tooltip.create(Component.literal("Crafting")));

        statsTabButton = new InventoryTabButton(this, "stats", true, leftPos + 140, topPos + 36, (button) -> {
            Reorganized.playUi(SoundEvents.PIG_HURT);
        });

        statsTabButton.setTooltip(Tooltip.create(Component.literal("Stats")));
        
        armorVisibilityButton = new ArmorVisibilityButton(this, leftPos + 117, topPos + 9, (b) -> hideArmor = !hideArmor);
        armorVisibilityButton.setTooltip(Tooltip.create(Component.literal(hideArmor ? "Show Armor" : "Hide Armor")));

        recipeTabButton = new InventoryTabButton(this, "recipes", false, leftPos + 16, topPos + 58, (b) -> {
            recipeBook.toggleVisibility();
            leftPos = recipeBook.updateScreenPosition(width, imageWidth);
            mouseDown = true;

            craftingTabButton.setPosition(leftPos + 140, topPos + 58);
            statsTabButton.setPosition(leftPos + 140, topPos + 36);
            armorVisibilityButton.setPosition(leftPos + 117, topPos + 9);
            recipeTabButton.setPosition(leftPos + 16, topPos + 58);
        });

        recipeTabButton.setTooltip(Tooltip.create(Component.literal("Recipe Book")));
        
        narrow = width < 379;
        recipeBook.init(width, height, minecraft, narrow, menu);
        leftPos = recipeBook.updateScreenPosition(width, imageWidth);

        addWidget(recipeBook);
        setInitialFocus(recipeBook);

        addRenderableWidget(recipeTabButton);
        addRenderableWidget(craftingTabButton);
        addRenderableWidget(statsTabButton);
        addRenderableWidget(armorVisibilityButton);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mx, int my) {
        context.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics context, int mx, int my, float delta) {
        renderBackground(context);
        
        if (recipeBook.isVisible() && narrow) {
            renderBg(context, delta, mx, my);
            recipeBook.render(context, mx, my, delta);
        } else {
            recipeBook.render(context, mx, my, delta);
            super.render(context, mx, my, delta);
            recipeBook.renderGhostRecipe(context, leftPos, topPos, false, delta);
        }

        if (recipeBook.isVisible()) {
            context.pose().pushPose();
            context.pose().translate(0, 0, 150);
            context.blit(Reorganized.id("textures/gui/inventory/main.png"), leftPos - 9, topPos + 7, 177, 86, 47, 166);
            context.pose().popPose();
        }

        renderTooltip(context, mx, my);
        recipeBook.renderTooltip(context, leftPos, topPos, mx, my);

        mouseX = mx;
        mouseY = my;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mx, int my) {
        context.blit(Reorganized.id("textures/gui/inventory/main.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
        context.blit(Reorganized.id("textures/gui/inventory/backgrounds/blank.png"), leftPos + 64, topPos + 8, 0, 0, 50, 70, 50, 70);
        ROInventoryScreen.drawEntity(context, leftPos + 90, topPos + 72, 30, (float)(leftPos + 90) - mouseX, (float)(topPos + 72 - 50) - mouseY, minecraft.player);
    }

    public static void drawEntity(GuiGraphics context, int x, int y, int size, float mx, float my, LivingEntity entity) {
        float f = (float)Math.atan(mx / 40);
        float g = (float)Math.atan(my / 40);
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
        return (!narrow || !recipeBook.isVisible()) && super.isHovering(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (recipeBook.mouseClicked(mx, my, button)) {
            setFocused(recipeBook);
            return true;
        }
        if (narrow && recipeBook.isVisible()) {
            return false;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (mouseDown) {
            mouseDown = false;
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    protected boolean hasClickedOutside(double mx, double my, int left, int top, int button) {
        boolean bl = mx < (double)left || my < (double)top || mx >= (double)(left + imageWidth) || my >= (double)(top + imageHeight);
        return recipeBook.hasClickedOutside(mx, my, leftPos, topPos, imageWidth, imageHeight, button) && bl;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType actionType) {
        super.slotClicked(slot, slotId, button, actionType);
        recipeBook.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        recipeBook.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return recipeBook;
    }
}