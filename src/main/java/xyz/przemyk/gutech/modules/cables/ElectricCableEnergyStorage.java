package xyz.przemyk.gutech.modules.cables;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.HashSet;

public class ElectricCableEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundNBT> {

    public static class MachineEntry {
        public TileEntity tileEntity;
        public Direction direction;

        public MachineEntry(TileEntity tileEntity, Direction direction) {
            this.tileEntity = tileEntity;
            this.direction = direction;
        }
    }

    private HashSet<MachineEntry> cachedMachines = null;
    public ArrayList<Pair<BlockPos, Direction>> machinesPos;

    private final World world;

    public ElectricCableEnergyStorage(World world) {
        this.world = world;
        machinesPos = new ArrayList<>();
    }

    public HashSet<MachineEntry> getCachedMachines() {
        if (cachedMachines == null) {
            cachedMachines = new HashSet<>();

            for (Pair<BlockPos, Direction> machine : machinesPos) {
                cachedMachines.add(new MachineEntry(world.getTileEntity(machine.getFirst()), machine.getSecond()));
            }
        }

        return cachedMachines;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        int[] positions = new int[machinesPos.size() * 3];
        for (int i = 0; i < machinesPos.size(); ++i) {
            int n = i * 4;
            Pair<BlockPos, Direction> machine = machinesPos.get(i);
            positions[n] = machine.getFirst().getX();
            positions[n + 1] = machine.getFirst().getY();
            positions[n + 2] = machine.getFirst().getZ();
            positions[n + 3] = machine.getSecond().getIndex();
        }
        compoundNBT.putIntArray("machines", positions);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        machinesPos.clear();
        int[] positions = compoundNBT.getIntArray("machines");
        for (int i = 0; i < positions.length / 4; ++i) {
            int n = i * 4;
            machinesPos.add(new Pair<>(new BlockPos(positions[n], positions[n + 1], positions[n + 2]),
                    Direction.byIndex(positions[n + 3])));
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int totalEnergyReceived = 0;

        HashSet<MachineEntry> machineEntries = getCachedMachines();
        if (machineEntries.size() == 0) {
            return 0;
        }

        int tryReceive = maxReceive / machineEntries.size();
        for (MachineEntry machineEntry : machineEntries) {
            if (machineEntry.tileEntity.isRemoved()) {
                machineEntries.remove(machineEntry);
                machinesPos.remove(new Pair<>(machineEntry.tileEntity.getPos(), machineEntry.direction)); // idk if it will work with "new Pair"
            }
            IEnergyStorage energyStorage = machineEntry.tileEntity.getCapability(CapabilityEnergy.ENERGY, machineEntry.direction).orElse(null);
            if (energyStorage != null) {
                totalEnergyReceived += energyStorage.receiveEnergy(tryReceive, simulate);
            }
        }
        return totalEnergyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int totalEnergyExtracted = 0;

        HashSet<MachineEntry> machineEntries = getCachedMachines();
        if (machineEntries.size() == 0) {
            return 0;
        }

        int tryExtract = maxExtract / machineEntries.size();
        for (MachineEntry machineEntry : machineEntries) {
            if (machineEntry.tileEntity.isRemoved()) {
                machineEntries.remove(machineEntry);
                machinesPos.remove(new Pair<>(machineEntry.tileEntity.getPos(), machineEntry.direction)); // idk if it will work with "new Pair"
            }
            IEnergyStorage energyStorage = machineEntry.tileEntity.getCapability(CapabilityEnergy.ENERGY, machineEntry.direction).orElse(null);
            if (energyStorage != null) {
                totalEnergyExtracted += energyStorage.extractEnergy(tryExtract, simulate);
            }
        }
        return totalEnergyExtracted;
    }

    @Override
    public int getEnergyStored() {
        int totalEnergyStored = 0;

        HashSet<MachineEntry> machineEntries = getCachedMachines();
        for (MachineEntry machineEntry : machineEntries) {
            if (machineEntry.tileEntity.isRemoved()) {
                machineEntries.remove(machineEntry);
                machinesPos.remove(new Pair<>(machineEntry.tileEntity.getPos(), machineEntry.direction)); // idk if it will work with "new Pair"
            }
            IEnergyStorage energyStorage = machineEntry.tileEntity.getCapability(CapabilityEnergy.ENERGY, machineEntry.direction).orElse(null);
            if (energyStorage != null) {
                totalEnergyStored += energyStorage.getEnergyStored();
            }
        }
        return totalEnergyStored;
    }

    @Override
    public int getMaxEnergyStored() {
        int totalMaxEnergyStored = 0;

        HashSet<MachineEntry> machineEntries = getCachedMachines();
        for (MachineEntry machineEntry : machineEntries) {
            if (machineEntry.tileEntity.isRemoved()) {
                machineEntries.remove(machineEntry);
                machinesPos.remove(new Pair<>(machineEntry.tileEntity.getPos(), machineEntry.direction)); // idk if it will work with "new Pair"
            }
            IEnergyStorage energyStorage = machineEntry.tileEntity.getCapability(CapabilityEnergy.ENERGY, machineEntry.direction).orElse(null);
            if (energyStorage != null) {
                totalMaxEnergyStored += energyStorage.getMaxEnergyStored();
            }
        }
        return totalMaxEnergyStored;
    }

    @Override
    public boolean canExtract() {
        HashSet<MachineEntry> machineEntries = getCachedMachines();
        for (MachineEntry machineEntry : machineEntries) {
            if (machineEntry.tileEntity.isRemoved()) {
                machineEntries.remove(machineEntry);
                machinesPos.remove(new Pair<>(machineEntry.tileEntity.getPos(), machineEntry.direction)); // idk if it will work with "new Pair"
            }
            IEnergyStorage energyStorage = machineEntry.tileEntity.getCapability(CapabilityEnergy.ENERGY, machineEntry.direction).orElse(null);
            if (energyStorage != null && energyStorage.canExtract()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canReceive() {
        HashSet<MachineEntry> machineEntries = getCachedMachines();
        for (MachineEntry machineEntry : machineEntries) {
            if (machineEntry.tileEntity.isRemoved()) {
                machineEntries.remove(machineEntry);
                machinesPos.remove(new Pair<>(machineEntry.tileEntity.getPos(), machineEntry.direction)); // idk if it will work with "new Pair"
            }
            IEnergyStorage energyStorage = machineEntry.tileEntity.getCapability(CapabilityEnergy.ENERGY, machineEntry.direction).orElse(null);
            if (energyStorage != null && energyStorage.canReceive()) {
                return true;
            }
        }
        return false;
    }
}
