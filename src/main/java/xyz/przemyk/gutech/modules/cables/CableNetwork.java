package xyz.przemyk.gutech.modules.cables;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.HashSet;

public class CableNetwork implements INBTSerializable<CompoundNBT> {

    public HashSet<BlockPos> cables;
    public int id;
    LazyOptional<ElectricCableEnergyStorage> energyStorage;

    public CableNetwork(int id, World world) {
        cables = new HashSet<>();
        this.id = id;
        energyStorage = LazyOptional.of(() -> new ElectricCableEnergyStorage(world));
    }

    /**
     * Merges this network to other. Removes THIS network.
     * @param other the other network
     */
    public CableNetwork mergeInto(CableNetwork other, ServerWorld world) {
        if (other == this) {
            return other;
        }
        CableNetworksData.get(world).networks.remove(id);
        for (BlockPos blockPos : cables) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity instanceof ElectricCableTileEntity) {
                ((ElectricCableTileEntity) tileEntity).networkID = other.id;
                other.cables.add(blockPos);
            }
        }

        return other;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (BlockPos blockPos : cables) {
            arrayList.add(blockPos.getX());
            arrayList.add(blockPos.getY());
            arrayList.add(blockPos.getZ());
        }
        compound.putIntArray("blocks", arrayList);
        energyStorage.ifPresent(energy -> compound.put("energyStorage", energy.serializeNBT()));
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        int[] blocks = compound.getIntArray("blocks");
        cables.clear();
        for (int i = 0; i < blocks.length / 3; ++i) {
            int n = i * 3;
            cables.add(new BlockPos(blocks[n], blocks[n + 1], blocks[n + 2]));
        }

        energyStorage.ifPresent(energy -> energy.deserializeNBT(compound.getCompound("energyStorage")));
    }
}
