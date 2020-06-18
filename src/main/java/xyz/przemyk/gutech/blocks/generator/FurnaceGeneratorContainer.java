package xyz.przemyk.gutech.blocks.generator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xyz.przemyk.gutech.SerializableEnergyStorage;
import xyz.przemyk.gutech.setup.ModBlocks;
import xyz.przemyk.gutech.setup.ModContainers;

public class FurnaceGeneratorContainer extends Container {

    private final TileEntity tileEntity;

    public FurnaceGeneratorContainer(int id, World worldIn, BlockPos blockPosIn, PlayerInventory playerInventoryIn) {
        super(ModContainers.FURNACE_GENERATOR.get(), id);
        tileEntity = worldIn.getTileEntity(blockPosIn);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h, 0, 80, 62)));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
        }

        trackIntArray(new IIntArray() {
            @Override
            public int get(int index) {
                switch (index) {
                    case 0:
                        return getEnergy();
                    case 1:
                        return getBurnTime();
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> ((SerializableEnergyStorage) h).setEnergy(value));
                        break;
                    case 1:
                        ((FurnaceGeneratorTileEntity) tileEntity).burnTime = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        });
    }

    public int getEnergy() {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public boolean isBurning() {
        return ((FurnaceGeneratorTileEntity) tileEntity).burnTime > 0;
    }

    public int getBurnTime() {
        return ((FurnaceGeneratorTileEntity) tileEntity).burnTime;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos()), playerIn, ModBlocks.FURNACE_GENERATOR.get());
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
                if (ForgeHooks.getBurnTime(stack) > 0) {
                    if (!mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
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
