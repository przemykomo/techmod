package xyz.przemyk.gutech.blocks.generator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerProvider;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xyz.przemyk.gutech.PrzemekTechMod;
import xyz.przemyk.gutech.SerializableEnergyStorage;
import xyz.przemyk.gutech.setup.ModTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FurnaceGeneratorTileEntity extends TileEntity implements ITickableTileEntity, IContainerProvider, INamedContainerProvider {

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(this::createItemHandler);
    private final LazyOptional<SerializableEnergyStorage> energyStorage = LazyOptional.of(this::createEnergyStorage);

    private IItemHandler createItemHandler() {
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

    private SerializableEnergyStorage createEnergyStorage() {
        return new SerializableEnergyStorage(10000, 20);
    }

    public FurnaceGeneratorTileEntity() {
        super(ModTileEntities.FURNACE_GENERATOR.get());
    }

    public int burnTime;

    @Override
    public void tick() {
        if (burnTime > 0) {
            --burnTime;
            energyStorage.ifPresent(energy -> energy.receiveEnergy(20, false));
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
                            world.setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.LIT, true));
                        }
                    });
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void read(CompoundNBT compound) {
        itemHandler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(compound.getCompound("inv")));
        energyStorage.ifPresent(energy -> energy.deserializeNBT(compound.getCompound("energy")));
        burnTime = compound.getInt("burnTime");
        super.read(compound);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        itemHandler.ifPresent(h -> compound.put("inv", ((INBTSerializable<CompoundNBT>) h).serializeNBT()));
        energyStorage.ifPresent(energy -> compound.put("energy", energy.serializeNBT()));
        compound.putInt("burnTime", burnTime);
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }

        if (cap == CapabilityEnergy.ENERGY) {
            return energyStorage.cast();
        }

        return super.getCapability(cap, side);
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
