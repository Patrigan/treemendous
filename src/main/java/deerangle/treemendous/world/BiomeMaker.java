package deerangle.treemendous.world;

import com.google.common.collect.ImmutableList;
import deerangle.treemendous.config.TreemendousConfig;
import deerangle.treemendous.main.Treemendous;
import deerangle.treemendous.tree.RegisteredTree;
import deerangle.treemendous.tree.TreeRegistry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.BiomeDictionary.Type.*;

public class BiomeMaker {

    public static final DeferredRegister<Biome> BIOMES = DeferredRegister
            .create(ForgeRegistries.BIOMES, Treemendous.MODID);
    public static final RegistryKey<Biome> MIXED_MAPLE_FOREST = makeBiomeKey("mixed_maple_forest");
    public static final RegistryKey<Biome> MIXED_FOREST = makeBiomeKey("mixed_forest");
    public static final RegistryKey<Biome> MIXED_FOREST_VANILLA = makeBiomeKey("mixed_forest_vanilla");
    public static final RegistryKey<Biome> NEEDLE_FOREST = makeBiomeKey("needle_forest");
    public static final RegistryKey<Biome> NEEDLE_FOREST_SNOW = makeBiomeKey("needle_forest_snow");
    private static ConfiguredFeature<?, ?> needleTreesFeature;

