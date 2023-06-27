package net.bunten.reorganized.ui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.reorganized.Reorganized;
import net.bunten.reorganized.mixin.RecipeBookComponentAccessor;
import net.bunten.reorganized.ui.buttons.ArmorVisibilityButton;
import net.bunten.reorganized.ui.buttons.InventoryTabButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

@Environment(value=EnvType.CLIENT)
public class ROInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {

    private final List<InventoryTabButton> tabButtons = new ArrayList<>();
    private final List<InventoryTabButton> leftSideTabButtons = new ArrayList<>();
    private final List<InventoryTabButton> rightSideTabButtons = new ArrayList<>();

    private final <T extends GuiEventListener & NarratableEntry> List<T> getTabWidgets() {
        return new ArrayList<>();
    }

    private float mouseX;
    private float mouseY;

    private boolean narrow;
    private boolean mouseDown;

    public static boolean hideArmor = false;

    public InventoryTabButton selectedLeftButton;
    public InventoryTabButton selectedRightButton;

    private InventoryTabButton recipeTabButton;
    private final RecipeBookComponent recipeTab = new RecipeBookComponent();

    private InventoryTabButton craftingTabButton;
    private final CraftingTabComponent craftingTab = new CraftingTabComponent();

    private InventoryTabButton statsTabButton;
    private final StatsTabComponent statsTab = new StatsTabComponent();

    private ArmorVisibilityButton armorVisibilityButton;

    public ROInventoryScreen(Player player) {
        super(player.inventoryMenu, player.getInventory(), Component.translatable(""));
        titleLabelX = 97;
        imageWidth = 176;
        imageHeight = 169;
    }

    private void disableOldTabs() {
        if (recipeTab.isVisible() && selectedLeftButton != recipeTabButton) recipeTab.toggleVisibility();
        if (craftingTab.isVisible() && selectedRightButton != craftingTabButton) craftingTab.toggleVisibility();
        if (statsTab.isVisible() && selectedRightButton != statsTabButton) statsTab.toggleVisibility();
    }

    @Override
    public void containerTick() {
        recipeTab.tick();
        disableOldTabs();
    }

    private void initButtons() {
        craftingTabButton = new InventoryTabButton(this, "crafting", true, leftPos + 140, topPos + 58, (button) -> {
            craftingTab.toggleVisibility();
            mouseDown = true;
        });

        craftingTabButton.setTooltip(Tooltip.create(Component.literal("Crafting")));

        statsTabButton = new InventoryTabButton(this, "stats", true, leftPos + 140, topPos + 36, (button) -> {
            statsTab.toggleVisibility();
            mouseDown = true;
        });

        statsTabButton.setTooltip(Tooltip.create(Component.literal("Stats")));

        recipeTabButton = new InventoryTabButton(this, "recipes", false, leftPos + 16, topPos + 58, (b) -> {
            recipeTab.toggleVisibility();
            mouseDown = true;
        });

        recipeTabButton.setTooltip(Tooltip.create(Component.literal("Recipe Book")));    
    }

    @Override
    protected void init() {
        super.init();

        initButtons();

        narrow = width < 379;

        recipeTab.init(width, height, minecraft, narrow, menu);
        craftingTab.init(width, height, minecraft);
        statsTab.init(width, height, minecraft);
        if (recipeTab.isVisible() && selectedLeftButton == null) selectedLeftButton = recipeTabButton;

        tabButtons.add(recipeTabButton);
        getTabWidgets().add(recipeTab);
        tabButtons.add(craftingTabButton);
        getTabWidgets().add(craftingTab);
        tabButtons.add(statsTabButton);
        getTabWidgets().add(statsTab);

        tabButtons.forEach((button) -> {
            if (button.isRightSided()) rightSideTabButtons.add(button); else leftSideTabButtons.add(button);
            addRenderableWidget(button);
        });

        getTabWidgets().forEach((widget) -> addWidget(widget));

        armorVisibilityButton = new ArmorVisibilityButton(this, leftPos + 117, topPos + 9, (b) -> hideArmor = !hideArmor);
        armorVisibilityButton.setTooltip(Tooltip.create(Component.literal(hideArmor ? "Show Armor" : "Hide Armor")));

        addRenderableWidget(armorVisibilityButton);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mx, int my) {
        context.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics context, int mx, int my, float delta) {
        if (craftingTab.isVisible()) craftingTab.render(context, mx, my, delta);
        if (statsTab.isVisible()) statsTab.render(context, mx, my, delta);
        renderBackground(context);
        
        if (recipeTab.isVisible() && narrow) {
            renderBg(context, delta, mx, my);
            recipeTab.render(context, mx, my, delta);
        } else {
            recipeTab.render(context, mx, my, delta);
            super.render(context, mx, my, delta);
            recipeTab.renderGhostRecipe(context, leftPos, topPos, false, delta);
        }

        if (recipeTab.isVisible()) {
            context.pose().pushPose();
            context.pose().translate(0, 0, 150);
            context.blit(Reorganized.id("textures/gui/inventory/main.png"), leftPos - 9, topPos + 7, 177, 86, 47, 166);
            context.pose().popPose();
        }

        renderTooltip(context, mx, my);
        recipeTab.renderTooltip(context, leftPos, topPos, mx, my);

        mouseX = mx;
        mouseY = my;
    }

    public ResourceLocation getPlayerBackgroundTexture() {
        return Reorganized.id("textures/gui/inventory/backgrounds/blank.png");
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mx, int my) {
        context.blit(Reorganized.id("textures/gui/inventory/main.png"), leftPos, topPos, 0, 0, imageWidth, imageHeight);
        context.blit(getPlayerBackgroundTexture(), leftPos + 64, topPos + 8, 0, 0, 50, 70, 50, 70);
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
        return (!narrow || !recipeTab.isVisible()) && super.isHovering(x, y, width, height, pointX, pointY);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (recipeTab.mouseClicked(mx, my, button)) {
            setFocused(recipeTab);
            return true;
        }
        if (narrow && recipeTab.isVisible()) {
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
    protected boolean hasClickedOutside(double cursorX, double cursorY, int screenLeft, int screenTop, int button) {
        boolean recipeBookSize;
        boolean tabSize;

        boolean inventoryTop = cursorX < (double)(screenLeft + 37) || cursorY < (double)screenTop || cursorX >= (double)((screenLeft - 37) + imageWidth) || cursorY >= (double)(screenTop + imageHeight);
        boolean inventoryBottom = cursorX < (double)screenLeft || cursorY < (double)(screenTop + 80) || cursorX >= (double)(screenLeft + imageWidth) || cursorY >= (double)(screenTop + imageHeight);

        if (recipeTab.isVisible()) {
            boolean bl2 = (double)(leftPos - 147) < cursorX && cursorX < (double)leftPos && (double)topPos < cursorY && cursorY < (double)(topPos + imageHeight);
            recipeBookSize = !bl2 && !((RecipeBookComponentAccessor)recipeTab).getSelectedTab().isHoveredOrFocused();
        } else recipeBookSize = true;

        if (craftingTab.isVisible() || statsTab.isVisible()) {
            tabSize = cursorX < (double)(screenLeft + 163) || cursorY < (double)(screenTop + 3) || cursorX >= (double)((screenLeft + 92) + imageWidth) || cursorY >= (double)((screenTop - 106) + imageHeight);
        } else tabSize = true;

        return tabSize && recipeBookSize && inventoryTop && inventoryBottom;
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType actionType) {
        super.slotClicked(slot, slotId, button, actionType);
        recipeTab.slotClicked(slot);
        craftingTab.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        recipeTab.recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return recipeTab;
    }
}