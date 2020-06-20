package xyz.przemyk.gutech.blocks.cable;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import xyz.przemyk.gutech.SerializableEnergyStorage;
import xyz.przemyk.gutech.blocks.AbstractEnergyTileEntity;
import xyz.przemyk.gutech.setup.ModBlocks;
import xyz.przemyk.gutech.setup.ModTileEntities;

import javax.annotation.Nullable;

public class ElectricCableTileEntity extends AbstractEnergyTileEntity {

    public ElectricCableTileEntity() {
        super(ModTileEntities.ELECTRIC_CABLE.get(), LazyOptional.of(() -> new SerializableEnergyStorage(20, 20)));
    }

    public void transferEnergy(@Nullable TileEntity tileEntity, IEnergyStorage myEnergyStorage) {
        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(otherEnergyStorage -> {
                otherEnergyStorage.receiveEnergy(myEnergyStorage.extractEnergy(otherEnergyStorage.receiveEnergy(20, true), false), false);

                if (tileEntity.getBlockState().getBlock() != ModBlocks.ELECTRIC_CABLE.get()) {
                    myEnergyStorage.receiveEnergy(otherEnergyStorage.extractEnergy(myEnergyStorage.receiveEnergy(20, true), false), false);
                }
            });
        }
    }

    @Override
    public void tick() {
        energyStorage.ifPresent(myEnergyStorage -> {
            BlockState blockState = getBlockState();
            transferEnergy(world.getTileEntity(pos.down()), myEnergyStorage);

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                TileEntity tileEntity = null;
                switch (blockState.get(ElectricCableBlock.FACING_PROPERTY_MAP.get(direction))) {
                    case NONE:
                        continue;
                    case UP:
                        tileEntity = world.getTileEntity(pos.offset(direction).up());
                        break;
                    case SIDE:
                        tileEntity = world.getTileEntity(pos.offset(direction));
                        break;
                }
                transferEnergy(tileEntity, myEnergyStorage);
            }
        });
        }
}
