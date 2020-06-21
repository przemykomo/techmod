package xyz.przemyk.gutech.modules.machines;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public abstract class AbstractEnergyTileEntity extends TileEntity implements ITickableTileEntity {

    protected final LazyOptional<SerializableEnergyStorage> optionalEnergyStorage;
    protected HashMap<Direction, IEnergyStorage> connectedEnergyStorages = new HashMap<>();

    public AbstractEnergyTileEntity(TileEntityType<?> tileEntityTypeIn, LazyOptional<SerializableEnergyStorage> optionalEnergyStorage) {
        super(tileEntityTypeIn);
        this.optionalEnergyStorage = optionalEnergyStorage;
    }

    @Override
    public void read(CompoundNBT compound) {
        optionalEnergyStorage.ifPresent(energy -> energy.deserializeNBT(compound));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        optionalEnergyStorage.ifPresent(energy -> energy.write(compound));
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return optionalEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    public void addMachine(Direction direction) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(direction));
        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(energy ->
                    connectedEnergyStorages.put(direction, energy));
        }
    }

    private boolean firstTick = true;

    @Override
    public void tick() {
        // I cannot use onLoad() because it needs to have already loaded tile entities
        if (firstTick) {
            firstTick = false;
            for (Direction direction : Direction.values()) {
                addMachine(direction);
            }
        }
        optionalEnergyStorage.ifPresent(myEnergyStorage -> {
            for (Direction direction : Direction.values()) {
                IEnergyStorage otherEnergyStorage = connectedEnergyStorages.get(direction);
                if (otherEnergyStorage == null) {
                    continue;
                }
                otherEnergyStorage.receiveEnergy(myEnergyStorage.extractEnergy(otherEnergyStorage.receiveEnergy(myEnergyStorage.maxExtract(), true), false), false);
                myEnergyStorage.receiveEnergy(otherEnergyStorage.extractEnergy(myEnergyStorage.receiveEnergy(myEnergyStorage.maxReceive(), true), false), false);
            }
        });
    }
}