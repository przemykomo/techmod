package xyz.przemyk.gutech.blocks;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import xyz.przemyk.gutech.SerializableEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractTechTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
    protected final LazyOptional<IItemHandler> itemHandler;
    protected final LazyOptional<SerializableEnergyStorage> energyStorage;

    public AbstractTechTileEntity(TileEntityType<?> tileEntityTypeIn, LazyOptional<IItemHandler> itemHandler, LazyOptional<SerializableEnergyStorage> energyStorage) {
        super(tileEntityTypeIn);
        this.itemHandler = itemHandler;
        this.energyStorage = energyStorage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void read(CompoundNBT compound) {
        itemHandler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(compound.getCompound("inv")));
        energyStorage.ifPresent(energy -> energy.deserializeNBT(compound.getCompound("energy")));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        itemHandler.ifPresent(h -> compound.put("inv", ((INBTSerializable<CompoundNBT>) h).serializeNBT()));
        energyStorage.ifPresent(energy -> compound.put("energy", energy.serializeNBT()));
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyStorage.cast();
        }
        return super.getCapability(cap, side);
    }
}
