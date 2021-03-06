package xyz.przemyk.gutech;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.przemyk.gutech.modules.machines.furnace.ElectricFurnaceScreen;
import xyz.przemyk.gutech.modules.machines.generator.FurnaceGeneratorScreen;
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
        ScreenManager.registerFactory(ModContainers.ELECTRIC_FURNACE.get(), ElectricFurnaceScreen::new);

        RenderTypeLookup.setRenderLayer(ModBlocks.ELECTRIC_CABLE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.FAST_ELECTRIC_CABLE.get(), RenderType.getCutout());
    }
}
