package deerangle.treemendous.main;

import deerangle.treemendous.data.*;
import deerangle.treemendous.tree.RegisteredTree;
import deerangle.treemendous.tree.TreeRegistry;
import deerangle.treemendous.world.BiomeMaker;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.AxeItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod(Treemendous.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Treemendous {
    public static final String MODID = "treemendous";

    public static final Logger LOGGER = LogManager.getLogger();
    private final Proxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public Treemendous() {
        ExtraRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ExtraRegistry.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ExtraRegistry.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TreeRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TreeRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TreeRegistry.BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        BiomeMaker.BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
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
                generator.addProvider(new BlockStateProvider(generator, MODID, event.getExistingFileHelper()));
                generator.addProvider(new ItemModelProvider(generator, MODID, event.getExistingFileHelper()));
            }
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        proxy.clientSetup();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        AxeItem.BLOCK_STRIPPING_MAP = new HashMap<>(AxeItem.BLOCK_STRIPPING_MAP);
        for (RegisteredTree tree : TreeRegistry.TREES) {
            tree.registerFeature();
            AxeItem.BLOCK_STRIPPING_MAP.put(tree.wood.get(), tree.stripped_wood.get());
            AxeItem.BLOCK_STRIPPING_MAP.put(tree.log.get(), tree.stripped_log.get());
        }
        BiomeMaker.addBiomesToOverworld();
    }

}
