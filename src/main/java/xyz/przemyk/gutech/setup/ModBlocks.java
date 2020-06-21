package xyz.przemyk.gutech.setup;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.przemyk.gutech.PrzemekTechMod;
import xyz.przemyk.gutech.modules.cables.ElectricCableBlock;
import xyz.przemyk.gutech.modules.machines.furnace.ElectricFurnaceBlock;
import xyz.przemyk.gutech.modules.machines.generator.FurnaceGeneratorBlock;

@SuppressWarnings("unused")
public class ModBlocks {
    //TODO: loot tables

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, PrzemekTechMod.MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<FurnaceGeneratorBlock> FURNACE_GENERATOR = BLOCKS.register("furnace_generator", FurnaceGeneratorBlock::new);
    public static final RegistryObject<ElectricFurnaceBlock> ELECTRIC_FURNACE = BLOCKS.register("electric_furnace", ElectricFurnaceBlock::new);
    public static final RegistryObject<ElectricCableBlock> ELECTRIC_CABLE = BLOCKS.register("electric_cable", () -> new ElectricCableBlock(20));
    public static final RegistryObject<ElectricCableBlock> FAST_ELECTRIC_CABLE = BLOCKS.register("fast_electric_cable", () -> new ElectricCableBlock(100));
}
