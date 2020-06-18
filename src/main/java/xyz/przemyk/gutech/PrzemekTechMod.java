package xyz.przemyk.gutech;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.przemyk.gutech.blocks.generator.FurnaceGeneratorScreen;
import xyz.przemyk.gutech.setup.ModBlocks;
import xyz.przemyk.gutech.setup.ModContainers;
import xyz.przemyk.gutech.setup.ModItems;
import xyz.przemyk.gutech.setup.ModTileEntities;

@Mod(PrzemekTechMod.MODID)
public class PrzemekTechMod {
    public static final String MODID = "przemektech";

    public PrzemekTechMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

        ModBlocks.init();
        ModItems.init();
        ModTileEntities.init();
        ModContainers.init();
    }

    public void init(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainers.FURNACE_GENERATOR.get(), FurnaceGeneratorScreen::new);
    }
}
