package xyz.przemyk.gutech.setup;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.przemyk.gutech.PrzemekTechMod;
import xyz.przemyk.gutech.modules.cables.ElectricCableTileEntity;
import xyz.przemyk.gutech.modules.machines.furnace.ElectricFurnaceTileEntity;
import xyz.przemyk.gutech.modules.machines.generator.FurnaceGeneratorTileEntity;

@SuppressWarnings("ConstantConditions")
public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, PrzemekTechMod.MODID);

    public static void init() {
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<TileEntityType<FurnaceGeneratorTileEntity>> FURNACE_GENERATOR =
            TILE_ENTITIES.register("furnace_generator", () ->
            TileEntityType.Builder.create(FurnaceGeneratorTileEntity::new, ModBlocks.FURNACE_GENERATOR.get()).build(null));

    public static final RegistryObject<TileEntityType<ElectricFurnaceTileEntity>> ELECTRIC_FURNACE =
            TILE_ENTITIES.register("electric_furnace", () ->
                    TileEntityType.Builder.create(ElectricFurnaceTileEntity::new, ModBlocks.ELECTRIC_FURNACE.get()).build(null));

    // each electric cable type MUST have different TileEntityType to make sure cable networks are working correctly
    public static final RegistryObject<TileEntityType<ElectricCableTileEntity>> ELECTRIC_CABLE =
            TILE_ENTITIES.register("electric_cable", () ->
                    TileEntityType.Builder.create(ElectricCableTileEntity::new, ModBlocks.ELECTRIC_CABLE.get()).build(null));

}
