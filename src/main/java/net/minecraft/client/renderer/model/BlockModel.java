package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockModel implements IUnbakedModel
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    @VisibleForTesting
    static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
    private final List<BlockPart> elements;
    @Nullable
    private final BlockModel.GuiLight guiLight3d;
    private final boolean ambientOcclusion;
    private final ItemCameraTransforms cameraTransforms;
    private final List<ItemOverride> overrides;
    public String name = "";
    @VisibleForTesting
    protected final Map<String, Either<RenderMaterial, String>> textures;
    @Nullable
    protected BlockModel parent;
    @Nullable
    protected ResourceLocation parentLocation;

    public static BlockModel deserialize(Reader readerIn)
    {
        return JSONUtils.fromJson(SERIALIZER, readerIn, BlockModel.class);
    }

    public static BlockModel deserialize(String jsonString)
    {
        return deserialize(new StringReader(jsonString));
    }

    public BlockModel(@Nullable ResourceLocation parentLocation, List<BlockPart> elements, Map<String, Either<RenderMaterial, String>> textures, boolean ambientOcclusion, @Nullable BlockModel.GuiLight guiLight3d, ItemCameraTransforms cameraTransforms, List<ItemOverride> overrides)
    {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.guiLight3d = guiLight3d;
        this.textures = textures;
        this.parentLocation = parentLocation;
        this.cameraTransforms = cameraTransforms;
        this.overrides = overrides;
    }

    public List<BlockPart> getElements()
    {
        return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
    }

    public boolean isAmbientOcclusion()
    {
        return this.parent != null ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
    }

    public BlockModel.GuiLight getGuiLight()
    {
        if (this.guiLight3d != null)
        {
            return this.guiLight3d;
        }
        else
        {
            return this.parent != null ? this.parent.getGuiLight() : BlockModel.GuiLight.SIDE;
        }
    }

    public List<ItemOverride> getOverrides()
    {
        return this.overrides;
    }

    private ItemOverrideList getItemOverrideList(ModelBakery modelBakeryIn, BlockModel modelIn)
    {
        return this.overrides.isEmpty() ? ItemOverrideList.EMPTY : new ItemOverrideList(modelBakeryIn, modelIn, modelBakeryIn::getUnbakedModel, this.overrides);
    }

    public Collection<ResourceLocation> getDependencies()
    {
        Set<ResourceLocation> set = Sets.newHashSet();

        for (ItemOverride itemoverride : this.overrides)
        {
            set.add(itemoverride.getLocation());
        }

        if (this.parentLocation != null)
        {
            set.add(this.parentLocation);
        }

        return set;
    }

    public Collection<RenderMaterial> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<IUnbakedModel> set = Sets.newLinkedHashSet();

        for (BlockModel blockmodel = this; blockmodel.parentLocation != null && blockmodel.parent == null; blockmodel = blockmodel.parent)
        {
            set.add(blockmodel);
            IUnbakedModel iunbakedmodel = modelGetter.apply(blockmodel.parentLocation);

            if (iunbakedmodel == null)
            {
                LOGGER.warn("No parent '{}' while loading model '{}'", this.parentLocation, blockmodel);
            }

            if (set.contains(iunbakedmodel))
            {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", blockmodel, set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentLocation);
                iunbakedmodel = null;
            }

            if (iunbakedmodel == null)
            {
                blockmodel.parentLocation = ModelBakery.MODEL_MISSING;
                iunbakedmodel = modelGetter.apply(blockmodel.parentLocation);
            }

            if (!(iunbakedmodel instanceof BlockModel))
            {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }

            blockmodel.parent = (BlockModel)iunbakedmodel;
        }

        Set<RenderMaterial> set1 = Sets.newHashSet(this.resolveTextureName("particle"));

        for (BlockPart blockpart : this.getElements())
        {
            for (BlockPartFace blockpartface : blockpart.mapFaces.values())
            {
                RenderMaterial rendermaterial = this.resolveTextureName(blockpartface.texture);

                if (Objects.equals(rendermaterial.getTextureLocation(), MissingTextureSprite.getLocation()))
                {
                    missingTextureErrors.add(Pair.of(blockpartface.texture, this.name));
                }

                set1.add(rendermaterial);
            }
        }

        this.overrides.forEach((override) ->
        {
            IUnbakedModel iunbakedmodel1 = modelGetter.apply(override.getLocation());

            if (!Objects.equals(iunbakedmodel1, this))
            {
                set1.addAll(iunbakedmodel1.getTextures(modelGetter, missingTextureErrors));
            }
        });

        if (this.getRootModel() == ModelBakery.MODEL_GENERATED)
        {
            ItemModelGenerator.LAYERS.forEach((layerName) ->
            {
                set1.add(this.resolveTextureName(layerName));
            });
        }

        return set1;
    }

    public IBakedModel bakeModel(ModelBakery modelBakeryIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn)
    {
        return this.bakeModel(modelBakeryIn, this, spriteGetterIn, transformIn, locationIn, true);
    }

    public IBakedModel bakeModel(ModelBakery modelBakeryIn, BlockModel modelIn, Function<RenderMaterial, TextureAtlasSprite> spriteGetterIn, IModelTransform transformIn, ResourceLocation locationIn, boolean guiLight3d)
    {
        TextureAtlasSprite textureatlassprite = spriteGetterIn.apply(this.resolveTextureName("particle"));

        if (this.getRootModel() == ModelBakery.MODEL_ENTITY)
        {
            return new BuiltInModel(this.getAllTransforms(), this.getItemOverrideList(modelBakeryIn, modelIn), textureatlassprite, this.getGuiLight().isSideLit());
        }
        else
        {
            SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(this, this.getItemOverrideList(modelBakeryIn, modelIn), guiLight3d)).setTexture(textureatlassprite);

            for (BlockPart blockpart : this.getElements())
            {
                for (Direction direction : blockpart.mapFaces.keySet())
                {
                    BlockPartFace blockpartface = blockpart.mapFaces.get(direction);
                    TextureAtlasSprite textureatlassprite1 = spriteGetterIn.apply(this.resolveTextureName(blockpartface.texture));

                    if (blockpartface.cullFace == null)
                    {
                        simplebakedmodel$builder.addGeneralQuad(bakeFace(blockpart, blockpartface, textureatlassprite1, direction, transformIn, locationIn));
                    }
                    else
                    {
                        simplebakedmodel$builder.addFaceQuad(Direction.rotateFace(transformIn.getRotation().getMatrix(), blockpartface.cullFace), bakeFace(blockpart, blockpartface, textureatlassprite1, direction, transformIn, locationIn));
                    }
                }
            }

            return simplebakedmodel$builder.build();
        }
    }

    private static BakedQuad bakeFace(BlockPart partIn, BlockPartFace partFaceIn, TextureAtlasSprite spriteIn, Direction directionIn, IModelTransform transformIn, ResourceLocation locationIn)
    {
        return FACE_BAKERY.bakeQuad(partIn.positionFrom, partIn.positionTo, partFaceIn, spriteIn, directionIn, transformIn, partIn.partRotation, partIn.shade, locationIn);
    }

    public boolean isTexturePresent(String textureName)
    {
        return !MissingTextureSprite.getLocation().equals(this.resolveTextureName(textureName).getTextureLocation());
    }

    public RenderMaterial resolveTextureName(String nameIn)
    {
        if (startsWithHash(nameIn))
        {
            nameIn = nameIn.substring(1);
        }

        List<String> list = Lists.newArrayList();

        while (true)
        {
            Either<RenderMaterial, String> either = this.findTexture(nameIn);
            Optional<RenderMaterial> optional = either.left();

            if (optional.isPresent())
            {
                return optional.get();
            }

            nameIn = either.right().get();

            if (list.contains(nameIn))
            {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(list), nameIn, this.name);
                return new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation());
            }

            list.add(nameIn);
        }
    }

    private Either<RenderMaterial, String> findTexture(String nameIn)
    {
        for (BlockModel blockmodel = this; blockmodel != null; blockmodel = blockmodel.parent)
        {
            Either<RenderMaterial, String> either = blockmodel.textures.get(nameIn);

            if (either != null)
            {
                return either;
            }
        }

        return Either.left(new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation()));
    }

    private static boolean startsWithHash(String strIn)
    {
        return strIn.charAt(0) == '#';
    }

    public BlockModel getRootModel()
    {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public ItemCameraTransforms getAllTransforms()
    {
        ItemTransformVec3f itemtransformvec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        ItemTransformVec3f itemtransformvec3f1 = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        ItemTransformVec3f itemtransformvec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        ItemTransformVec3f itemtransformvec3f3 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
        ItemTransformVec3f itemtransformvec3f4 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
        ItemTransformVec3f itemtransformvec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
        ItemTransformVec3f itemtransformvec3f6 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
        ItemTransformVec3f itemtransformvec3f7 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
        return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
    }

    private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
    {
        return this.parent != null && !this.cameraTransforms.hasCustomTransform(type) ? this.parent.getTransform(type) : this.cameraTransforms.getTransform(type);
    }

    public String toString()
    {
        return this.name;
    }

    public static class Deserializer implements JsonDeserializer<BlockModel>
    {
        public BlockModel deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            List<BlockPart> list = this.getModelElements(p_deserialize_3_, jsonobject);
            String s = this.getParent(jsonobject);
            Map<String, Either<RenderMaterial, String>> map = this.getTextures(jsonobject);
            boolean flag = this.getAmbientOcclusionEnabled(jsonobject);
            ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;

            if (jsonobject.has("display"))
            {
                JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonobject, "display");
                itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
            }

            List<ItemOverride> list1 = this.getItemOverrides(p_deserialize_3_, jsonobject);
            BlockModel.GuiLight blockmodel$guilight = null;

            if (jsonobject.has("gui_light"))
            {
                blockmodel$guilight = BlockModel.GuiLight.getLightFromName(JSONUtils.getString(jsonobject, "gui_light"));
            }

            ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
            return new BlockModel(resourcelocation, list, map, flag, blockmodel$guilight, itemcameratransforms, list1);
        }

        protected List<ItemOverride> getItemOverrides(JsonDeserializationContext deserializationContext, JsonObject object)
        {
            List<ItemOverride> list = Lists.newArrayList();

            if (object.has("overrides"))
            {
                for (JsonElement jsonelement : JSONUtils.getJsonArray(object, "overrides"))
                {
                    list.add(deserializationContext.deserialize(jsonelement, ItemOverride.class));
                }
            }

            return list;
        }

        private Map<String, Either<RenderMaterial, String>> getTextures(JsonObject object)
        {
            ResourceLocation resourcelocation = AtlasTexture.LOCATION_BLOCKS_TEXTURE;
            Map<String, Either<RenderMaterial, String>> map = Maps.newHashMap();

            if (object.has("textures"))
            {
                JsonObject jsonobject = JSONUtils.getJsonObject(object, "textures");

                for (Entry<String, JsonElement> entry : jsonobject.entrySet())
                {
                    map.put(entry.getKey(), findTexture(resourcelocation, entry.getValue().getAsString()));
                }
            }

            return map;
        }

        private static Either<RenderMaterial, String> findTexture(ResourceLocation locationIn, String nameIn)
        {
            if (BlockModel.startsWithHash(nameIn))
            {
                return Either.right(nameIn.substring(1));
            }
            else
            {
                ResourceLocation resourcelocation = ResourceLocation.tryCreate(nameIn);

                if (resourcelocation == null)
                {
                    throw new JsonParseException(nameIn + " is not valid resource location");
                }
                else
                {
                    return Either.left(new RenderMaterial(locationIn, resourcelocation));
                }
            }
        }

        private String getParent(JsonObject object)
        {
            return JSONUtils.getString(object, "parent", "");
        }

        protected boolean getAmbientOcclusionEnabled(JsonObject object)
        {
            return JSONUtils.getBoolean(object, "ambientocclusion", true);
        }

        protected List<BlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object)
        {
            List<BlockPart> list = Lists.newArrayList();

            if (object.has("elements"))
            {
                for (JsonElement jsonelement : JSONUtils.getJsonArray(object, "elements"))
                {
                    list.add(deserializationContext.deserialize(jsonelement, BlockPart.class));
                }
            }

            return list;
        }
    }

    public static enum GuiLight
    {
        FRONT("front"),
        SIDE("side");

        private final String name;

        private GuiLight(String name)
        {
            this.name = name;
        }

        public static BlockModel.GuiLight getLightFromName(String name)
        {
            for (BlockModel.GuiLight blockmodel$guilight : values())
            {
                if (blockmodel$guilight.name.equals(name))
                {
                    return blockmodel$guilight;
                }
            }

            throw new IllegalArgumentException("Invalid gui light: " + name);
        }

        public boolean isSideLit()
        {
            return this == SIDE;
        }
    }
}
