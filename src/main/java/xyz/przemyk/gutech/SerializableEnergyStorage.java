package xyz.przemyk.gutech;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class SerializableEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

    public SerializableEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public void setEnergy(int energy) {
        this.energy = energy;
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