    static {
        BIOMES.register("mixed_maple_forest", () -> {
            TreeRegistry.maple.registerFeature();
            TreeRegistry.red_maple.registerFeature();
            TreeRegistry.brown_maple.registerFeature();

            ConfiguredFeature<?, ?> treesFeature = registerConfiguredFeature("trees_mixed_maple",
                    Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList
                            .of(TreeRegistry.red_maple.getSingleTreeFeature().withChance(0.3F),
                                    TreeRegistry.brown_maple.getSingleTreeFeature().withChance(0.3F)),
                            TreeRegistry.maple.getSingleTreeFeature()))
                            .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                            .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 2))));
            return makeForestBiome(0.2f, 0.4f, 0.6f, false, false, treesFeature);
        });

        BIOMES.register("mixed_forest", () -> {
            for (RegisteredTree tree : TreeRegistry.TREES) {
                tree.registerFeature();
            }

            ConfiguredFeature<?, ?> treesFeature = registerConfiguredFeature("trees_mixed",
                    Feature.SIMPLE_RANDOM_SELECTOR.withConfiguration(new SingleRandomFeature(TreeRegistry.TREES.stream()
                            .map(tree -> (Supplier<ConfiguredFeature<?, ?>>) tree::getSingleTreeFeature)
                            .collect(Collectors.toList()))).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                            .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 2))));
            return makeForestBiome(0.2f, 0.4f, 0.6f, false, false, treesFeature);
        });

        BIOMES.register("mixed_forest_vanilla", () -> {
            for (RegisteredTree tree : TreeRegistry.TREES) {
                tree.registerFeature();
            }

            ConfiguredFeature<?, ?> treesFeature = registerConfiguredFeature("trees_mixed_vanilla",
                    Feature.SIMPLE_RANDOM_SELECTOR.withConfiguration(new SingleRandomFeature(Stream.concat(
                            TreeRegistry.TREES.stream()
                                    .map(tree -> (Supplier<ConfiguredFeature<?, ?>>) tree::getSingleTreeFeature),
                            ImmutableList.of(Features.OAK, Features.SPRUCE, Features.BIRCH, Features.JUNGLE_TREE,
                                    Features.ACACIA, Features.DARK_OAK).stream().map(tree -> () -> tree))
                            .collect(Collectors.toList()))).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                            .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 2))));
            return makeForestBiome(0.2f, 0.4f, 0.6f, false, false, treesFeature);
        });

        BIOMES.register("needle_forest", () -> {
            TreeRegistry.fir.registerFeature();
            TreeRegistry.douglas.registerFeature();
            TreeRegistry.larch.registerFeature();
            TreeRegistry.pine.registerFeature();
            TreeRegistry.cedar.registerFeature();
            TreeRegistry.juniper.registerFeature();

            if (needleTreesFeature == null) {
                needleTreesFeature = registerConfiguredFeature("trees_needle", Feature.SIMPLE_RANDOM_SELECTOR
                        .withConfiguration(new SingleRandomFeature(ImmutableList
                                .of(TreeRegistry.fir::getSingleTreeFeature, TreeRegistry.douglas::getSingleTreeFeature,
                                        TreeRegistry.larch::getSingleTreeFeature,
                                        TreeRegistry.juniper::getSingleTreeFeature,
                                        TreeRegistry.cedar::getSingleTreeFeature,
                                        TreeRegistry.pine::getSingleTreeFeature, () -> Features.SPRUCE,
                                        () -> Features.PINE))).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                        .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 2))));
            }
            return makeForestBiome(0.2f, 0.4f, 0.4f, false, false, needleTreesFeature);
        });

        BIOMES.register("needle_forest_snow", () -> {
            TreeRegistry.fir.registerFeature();
            TreeRegistry.douglas.registerFeature();
            TreeRegistry.larch.registerFeature();
            TreeRegistry.pine.registerFeature();
            TreeRegistry.cedar.registerFeature();
            TreeRegistry.juniper.registerFeature();

            if (needleTreesFeature == null) {
                needleTreesFeature = registerConfiguredFeature("trees_needle", Feature.SIMPLE_RANDOM_SELECTOR
                        .withConfiguration(new SingleRandomFeature(ImmutableList
                                .of(TreeRegistry.fir::getSingleTreeFeature, TreeRegistry.douglas::getSingleTreeFeature,
                                        TreeRegistry.larch::getSingleTreeFeature,
                                        TreeRegistry.juniper::getSingleTreeFeature,
                                        TreeRegistry.cedar::getSingleTreeFeature,
                                        TreeRegistry.pine::getSingleTreeFeature, () -> Features.SPRUCE,
                                        () -> Features.PINE))).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                        .withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(10, 0.1F, 2))));
            }
            return makeForestBiome(0.2f, 0.4f, 0.4f, true, false, needleTreesFeature);
        });
    }

    public static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> registerConfiguredFeature(String key, ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry
                .register(WorldGenRegistries.CONFIGURED_FEATURE, Treemendous.MODID + ":" + key, configuredFeature);
    }

    public static Biome makeForestBiome(float depth, float scale, float temperature, boolean snowy, boolean dry, ConfiguredFeature<?, ?> tree) {
        MobSpawnInfo.Builder mobSpawnInfo = new MobSpawnInfo.Builder();
        DefaultBiomeFeatures.withPassiveMobs(mobSpawnInfo);
        DefaultBiomeFeatures.withBatsAndHostiles(mobSpawnInfo);
        BiomeGenerationSettings.Builder genSettings = new BiomeGenerationSettings.Builder()
                .withSurfaceBuilder(ConfiguredSurfaceBuilders.field_244178_j);
        DefaultBiomeFeatures.withStrongholdAndMineshaft(genSettings);
        genSettings.withStructure(StructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.withCavesAndCanyons(genSettings);
        DefaultBiomeFeatures.withLavaAndWaterLakes(genSettings);
        DefaultBiomeFeatures.withMonsterRoom(genSettings);
        DefaultBiomeFeatures.withDisks(genSettings);
        DefaultBiomeFeatures.withAllForestFlowerGeneration(genSettings);
        DefaultBiomeFeatures.withDefaultFlowers(genSettings);
        DefaultBiomeFeatures.withForestGrass(genSettings);
        DefaultBiomeFeatures.withCommonOverworldBlocks(genSettings);
        DefaultBiomeFeatures.withOverworldOres(genSettings);
        DefaultBiomeFeatures.withNormalMushroomGeneration(genSettings);
        DefaultBiomeFeatures.withSugarCaneAndPumpkins(genSettings);
        DefaultBiomeFeatures.withLavaAndWaterSprings(genSettings);
        DefaultBiomeFeatures.withFrozenTopLayer(genSettings);
        genSettings.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, tree);

        return new Biome.Builder()
                .precipitation(snowy ? Biome.RainType.SNOW : (dry ? Biome.RainType.NONE : Biome.RainType.RAIN))
                .category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(snowy ? -0.5F : temperature)
                .downfall(snowy ? 0.4F : (dry ? 0.0F : 0.8F)).setEffects(
                        (new BiomeAmbience.Builder()).setWaterColor(snowy ? 4020182 : 4159204).setWaterFogColor(329011)
                                .setFogColor(12638463).withSkyColor(MathHelper.hsvToRGB(0.6105556f, 0.5233333f, 1.0F))
                                .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build())
                .withMobSpawnSettings(mobSpawnInfo.copy()).withGenerationSettings(genSettings.build()).build();
    }

    public static void addBiomesToOverworld() {
        TreemendousConfig.BiomeSpawnRate spawnRateConfig = TreemendousConfig.biomeSpawnRateConfig;
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(MIXED_MAPLE_FOREST, spawnRateConfig.mixedMapleForestWeight.get()));
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(MIXED_FOREST, spawnRateConfig.mixedForestWeight.get()));
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(MIXED_FOREST_VANILLA, spawnRateConfig.mixedForestVanillaWeight.get()));
        BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(NEEDLE_FOREST, spawnRateConfig.needleForestWeight.get()));
        BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(NEEDLE_FOREST_SNOW, spawnRateConfig.needleForestSnowWeight.get()));
        for (RegisteredTree tree : TreeRegistry.TREES) {
            for (RegistryKey<Biome> b : tree.getFrostyBiomes()) {
                BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(b, spawnRateConfig.treeWeights.get(b).get()));
            }
            for (RegistryKey<Biome> b : tree.getBiomes()) {
                BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(b, spawnRateConfig.treeWeights.get(b).get()));
            }
        }
    }

    public static void addBiomesToBiomeDictionary() {
        BiomeDictionary.addTypes(MIXED_MAPLE_FOREST, OVERWORLD, FOREST);
        BiomeDictionary.addTypes(MIXED_FOREST, OVERWORLD, FOREST);
        BiomeDictionary.addTypes(MIXED_FOREST_VANILLA, OVERWORLD, FOREST);
        BiomeDictionary.addTypes(NEEDLE_FOREST, OVERWORLD, FOREST);
        BiomeDictionary.addTypes(NEEDLE_FOREST_SNOW, OVERWORLD, FOREST);
        for (RegisteredTree tree : TreeRegistry.TREES) {
            for (RegistryKey<Biome> b : tree.getFrostyBiomes()) {
                BiomeDictionary.addTypes(b, OVERWORLD, FOREST, COLD);
            }
            for (RegistryKey<Biome> b : tree.getBiomes()) {
                BiomeDictionary.addTypes(b, OVERWORLD, FOREST);
            }
        }
    }

    public static RegistryKey<Biome> makeBiomeKey(String name) {
        return RegistryKey.getOrCreateKey(Registry.BIOME_KEY, new ResourceLocation(Treemendous.MODID, name));
    }
}
