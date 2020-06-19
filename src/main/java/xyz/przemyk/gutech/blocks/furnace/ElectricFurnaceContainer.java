package xyz.przemyk.gutech.blocks.furnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.przemyk.gutech.OutputSlotItemHandler;
import xyz.przemyk.gutech.blocks.AbstractMachineContainer;
import xyz.przemyk.gutech.setup.ModBlocks;
import xyz.przemyk.gutech.setup.ModContainers;

public class ElectricFurnaceContainer extends AbstractMachineContainer {

    public ElectricFurnaceContainer(int id, World worldIn, BlockPos blockPosIn, PlayerInventory playerInventoryIn) {
        super(ModContainers.ELECTRIC_FURNACE.get(), id, worldIn, blockPosIn, playerInventoryIn);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, 0, 56, 35));
            addSlot(new OutputSlotItemHandler(itemHandler, 1, 116, 35));
        });

        trackInt(new IntReferenceHolder() {
            @Override
            public int get() {
                return getCookTime();
            }

            @Override
            public void set(int value) {
                ((ElectricFurnaceTileEntity) tileEntity).cookTime = value;
            }
        });
    }

    public int getCookTime() {
        return ((ElectricFurnaceTileEntity) tileEntity).cookTime;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn, ModBlocks.ELECTRIC_FURNACE.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 0) {
                if (!mergeItemStack(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (!mergeItemStack(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                } else if (index < 28) {
                    if (!mergeItemStack(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !mergeItemStack(stack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }
}
