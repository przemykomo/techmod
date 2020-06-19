package xyz.przemyk.gutech.blocks;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import xyz.przemyk.gutech.SerializableEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractEnergyTileEntity extends TileEntity implements ITickableTileEntity {

    protected final LazyOptional<SerializableEnergyStorage> energyStorage;

    public AbstractEnergyTileEntity(TileEntityType<?> tileEntityTypeIn, LazyOptional<SerializableEnergyStorage> energyStorage) {
        super(tileEntityTypeIn);
        this.energyStorage = energyStorage;
    }

    @Override
    public void read(CompoundNBT compound) {
        energyStorage.ifPresent(energy -> energy.deserializeNBT(compound.getCompound("energy")));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
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
