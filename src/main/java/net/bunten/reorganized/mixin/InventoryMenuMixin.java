package net.bunten.reorganized.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<CraftingContainer> {

    public InventoryMenuMixin(MenuType<?> type, int i) {
        super(type, i);
    }

    @Shadow
    @Final
    private CraftingContainer craftSlots;

    @Shadow
    @Final
    private ResultContainer resultSlots;

    @Shadow
    @Final
    private static EquipmentSlot[] SLOT_IDS;

    @Shadow
    @Final
    static ResourceLocation[] TEXTURE_EMPTY_SLOTS;

    @Shadow
    @Final
    public static ResourceLocation BLOCK_ATLAS;

    @Shadow
    @Final
    public static ResourceLocation EMPTY_ARMOR_SLOT_SHIELD;

    @Shadow
    static void onEquipItem(Player player, EquipmentSlot slot, ItemStack newStack, ItemStack currentStack) {}

    @Inject(at = @At("TAIL"), method = "<init>")
	private void init(Inventory inventory, boolean onServer, final Player player, CallbackInfo info) {
        slots.clear();

        int i;

        addSlot(new ResultSlot(inventory.player, craftSlots, resultSlots, 0, 154 + 81, 35));

        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                addSlot(new Slot(craftSlots, j + i * 2, 98 + j * 18 + 81, 25 + i * 18));
            }
        }

        // Armor Slots

        for (i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentSlot = SLOT_IDS[i];
            addSlot(new Slot(inventory, 39 - i, 8 + 36, 8 + i * 18){

                @Override
                public void setByPlayer(ItemStack stack) {
                    onEquipItem(player, equipmentSlot, stack, getItem());
                    super.setByPlayer(stack);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return equipmentSlot == Mob.getEquipmentSlotForItem(stack);
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack itemStack = getItem();
                    if (!itemStack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.mayPickup(player);
                }

                @Override
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentSlot.getIndex()]);
                }
            });
        }

        // Inventory

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + (18 * j), 87 + (18 * i)));
            }
        }

        // Hotbar

        for (i = 0; i < 9; ++i) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 145));
        }
        
        // Offhand Slot

        addSlot(new Slot(inventory, 40, 118, 62){

            @Override
            public void setByPlayer(ItemStack stack) {
                onEquipItem(player, EquipmentSlot.OFFHAND, stack, getItem());
                super.setByPlayer(stack);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(BLOCK_ATLAS, EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }
}