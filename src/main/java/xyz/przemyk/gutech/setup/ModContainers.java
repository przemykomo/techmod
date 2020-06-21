package xyz.przemyk.gutech.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.przemyk.gutech.PrzemekTechMod;
import xyz.przemyk.gutech.modules.machines.furnace.ElectricFurnaceContainer;
import xyz.przemyk.gutech.modules.machines.generator.FurnaceGeneratorContainer;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, PrzemekTechMod.MODID);

    public static void init() {
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<ContainerType<FurnaceGeneratorContainer>> FURNACE_GENERATOR =
            CONTAINERS.register("furnace_generator", () ->
                    IForgeContainerType.create(((windowId, inv, data) ->
                            new FurnaceGeneratorContainer(windowId, Minecraft.getInstance().world, data.readBlockPos(), inv))));

    public static final RegistryObject<ContainerType<ElectricFurnaceContainer>> ELECTRIC_FURNACE =
            CONTAINERS.register("electric_furnace", () ->
                    IForgeContainerType.create(((windowId, inv, data) ->
                            new ElectricFurnaceContainer(windowId, Minecraft.getInstance().world, data.readBlockPos(), inv))));

}
