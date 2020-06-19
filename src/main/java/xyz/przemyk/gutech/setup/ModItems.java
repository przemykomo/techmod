package xyz.przemyk.gutech.setup;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.przemyk.gutech.PrzemekTechMod;

@SuppressWarnings("unused")
public class ModItems {

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, PrzemekTechMod.MODID);

    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final ItemGroup ITEM_GROUP = new ItemGroup(PrzemekTechMod.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(FURNACE_GENERATOR.get(), 63);
        }
    };

    public static final RegistryObject<BlockItem> FURNACE_GENERATOR = ITEMS.register("furnace_generator", () -> new BlockItem(ModBlocks.FURNACE_GENERATOR.get(), new Item.Properties().group(ITEM_GROUP)));
    public static final RegistryObject<BlockItem> ELECTRIC_FURNACE = ITEMS.register("electric_furnace", () -> new BlockItem(ModBlocks.ELECTRIC_FURNACE.get(), new Item.Properties().group(ITEM_GROUP)));
    public static final RegistryObject<BlockItem> ELECTRIC_CABLE = ITEMS.register("electric_cable", () -> new BlockItem(ModBlocks.ELECTRIC_CABLE.get(), new Item.Properties().group(ITEM_GROUP)));
}
