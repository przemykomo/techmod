package xyz.przemyk.gutech.modules.machines;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractMachineTileEntity extends AbstractEnergyTileEntity implements INamedContainerProvider {
    protected final LazyOptional<IItemHandler> itemHandler;

    public AbstractMachineTileEntity(TileEntityType<?> tileEntityTypeIn, LazyOptional<IItemHandler> itemHandler, LazyOptional<SerializableEnergyStorage> energyStorage) {
        super(tileEntityTypeIn, energyStorage);
        this.itemHandler = itemHandler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void read(CompoundNBT compound) {
        itemHandler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(compound.getCompound("inv")));
        super.read(compound);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        itemHandler.ifPresent(h -> compound.put("inv", ((INBTSerializable<CompoundNBT>) h).serializeNBT()));
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }

        return super.getCapability(cap, side);
    }
}
