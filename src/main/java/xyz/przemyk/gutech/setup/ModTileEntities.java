package xyz.przemyk.gutech.setup;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.przemyk.gutech.PrzemekTechMod;
import xyz.przemyk.gutech.blocks.generator.FurnaceGeneratorTileEntity;

@SuppressWarnings("ConstantConditions")
public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, PrzemekTechMod.MODID);

    public static void init() {
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<TileEntityType<FurnaceGeneratorTileEntity>> FURNACE_GENERATOR =
            TILE_ENTITIES.register("furnace_generator", () ->
            TileEntityType.Builder.create(FurnaceGeneratorTileEntity::new, ModBlocks.FURNACE_GENERATOR.get()).build(null));
}
