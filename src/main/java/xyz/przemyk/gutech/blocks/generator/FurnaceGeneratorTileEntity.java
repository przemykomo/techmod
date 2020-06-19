package xyz.przemyk.gutech.blocks.generator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xyz.przemyk.gutech.PrzemekTechMod;
import xyz.przemyk.gutech.SerializableEnergyStorage;
import xyz.przemyk.gutech.blocks.AbstractMachineTileEntity;
import xyz.przemyk.gutech.setup.ModTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceGeneratorTileEntity extends AbstractMachineTileEntity {

    public FurnaceGeneratorTileEntity() {
        super(ModTileEntities.FURNACE_GENERATOR.get(),
                LazyOptional.of(FurnaceGeneratorTileEntity::createItemHandler),
                LazyOptional.of(FurnaceGeneratorTileEntity::createEnergyStorage));
    }

    private static IItemHandler createItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack) > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (ForgeHooks.getBurnTime(stack) <= 0) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private static SerializableEnergyStorage createEnergyStorage() {
        return new SerializableEnergyStorage(10000, 0, 20);
    }

    public int burnTime;

    @Override
    public void tick() {
        if (burnTime > 0) {
            --burnTime;
            energyStorage.ifPresent(energy -> energy.addEnergy(20));
            if (burnTime == 0 && !world.isRemote) {
                world.setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.LIT, false));
            }
        } else {
            energyStorage.ifPresent(energy -> {
                if (energy.getEnergyStored() != energy.getMaxEnergyStored()) {
                    itemHandler.ifPresent(h -> {
                        burnTime = ForgeHooks.getBurnTime(h.getStackInSlot(0));
                        if (burnTime > 0) {
                            h.extractItem(0, 1, false);
                            if (!world.isRemote) {
                                world.setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.LIT, true));
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        burnTime = compound.getInt("burnTime");
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("burnTime", burnTime);
        return super.write(compound);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new FurnaceGeneratorContainer(id, world, pos, playerInventory);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(PrzemekTechMod.MODID + ".container.furnace_generator");
    }
}
