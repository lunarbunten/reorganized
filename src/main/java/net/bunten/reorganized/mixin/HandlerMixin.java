package net.bunten.reorganized.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.util.Pair;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

@Mixin(PlayerScreenHandler.class)
public abstract class HandlerMixin extends AbstractRecipeScreenHandler<RecipeInputInventory> {

    @Shadow
    @Final
    private RecipeInputInventory craftingInput;

    @Shadow
    @Final
    private CraftingResultInventory craftingResult;

    @Shadow
    @Final
    private static EquipmentSlot[] EQUIPMENT_SLOT_ORDER;

    @Shadow
    @Final
    static Identifier[] EMPTY_ARMOR_SLOT_TEXTURES;

    @Shadow
    @Final
    public static Identifier BLOCK_ATLAS_TEXTURE;

    @Shadow
    @Final
    public static Identifier EMPTY_OFFHAND_ARMOR_SLOT;

    @Shadow
    static void onEquipStack(PlayerEntity player, EquipmentSlot slot, ItemStack newStack, ItemStack currentStack) {}

    public HandlerMixin(ScreenHandlerType<?> type, int i) {
        super(type, i);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
	private void init(PlayerInventory inventory, boolean onServer, final PlayerEntity player, CallbackInfo info) {
        slots.clear();

        int i;

        addSlot(new CraftingResultSlot(inventory.player, craftingInput, craftingResult, 0, 154, 28));

        for (i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                addSlot(new Slot(craftingInput, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        addInventorySlots(i, inventory, player);
	}

    private void addInventorySlots(int i, PlayerInventory inventory, PlayerEntity player) {

        // Armor Slots

        for (i = 0; i < 4; ++i) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
            addSlot(new Slot(inventory, 39 - i, 8 + 36, 8 + i * 18){

                @Override
                public void setStack(ItemStack stack) {
                    onEquipStack(player, equipmentSlot, stack, getStack());
                    super.setStack(stack);
                }

                @Override
                public int getMaxItemCount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
                }

                @Override
                public boolean canTakeItems(PlayerEntity player) {
                    ItemStack itemStack = getStack();
                    if (!itemStack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
                        return false;
                    }
                    return super.canTakeItems(player);
                }

                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                }
            });
        }

        // Inventory

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + (18 * j), 98 + (18 * i)));
            }
        }

        // Hotbar

        for (i = 0; i < 9; ++i) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142 + 14));
        }
        
        // Offhand Slot

        addSlot(new Slot(inventory, 40, 118, 62){

            @Override
            public void setStack(ItemStack stack) {
                onEquipStack(player, EquipmentSlot.OFFHAND, stack, getStack());
                super.setStack(stack);
            }

            @Override
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }
}