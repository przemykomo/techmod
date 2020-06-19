package xyz.przemyk.gutech.blocks.cable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import xyz.przemyk.gutech.SerializableEnergyStorage;
import xyz.przemyk.gutech.blocks.AbstractEnergyTileEntity;
import xyz.przemyk.gutech.setup.ModTileEntities;

public class ElectricCableTileEntity extends AbstractEnergyTileEntity {

    public ElectricCableTileEntity() {
        super(ModTileEntities.ELECTRIC_CABLE.get(), LazyOptional.of(() -> new SerializableEnergyStorage(20, 20)));
    }

    @Override
    public void tick() {
        //TODO: change getAllInBox to iterating over direction blockStates
        BlockPos.getAllInBox(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1,
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).forEach(blockPos -> {
                    TileEntity tileEntity = world.getTileEntity(blockPos);
                    if (tileEntity != null) {
                        world.getTileEntity(blockPos).getCapability(CapabilityEnergy.ENERGY).ifPresent(otherEnergyStorage ->
                                energyStorage.ifPresent(myEnergyStorage -> {
                                    otherEnergyStorage.receiveEnergy(myEnergyStorage.extractEnergy(20, false), false);
                                    myEnergyStorage.receiveEnergy(otherEnergyStorage.extractEnergy(20, false), false);
                                }));
                    }
                });
    }
}
