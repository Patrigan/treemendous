package deerangle.treemendous.main;

import deerangle.treemendous.config.TreemendousConfig;
import deerangle.treemendous.data.*;
import deerangle.treemendous.tree.TreeRegistry;
import deerangle.treemendous.world.BiomeMaker;
import deerangle.treemendous.world.TreeWorldGenRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Treemendous.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Treemendous {
    public static final String MODID = "treemendous";

    public static final Logger LOGGER = LogManager.getLogger();

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(TreeRegistry.rainbow_eucalyptus.sapling_item.get());
        }
    };

    private final Proxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public Treemendous() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TreemendousConfig.CONFIG_SPEC);

        ExtraRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ExtraRegistry.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ExtraRegistry.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TreeRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TreeRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TreeRegistry.BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        BiomeMaker.BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        TreeWorldGenRegistry.register();
    }

    @SubscribeEvent
    public static void registerDataGens(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        boolean genTextures = Boolean.parseBoolean(System.getProperty("generate_textures"));
        if (genTextures) {
            if (event.includeClient()) {
                generator.addProvider(new TextureProvider(generator, MODID, event.getExistingFileHelper()));
            }
        } else {
            if (event.includeServer()) {
                generator.addProvider(new RecipeProvider(generator));
                generator.addProvider(new LootTableProvider(generator, MODID));
                generator.addProvider(new ItemTagsProvider(generator, MODID, event.getExistingFileHelper()));
                generator.addProvider(new BlockTagsProvider(generator, MODID, event.getExistingFileHelper()));
            }
            if (event.includeClient()) {
                generator.addProvider(new LanguageProvider(generator, MODID, "en_us"));
                generator.addProvider(new ItemModelProvider(generator, MODID, event.getExistingFileHelper()));
                generator.addProvider(new BlockStateProvider(generator, MODID, event.getExistingFileHelper()));
            }
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        proxy.clientSetup();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        BiomeMaker.addBiomesToOverworld();
        BiomeMaker.addBiomesToBiomeDictionary();
    }

}
