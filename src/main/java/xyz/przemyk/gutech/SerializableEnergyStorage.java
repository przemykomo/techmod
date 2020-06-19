package xyz.przemyk.gutech;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class SerializableEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

    public SerializableEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public SerializableEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void addEnergy(int maxEnergy) {
        this.energy += Math.min(capacity - energy, maxEnergy);
    }

    public void subtractEnergy(int maxEnergy) {
        this.energy -= Math.min(energy, maxEnergy);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("energy", getEnergyStored());
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        setEnergy(compound.getInt("energy"));
    }
}
