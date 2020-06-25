package xyz.przemyk.gutech.modules.cables;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import xyz.przemyk.gutech.setup.ModTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ElectricCableTileEntity extends TileEntity {

    public int networkID;

    public ElectricCableTileEntity() {
        super(ModTileEntities.ELECTRIC_CABLE.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("networkID", networkID);
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        networkID = compound.getInt("networkID");
        super.read(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!world.isRemote && side != Direction.UP && cap == CapabilityEnergy.ENERGY) {
            CableNetwork cableNetwork = CableNetworksData.get((ServerWorld) world).networks.get(networkID);
            if (cableNetwork != null) {
                return cableNetwork.energyStorage.cast();
            }
        }
        return super.getCapability(cap, side);
    }
}
