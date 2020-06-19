package xyz.przemyk.gutech.blocks.furnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import xyz.przemyk.gutech.PrzemekTechMod;
import xyz.przemyk.gutech.SerializableEnergyStorage;
import xyz.przemyk.gutech.blocks.AbstractMachineTileEntity;
import xyz.przemyk.gutech.setup.ModTileEntities;

import javax.annotation.Nullable;

public class ElectricFurnaceTileEntity extends AbstractMachineTileEntity {

    public static final int ENERGY_PER_TICK = 20;
    public static final int MAX_COOK_TIME = 200;

    public ElectricFurnaceTileEntity() {
        super(ModTileEntities.ELECTRIC_FURNACE.get(),
                LazyOptional.of(ElectricFurnaceTileEntity::createItemHandler),
                LazyOptional.of(ElectricFurnaceTileEntity::createEnergyStorage));
    }

    private static IItemHandler createItemHandler() {
        return new ItemStackHandler(2);
    }

    private static SerializableEnergyStorage createEnergyStorage() {
        return new SerializableEnergyStorage(10000, 20, 0);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(PrzemekTechMod.MODID + ".container.electric_furnace");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ElectricFurnaceContainer(id, world, pos, playerInventory);
    }

    public int cookTime;

    @Override
    public void tick() {
        energyStorage.ifPresent(energy -> itemHandler.ifPresent(handler -> {
            ItemStack input = handler.getStackInSlot(0);
            if (energy.getEnergyStored() < ENERGY_PER_TICK || input.isEmpty()) {
                if (!world.isRemote && world.getBlockState(pos).get(BlockStateProperties.LIT)) {
                    world.setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.LIT, false));
                }
                cookTime = 0;
            } else {
                FurnaceRecipe recipe = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(handler.getStackInSlot(0)), world).orElse(null);
                if (canSmelt(recipe, handler)) {
                    ++cookTime;

                    if (!world.isRemote && !world.getBlockState(pos).get(BlockStateProperties.LIT)) {
                        world.setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.LIT, true));
                    }

                    energy.subtractEnergy(ENERGY_PER_TICK);
                    if (cookTime >= MAX_COOK_TIME && recipe != null) {
                        cookTime = 0;
                        handler.extractItem(0, 1, false);
                        handler.insertItem(1, recipe.getCraftingResult(new Inventory(handler.getStackInSlot(0))), false);
                    }
                } else {
                    cookTime = 0;
                }
            }
        }));
    }

    public boolean canSmelt(FurnaceRecipe recipe, IItemHandler itemHandler) {
        if (!itemHandler.getStackInSlot(0).isEmpty() && recipe != null) {
            ItemStack output = recipe.getCraftingResult(new Inventory(itemHandler.getStackInSlot(0)));
            if (output.isEmpty()) {
                return false;

            }
            ItemStack currentOutput = itemHandler.getStackInSlot(1);
            return currentOutput.isEmpty() || (currentOutput.isItemEqual(output) && output.getCount() + currentOutput.getCount() <= output.getMaxStackSize());
        }
        return false;
    }

    @Override
    public void read(CompoundNBT compound) {
        cookTime = compound.getInt("CookTime");
        super.read(compound);
    }
}
