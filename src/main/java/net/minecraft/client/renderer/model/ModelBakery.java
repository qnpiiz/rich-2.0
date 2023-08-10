package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.BellTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.ConduitTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.EnchantmentTableTileEntityRenderer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.registry.Registry;
import net.optifine.reflect.Reflector;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery
{
    public static final RenderMaterial LOCATION_FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/fire_0"));
    public static final RenderMaterial LOCATION_FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/fire_1"));
    public static final RenderMaterial LOCATION_LAVA_FLOW = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/lava_flow"));
    public static final RenderMaterial LOCATION_WATER_FLOW = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/water_flow"));
    public static final RenderMaterial LOCATION_WATER_OVERLAY = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("block/water_overlay"));
    public static final RenderMaterial LOCATION_BANNER_BASE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/banner_base"));
    public static final RenderMaterial LOCATION_SHIELD_BASE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/shield_base"));
    public static final RenderMaterial LOCATION_SHIELD_NO_PATTERN = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/shield_base_nopattern"));
    public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10).mapToObj((p_lambda$static$0_0_) ->
    {
        return new ResourceLocation("block/destroy_stage_" + p_lambda$static$0_0_);
    }).collect(Collectors.toList());
    public static final List<ResourceLocation> DESTROY_LOCATIONS = DESTROY_STAGES.stream().map((p_lambda$static$1_0_) ->
    {
        return new ResourceLocation("textures/" + p_lambda$static$1_0_.getPath() + ".png");
    }).collect(Collectors.toList());
    public static final List<RenderType> DESTROY_RENDER_TYPES = DESTROY_LOCATIONS.stream().map(RenderType::getCrumbling).collect(Collectors.toList());
    private static final Set<RenderMaterial> LOCATIONS_BUILTIN_TEXTURES = Util.make(Sets.newHashSet(), (p_lambda$static$2_0_) ->
    {
        p_lambda$static$2_0_.add(LOCATION_WATER_FLOW);
        p_lambda$static$2_0_.add(LOCATION_LAVA_FLOW);
        p_lambda$static$2_0_.add(LOCATION_WATER_OVERLAY);
        p_lambda$static$2_0_.add(LOCATION_FIRE_0);
        p_lambda$static$2_0_.add(LOCATION_FIRE_1);
        p_lambda$static$2_0_.add(BellTileEntityRenderer.BELL_BODY_TEXTURE);
        p_lambda$static$2_0_.add(ConduitTileEntityRenderer.BASE_TEXTURE);
        p_lambda$static$2_0_.add(ConduitTileEntityRenderer.CAGE_TEXTURE);
        p_lambda$static$2_0_.add(ConduitTileEntityRenderer.WIND_TEXTURE);
        p_lambda$static$2_0_.add(ConduitTileEntityRenderer.VERTICAL_WIND_TEXTURE);
        p_lambda$static$2_0_.add(ConduitTileEntityRenderer.OPEN_EYE_TEXTURE);
        p_lambda$static$2_0_.add(ConduitTileEntityRenderer.CLOSED_EYE_TEXTURE);
        p_lambda$static$2_0_.add(EnchantmentTableTileEntityRenderer.TEXTURE_BOOK);
        p_lambda$static$2_0_.add(LOCATION_BANNER_BASE);
        p_lambda$static$2_0_.add(LOCATION_SHIELD_BASE);
        p_lambda$static$2_0_.add(LOCATION_SHIELD_NO_PATTERN);

        for (ResourceLocation resourcelocation : DESTROY_STAGES)
        {
            p_lambda$static$2_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, resourcelocation));
        }

        p_lambda$static$2_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET));
        p_lambda$static$2_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE));
        p_lambda$static$2_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS));
        p_lambda$static$2_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS));
        p_lambda$static$2_0_.add(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD));
        Atlases.collectAllMaterials(p_lambda$static$2_0_::add);
    });
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
    private static final String MODEL_MISSING_STRING = MODEL_MISSING.toString();
    @VisibleForTesting
    public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '"');
    private static final Map<String, String> BUILT_IN_MODELS = Maps.newHashMap(ImmutableMap.of("missing", MISSING_MODEL_MESH));
    private static final Splitter SPLITTER_COMMA = Splitter.on(',');
    private static final Splitter EQUALS_SPLITTER = Splitter.on('=').limit(2);
    public static final BlockModel MODEL_GENERATED = Util.make(BlockModel.deserialize("{\"gui_light\": \"front\"}"), (p_lambda$static$3_0_) ->
    {
        p_lambda$static$3_0_.name = "generation marker";
    });
    public static final BlockModel MODEL_ENTITY = Util.make(BlockModel.deserialize("{\"gui_light\": \"side\"}"), (p_lambda$static$4_0_) ->
    {
        p_lambda$static$4_0_.name = "block entity marker";
    });
    private static final StateContainer<Block, BlockState> STATE_CONTAINER_ITEM_FRAME = (new StateContainer.Builder<Block, BlockState>(Blocks.AIR)).add(BooleanProperty.create("map")).func_235882_a_(Block::getDefaultState, BlockState::new);
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<ResourceLocation, StateContainer<Block, BlockState>> STATE_CONTAINER_OVERRIDES = ImmutableMap.of(new ResourceLocation("item_frame"), STATE_CONTAINER_ITEM_FRAME);
    private final IResourceManager resourceManager;
    @Nullable
    private SpriteMap spriteMap;
    private final BlockColors blockColors;
    private final Set<ResourceLocation> unbakedModelLoadingQueue = Sets.newHashSet();
    private final BlockModelDefinition.ContainerHolder containerHolder = new BlockModelDefinition.ContainerHolder();
    private final Map<ResourceLocation, IUnbakedModel> unbakedModels = Maps.newHashMap();
    private final Map<Triple<ResourceLocation, TransformationMatrix, Boolean>, IBakedModel> bakedModels = Maps.newHashMap();
    private final Map<ResourceLocation, IUnbakedModel> topUnbakedModels = Maps.newHashMap();
    private final Map<ResourceLocation, IBakedModel> topBakedModels = Maps.newHashMap();
    private Map<ResourceLocation, Pair<AtlasTexture, AtlasTexture.SheetData>> sheetData;
    private int counterModelId = 1;
    private final Object2IntMap<BlockState> stateModelIds = Util.make(new Object2IntOpenHashMap<>(), (p_lambda$new$5_0_) ->
    {
        p_lambda$new$5_0_.defaultReturnValue(-1);
    });
    public Map<ResourceLocation, IUnbakedModel> mapUnbakedModels;

    public ModelBakery(IResourceManager resourceManagerIn, BlockColors blockColorsIn, IProfiler profilerIn, int maxMipmapLevel)
    {
        this(resourceManagerIn, blockColorsIn, true);
        this.processLoading(profilerIn, maxMipmapLevel);
    }

    protected ModelBakery(IResourceManager p_i242117_1_, BlockColors p_i242117_2_, boolean p_i242117_3_)
    {
        this.resourceManager = p_i242117_1_;
        this.blockColors = p_i242117_2_;
    }

    protected void processLoading(IProfiler p_processLoading_1_, int p_processLoading_2_)
    {
        Reflector.ModelLoaderRegistry_onModelLoadingStart.callVoid();
        p_processLoading_1_.startSection("missing_model");

        try
        {
            this.unbakedModels.put(MODEL_MISSING, this.loadModel(MODEL_MISSING));
            this.loadTopModel(MODEL_MISSING);
        }
        catch (IOException ioexception)
        {
            LOGGER.error("Error loading missing model, should never happen :(", (Throwable)ioexception);
            throw new RuntimeException(ioexception);
        }

        p_processLoading_1_.endStartSection("static_definitions");
        STATE_CONTAINER_OVERRIDES.forEach((p_lambda$processLoading$7_1_, p_lambda$processLoading$7_2_) ->
        {
            p_lambda$processLoading$7_2_.getValidStates().forEach((p_lambda$null$6_2_) -> {
                this.loadTopModel(BlockModelShapes.getModelLocation(p_lambda$processLoading$7_1_, p_lambda$null$6_2_));
            });
        });
        p_processLoading_1_.endStartSection("blocks");

        for (Block block : Registry.BLOCK)
        {
            block.getStateContainer().getValidStates().forEach((p_lambda$processLoading$8_1_) ->
            {
                this.loadTopModel(BlockModelShapes.getModelLocation(p_lambda$processLoading$8_1_));
            });
        }

        p_processLoading_1_.endStartSection("items");

        for (ResourceLocation resourcelocation : Registry.ITEM.keySet())
        {
            this.loadTopModel(new ModelResourceLocation(resourcelocation, "inventory"));
        }

        p_processLoading_1_.endStartSection("special");
        this.loadTopModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));

        for (ResourceLocation resourcelocation1 : this.getSpecialModels())
        {
            this.addModelToCache(resourcelocation1);
        }

        p_processLoading_1_.endStartSection("textures");
        this.mapUnbakedModels = this.unbakedModels;
        TextureUtils.registerCustomModels(this);
        Set<Pair<String, String>> set = Sets.newLinkedHashSet();
        Set<RenderMaterial> set1 = this.topUnbakedModels.values().stream().flatMap((p_lambda$processLoading$9_2_) ->
        {
            return p_lambda$processLoading$9_2_.getTextures(this::getUnbakedModel, set).stream();
        }).collect(Collectors.toSet());
        set1.addAll(LOCATIONS_BUILTIN_TEXTURES);
        Reflector.call(Reflector.ForgeHooksClient_gatherFluidTextures, set1);
        set.stream().filter((p_lambda$processLoading$10_0_) ->
        {
            return !p_lambda$processLoading$10_0_.getSecond().equals(MODEL_MISSING_STRING);
        }).forEach((p_lambda$processLoading$11_0_) ->
        {
            LOGGER.warn("Unable to resolve texture reference: {} in {}", p_lambda$processLoading$11_0_.getFirst(), p_lambda$processLoading$11_0_.getSecond());
        });
        Map<ResourceLocation, List<RenderMaterial>> map = set1.stream().collect(Collectors.groupingBy(RenderMaterial::getAtlasLocation));
        p_processLoading_1_.endStartSection("stitching");
        this.sheetData = Maps.newHashMap();

        for (Entry<ResourceLocation, List<RenderMaterial>> entry : map.entrySet())
        {
            AtlasTexture atlastexture = new AtlasTexture(entry.getKey());
            AtlasTexture.SheetData atlastexture$sheetdata = atlastexture.stitch(this.resourceManager, entry.getValue().stream().map(RenderMaterial::getTextureLocation), p_processLoading_1_, p_processLoading_2_);
            this.sheetData.put(entry.getKey(), Pair.of(atlastexture, atlastexture$sheetdata));
        }

        p_processLoading_1_.endSection();
    }

    public SpriteMap uploadTextures(TextureManager resourceManagerIn, IProfiler profilerIn)
    {
        profilerIn.startSection("atlas");

        for (Pair<AtlasTexture, AtlasTexture.SheetData> pair : this.sheetData.values())
        {
            AtlasTexture atlastexture = pair.getFirst();
            AtlasTexture.SheetData atlastexture$sheetdata = pair.getSecond();
            atlastexture.upload(atlastexture$sheetdata);
            resourceManagerIn.loadTexture(atlastexture.getTextureLocation(), atlastexture);
            resourceManagerIn.bindTexture(atlastexture.getTextureLocation());
            atlastexture.setBlurMipmap(atlastexture$sheetdata);
        }

        this.spriteMap = new SpriteMap(this.sheetData.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
        profilerIn.endStartSection("baking");
        this.topUnbakedModels.keySet().forEach((p_lambda$uploadTextures$12_1_) ->
        {
            IBakedModel ibakedmodel = null;

            try {
                ibakedmodel = this.bake(p_lambda$uploadTextures$12_1_, ModelRotation.X0_Y0);
            }
            catch (Exception exception)
            {
                LOGGER.warn("Unable to bake model: '{}': {}", p_lambda$uploadTextures$12_1_, exception);
            }

            if (ibakedmodel != null)
            {
                this.topBakedModels.put(p_lambda$uploadTextures$12_1_, ibakedmodel);
            }
        });
        profilerIn.endSection();
        return this.spriteMap;
    }

    private static Predicate<BlockState> parseVariantKey(StateContainer<Block, BlockState> containerIn, String variantIn)
    {
        Map < Property<?>, Comparable<? >> map = Maps.newHashMap();

        for (String s : SPLITTER_COMMA.split(variantIn))
        {
            Iterator<String> iterator = EQUALS_SPLITTER.split(s).iterator();

            if (iterator.hasNext())
            {
                String s1 = iterator.next();
                Property<?> property = containerIn.getProperty(s1);

                if (property != null && iterator.hasNext())
                {
                    String s2 = iterator.next();
                    Comparable<?> comparable = parseValue(property, s2);

                    if (comparable == null)
                    {
                        throw new RuntimeException("Unknown value: '" + s2 + "' for blockstate property: '" + s1 + "' " + property.getAllowedValues());
                    }

                    map.put(property, comparable);
                }
                else if (!s1.isEmpty())
                {
                    throw new RuntimeException("Unknown blockstate property: '" + s1 + "'");
                }
            }
        }

        Block block = containerIn.getOwner();
        return (p_lambda$parseVariantKey$13_2_) ->
        {
            if (p_lambda$parseVariantKey$13_2_ != null && block == p_lambda$parseVariantKey$13_2_.getBlock())
            {
                for (Entry < Property<?>, Comparable<? >> entry : map.entrySet())
                {
                    if (!Objects.equals(p_lambda$parseVariantKey$13_2_.get(entry.getKey()), entry.getValue()))
                    {
                        return false;
                    }
                }

                return true;
            }
            else {
                return false;
            }
        };
    }

    @Nullable
    static <T extends Comparable<T>> T parseValue(Property<T> property, String value)
    {
        return property.parseValue(value).orElse((T)(null));
    }

    public IUnbakedModel getUnbakedModel(ResourceLocation modelLocation)
    {
        if (this.unbakedModels.containsKey(modelLocation))
        {
            return this.unbakedModels.get(modelLocation);
        }
        else if (this.unbakedModelLoadingQueue.contains(modelLocation))
        {
            throw new IllegalStateException("Circular reference while loading " + modelLocation);
        }
        else
        {
            this.unbakedModelLoadingQueue.add(modelLocation);
            IUnbakedModel iunbakedmodel = this.unbakedModels.get(MODEL_MISSING);

            while (!this.unbakedModelLoadingQueue.isEmpty())
            {
                ResourceLocation resourcelocation = this.unbakedModelLoadingQueue.iterator().next();

                try
                {
                    if (!this.unbakedModels.containsKey(resourcelocation))
                    {
                        this.loadBlockstate(resourcelocation);
                    }
                }
                catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception)
                {
                    LOGGER.warn(modelbakery$blockstatedefinitionexception.getMessage());
                    this.unbakedModels.put(resourcelocation, iunbakedmodel);
                }
                catch (Exception exception)
                {
                    LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", resourcelocation, modelLocation);
                    LOGGER.warn(exception.getClass().getName() + ": " + exception.getMessage());
                    this.unbakedModels.put(resourcelocation, iunbakedmodel);
                }
                finally
                {
                    this.unbakedModelLoadingQueue.remove(resourcelocation);
                }
            }

            return this.unbakedModels.getOrDefault(modelLocation, iunbakedmodel);
        }
    }

    private void loadBlockstate(ResourceLocation blockstateLocation) throws Exception
    {
        if (!(blockstateLocation instanceof ModelResourceLocation))
        {
            this.putModel(blockstateLocation, this.loadModel(blockstateLocation));
        }
        else
        {
            ModelResourceLocation modelresourcelocation = (ModelResourceLocation)blockstateLocation;

            if (Objects.equals(modelresourcelocation.getVariant(), "inventory"))
            {
                ResourceLocation resourcelocation2 = new ResourceLocation(blockstateLocation.getNamespace(), "item/" + blockstateLocation.getPath());
                String s = blockstateLocation.getPath();

                if (s.startsWith("optifine/") || s.startsWith("item/"))
                {
                    resourcelocation2 = blockstateLocation;
                }

                BlockModel blockmodel = this.loadModel(resourcelocation2);
                this.putModel(modelresourcelocation, blockmodel);
                this.unbakedModels.put(resourcelocation2, blockmodel);
            }
            else
            {
                ResourceLocation resourcelocation = new ResourceLocation(blockstateLocation.getNamespace(), blockstateLocation.getPath());
                StateContainer<Block, BlockState> statecontainer = Optional.ofNullable(STATE_CONTAINER_OVERRIDES.get(resourcelocation)).orElseGet(() ->
                {
                    return Registry.BLOCK.getOrDefault(resourcelocation).getStateContainer();
                });
                this.containerHolder.setStateContainer(statecontainer);
                List < Property<? >> list = ImmutableList.copyOf(this.blockColors.getColorProperties(statecontainer.getOwner()));
                ImmutableList<BlockState> immutablelist = statecontainer.getValidStates();
                Map<ModelResourceLocation, BlockState> map = Maps.newHashMap();
                immutablelist.forEach((p_lambda$loadBlockstate$15_2_) ->
                {
                    BlockState blockstate = map.put(BlockModelShapes.getModelLocation(resourcelocation, p_lambda$loadBlockstate$15_2_), p_lambda$loadBlockstate$15_2_);
                });
                Map<BlockState, Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>>> map1 = Maps.newHashMap();
                ResourceLocation resourcelocation1 = new ResourceLocation(blockstateLocation.getNamespace(), "blockstates/" + blockstateLocation.getPath() + ".json");
                IUnbakedModel iunbakedmodel = this.unbakedModels.get(MODEL_MISSING);
                ModelBakery.ModelListWrapper modelbakery$modellistwrapper = new ModelBakery.ModelListWrapper(ImmutableList.of(iunbakedmodel), ImmutableList.of());
                Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair = Pair.of(iunbakedmodel, () ->
                {
                    return modelbakery$modellistwrapper;
                });

                try
                {
                    List<Pair<String, BlockModelDefinition>> list1;

                    try
                    {
                        list1 = this.resourceManager.getAllResources(resourcelocation1).stream().map((p_lambda$loadBlockstate$17_1_) ->
                        {
                            try (InputStream inputstream = p_lambda$loadBlockstate$17_1_.getInputStream())
                            {
                                return Pair.of(p_lambda$loadBlockstate$17_1_.getPackName(), BlockModelDefinition.fromJson(this.containerHolder, new InputStreamReader(inputstream, StandardCharsets.UTF_8)));
                            }
                            catch (Exception exception11)
                            {
                                throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", p_lambda$loadBlockstate$17_1_.getLocation(), p_lambda$loadBlockstate$17_1_.getPackName(), exception11.getMessage()));
                            }
                        }).collect(Collectors.toList());
                    }
                    catch (IOException ioexception)
                    {
                        LOGGER.warn("Exception loading blockstate definition: {}: {}", resourcelocation1, ioexception);
                        return;
                    }

                    for (Pair<String, BlockModelDefinition> pair1 : list1)
                    {
                        BlockModelDefinition blockmodeldefinition = pair1.getSecond();
                        Map<BlockState, Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>>> map2 = Maps.newIdentityHashMap();
                        Multipart multipart;

                        if (blockmodeldefinition.hasMultipartData())
                        {
                            multipart = blockmodeldefinition.getMultipartData();
                            immutablelist.forEach((p_lambda$loadBlockstate$19_3_) ->
                            {
                                Pair pair2 = map2.put(p_lambda$loadBlockstate$19_3_, Pair.of(multipart, () -> {
                                    return ModelBakery.ModelListWrapper.makeWrapper(p_lambda$loadBlockstate$19_3_, multipart, list);
                                }));
                            });
                        }
                        else
                        {
                            multipart = null;
                        }

                        blockmodeldefinition.getVariants().forEach((p_lambda$loadBlockstate$23_9_, p_lambda$loadBlockstate$23_10_) ->
                        {
                            try {
                                immutablelist.stream().filter(parseVariantKey(statecontainer, p_lambda$loadBlockstate$23_9_)).forEach((p_lambda$null$22_6_) -> {
                                    Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = map2.put(p_lambda$null$22_6_, Pair.of(p_lambda$loadBlockstate$23_10_, () -> {
                                        return ModelBakery.ModelListWrapper.makeWrapper(p_lambda$null$22_6_, p_lambda$loadBlockstate$23_10_, list);
                                    }));

                                    if (pair2 != null && pair2.getFirst() != multipart)
                                    {
                                        map2.put(p_lambda$null$22_6_, pair);
                                        throw new RuntimeException("Overlapping definition with: " + (String)blockmodeldefinition.getVariants().entrySet().stream().filter((p_lambda$null$21_1_) ->
                                        {
                                            return p_lambda$null$21_1_.getValue() == pair2.getFirst();
                                        }).findFirst().get().getKey());
                                    }
                                });
                            }
                            catch (Exception exception1)
                            {
                                LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", resourcelocation1, pair1.getFirst(), p_lambda$loadBlockstate$23_9_, exception1.getMessage());
                            }
                        });
                        map1.putAll(map2);
                    }

                    return;
                }
                catch (ModelBakery.BlockStateDefinitionException modelbakery$blockstatedefinitionexception)
                {
                    throw modelbakery$blockstatedefinitionexception;
                }
                catch (Exception exception1)
                {
                    throw new ModelBakery.BlockStateDefinitionException(String.format("Exception loading blockstate definition: '%s': %s", resourcelocation1, exception1));
                }
                finally
                {
                    HashMap lvt_20_1_ = Maps.newHashMap();
                    map.forEach((p_lambda$loadBlockstate$25_5_, p_lambda$loadBlockstate$25_6_) ->
                    {
                        Pair<IUnbakedModel, Supplier<ModelBakery.ModelListWrapper>> pair2 = map1.get(p_lambda$loadBlockstate$25_6_);

                        if (pair2 == null)
                        {
                            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", resourcelocation1, p_lambda$loadBlockstate$25_5_);
                            pair2 = pair;
                        }

                        this.putModel(p_lambda$loadBlockstate$25_5_, pair2.getFirst());

                        try {
                            ModelBakery.ModelListWrapper modelbakery$modellistwrapper1 = pair2.getSecond().get();
                            ((Set)lvt_20_1_.computeIfAbsent(modelbakery$modellistwrapper1, (p_lambda$null$24_0_) -> {
                                return Sets.newIdentityHashSet();
                            })).add(p_lambda$loadBlockstate$25_6_);
                        }
                        catch (Exception exception11)
                        {
                            LOGGER.warn("Exception evaluating model definition: '{}'", p_lambda$loadBlockstate$25_5_, exception11);
                        }
                    });
                    lvt_20_1_.forEach((p_lambda$loadBlockstate$26_1_, p_lambda$loadBlockstate$26_2_) ->
                    {
                        Iterator<BlockState> iterator = ((Set)p_lambda$loadBlockstate$26_2_).iterator();

                        while (iterator.hasNext())
                        {
                            BlockState blockstate = iterator.next();

                            if (blockstate.getRenderType() != BlockRenderType.MODEL)
                            {
                                iterator.remove();
                                this.stateModelIds.put(blockstate, 0);
                            }
                        }

                        if (((Set)p_lambda$loadBlockstate$26_2_).size() > 1)
                        {
                            this.registerModelIds((Set)p_lambda$loadBlockstate$26_2_);
                        }
                    });
                }
            }
        }
    }

    private void putModel(ResourceLocation locationIn, IUnbakedModel modelIn)
    {
        this.unbakedModels.put(locationIn, modelIn);
        this.unbakedModelLoadingQueue.addAll(modelIn.getDependencies());
    }

    private void addModelToCache(ResourceLocation p_addModelToCache_1_)
    {
        IUnbakedModel iunbakedmodel = this.getUnbakedModel(p_addModelToCache_1_);
        this.unbakedModels.put(p_addModelToCache_1_, iunbakedmodel);
        this.topUnbakedModels.put(p_addModelToCache_1_, iunbakedmodel);
    }

    public void loadTopModel(ModelResourceLocation locationIn)
    {
        IUnbakedModel iunbakedmodel = this.getUnbakedModel(locationIn);
        this.unbakedModels.put(locationIn, iunbakedmodel);
        this.topUnbakedModels.put(locationIn, iunbakedmodel);
    }

    private void registerModelIds(Iterable<BlockState> blockStatesIn)
    {
        int i = this.counterModelId++;
        blockStatesIn.forEach((p_lambda$registerModelIds$27_2_) ->
        {
            this.stateModelIds.put(p_lambda$registerModelIds$27_2_, i);
        });
    }

    @Nullable
    public IBakedModel bake(ResourceLocation locationIn, IModelTransform transformIn)
    {
        return this.getBakedModel(locationIn, transformIn, this.spriteMap::getSprite);
    }

    public IBakedModel getBakedModel(ResourceLocation p_getBakedModel_1_, IModelTransform p_getBakedModel_2_, Function<RenderMaterial, TextureAtlasSprite> p_getBakedModel_3_)
    {
        Triple<ResourceLocation, TransformationMatrix, Boolean> triple = Triple.of(p_getBakedModel_1_, p_getBakedModel_2_.getRotation(), p_getBakedModel_2_.isUvLock());

        if (this.bakedModels.containsKey(triple))
        {
            return this.bakedModels.get(triple);
        }
        else if (this.spriteMap == null)
        {
            throw new IllegalStateException("bake called too early");
        }
        else
        {
            IUnbakedModel iunbakedmodel = this.getUnbakedModel(p_getBakedModel_1_);

            if (iunbakedmodel instanceof BlockModel)
            {
                BlockModel blockmodel = (BlockModel)iunbakedmodel;

                if (blockmodel.getRootModel() == MODEL_GENERATED)
                {
                    if (Reflector.ForgeHooksClient.exists())
                    {
                        return ITEM_MODEL_GENERATOR.makeItemModel(p_getBakedModel_3_, blockmodel).bakeModel(this, blockmodel, p_getBakedModel_3_, p_getBakedModel_2_, p_getBakedModel_1_, false);
                    }

                    return ITEM_MODEL_GENERATOR.makeItemModel(this.spriteMap::getSprite, blockmodel).bakeModel(this, blockmodel, this.spriteMap::getSprite, p_getBakedModel_2_, p_getBakedModel_1_, false);
                }
            }

            IBakedModel ibakedmodel = iunbakedmodel.bakeModel(this, this.spriteMap::getSprite, p_getBakedModel_2_, p_getBakedModel_1_);

            if (Reflector.ForgeHooksClient.exists())
            {
                ibakedmodel = iunbakedmodel.bakeModel(this, p_getBakedModel_3_, p_getBakedModel_2_, p_getBakedModel_1_);
            }

            this.bakedModels.put(triple, ibakedmodel);
            return ibakedmodel;
        }
    }

    private BlockModel loadModel(ResourceLocation location) throws IOException
    {
        Reader reader = null;
        IResource iresource = null;
        BlockModel basePath;

        try
        {
            String s = location.getPath();
            ResourceLocation resourcelocation = location;

            if ("builtin/generated".equals(s))
            {
                return MODEL_GENERATED;
            }

            if (!"builtin/entity".equals(s))
            {
                if (s.startsWith("builtin/"))
                {
                    String s2 = s.substring("builtin/".length());
                    String s1 = BUILT_IN_MODELS.get(s2);

                    if (s1 == null)
                    {
                        throw new FileNotFoundException(location.toString());
                    }

                    reader = new StringReader(s1);
                }
                else
                {
                    resourcelocation = this.getModelLocation(location);
                    iresource = this.resourceManager.getResource(resourcelocation);
                    reader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);
                }

                BlockModel blockmodel = BlockModel.deserialize(reader);
                blockmodel.name = location.toString();
                String s3 = TextureUtils.getBasePath(resourcelocation.getPath());
                fixModelLocations(blockmodel, s3);
                return blockmodel;
            }

            basePath = MODEL_ENTITY;
        }
        finally
        {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly((Closeable)iresource);
        }

        return basePath;
    }

    public Map<ResourceLocation, IBakedModel> getTopBakedModels()
    {
        return this.topBakedModels;
    }

    public Object2IntMap<BlockState> getStateModelIds()
    {
        return this.stateModelIds;
    }

    private ResourceLocation getModelLocation(ResourceLocation p_getModelLocation_1_)
    {
        String s = p_getModelLocation_1_.getPath();

        if (s.startsWith("optifine/"))
        {
            if (!s.endsWith(".json"))
            {
                p_getModelLocation_1_ = new ResourceLocation(p_getModelLocation_1_.getNamespace(), s + ".json");
            }

            return p_getModelLocation_1_;
        }
        else
        {
            return new ResourceLocation(p_getModelLocation_1_.getNamespace(), "models/" + p_getModelLocation_1_.getPath() + ".json");
        }
    }

    public static void fixModelLocations(BlockModel p_fixModelLocations_0_, String p_fixModelLocations_1_)
    {
        ResourceLocation resourcelocation = fixModelLocation(p_fixModelLocations_0_.parentLocation, p_fixModelLocations_1_);

        if (resourcelocation != p_fixModelLocations_0_.parentLocation)
        {
            p_fixModelLocations_0_.parentLocation = resourcelocation;
        }

        if (p_fixModelLocations_0_.textures != null)
        {
            for (Entry<String, Either<RenderMaterial, String>> entry : p_fixModelLocations_0_.textures.entrySet())
            {
                Either<RenderMaterial, String> either = entry.getValue();
                Optional<RenderMaterial> optional = either.left();

                if (optional.isPresent())
                {
                    RenderMaterial rendermaterial = optional.get();
                    ResourceLocation resourcelocation1 = rendermaterial.getTextureLocation();
                    String s = resourcelocation1.getPath();
                    String s1 = fixResourcePath(s, p_fixModelLocations_1_);

                    if (!s1.equals(s))
                    {
                        ResourceLocation resourcelocation2 = new ResourceLocation(resourcelocation1.getNamespace(), s1);
                        RenderMaterial rendermaterial1 = new RenderMaterial(rendermaterial.getAtlasLocation(), resourcelocation2);
                        Either<RenderMaterial, String> either1 = Either.left(rendermaterial1);
                        entry.setValue(either1);
                    }
                }
            }
        }
    }

    public static ResourceLocation fixModelLocation(ResourceLocation p_fixModelLocation_0_, String p_fixModelLocation_1_)
    {
        if (p_fixModelLocation_0_ != null && p_fixModelLocation_1_ != null)
        {
            if (!p_fixModelLocation_0_.getNamespace().equals("minecraft"))
            {
                return p_fixModelLocation_0_;
            }
            else
            {
                String s = p_fixModelLocation_0_.getPath();
                String s1 = fixResourcePath(s, p_fixModelLocation_1_);

                if (s1 != s)
                {
                    p_fixModelLocation_0_ = new ResourceLocation(p_fixModelLocation_0_.getNamespace(), s1);
                }

                return p_fixModelLocation_0_;
            }
        }
        else
        {
            return p_fixModelLocation_0_;
        }
    }

    private static String fixResourcePath(String p_fixResourcePath_0_, String p_fixResourcePath_1_)
    {
        p_fixResourcePath_0_ = TextureUtils.fixResourcePath(p_fixResourcePath_0_, p_fixResourcePath_1_);
        p_fixResourcePath_0_ = StrUtils.removeSuffix(p_fixResourcePath_0_, ".json");
        return StrUtils.removeSuffix(p_fixResourcePath_0_, ".png");
    }

    public Set<ResourceLocation> getSpecialModels()
    {
        return Collections.emptySet();
    }

    public SpriteMap getSpriteMap()
    {
        return this.spriteMap;
    }

    static class BlockStateDefinitionException extends RuntimeException
    {
        public BlockStateDefinitionException(String message)
        {
            super(message);
        }
    }

    static class ModelListWrapper
    {
        private final List<IUnbakedModel> models;
        private final List<Object> colorValues;

        public ModelListWrapper(List<IUnbakedModel> modelsIn, List<Object> colorValuesIn)
        {
            this.models = modelsIn;
            this.colorValues = colorValuesIn;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof ModelBakery.ModelListWrapper))
            {
                return false;
            }
            else
            {
                ModelBakery.ModelListWrapper modelbakery$modellistwrapper = (ModelBakery.ModelListWrapper)p_equals_1_;
                return Objects.equals(this.models, modelbakery$modellistwrapper.models) && Objects.equals(this.colorValues, modelbakery$modellistwrapper.colorValues);
            }
        }

        public int hashCode()
        {
            return 31 * this.models.hashCode() + this.colorValues.hashCode();
        }

        public static ModelBakery.ModelListWrapper makeWrapper(BlockState blockStateIn, Multipart multipartIn, Collection < Property<? >> propertiesIn)
        {
            StateContainer<Block, BlockState> statecontainer = blockStateIn.getBlock().getStateContainer();
            List<IUnbakedModel> list = multipartIn.getSelectors().stream().filter((p_lambda$makeWrapper$0_2_) ->
            {
                return p_lambda$makeWrapper$0_2_.getPredicate(statecontainer).test(blockStateIn);
            }).map(Selector::getVariantList).collect(ImmutableList.toImmutableList());
            List<Object> list1 = getColorValues(blockStateIn, propertiesIn);
            return new ModelBakery.ModelListWrapper(list, list1);
        }

        public static ModelBakery.ModelListWrapper makeWrapper(BlockState blockStateIn, IUnbakedModel modelIn, Collection < Property<? >> propertiesIn)
        {
            List<Object> list = getColorValues(blockStateIn, propertiesIn);
            return new ModelBakery.ModelListWrapper(ImmutableList.of(modelIn), list);
        }

        private static List<Object> getColorValues(BlockState blockStateIn, Collection < Property<? >> propertiesIn)
        {
            return propertiesIn.stream().map(blockStateIn::get).collect(ImmutableList.toImmutableList());
        }
    }
}
