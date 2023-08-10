package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.optifine.Config;
import net.optifine.EmissiveTextures;
import net.optifine.SmartAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.ITextureFormat;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;
import net.optifine.shaders.ShadersTextureType;
import net.optifine.texture.ColorBlenderLinear;
import net.optifine.texture.IColorBlender;
import net.optifine.util.CounterInt;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AtlasTexture extends Texture implements ITickable
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Deprecated
    public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = PlayerContainer.LOCATION_BLOCKS_TEXTURE;

    @Deprecated
    public static final ResourceLocation LOCATION_PARTICLES_TEXTURE = new ResourceLocation("textures/atlas/particles.png");
    private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
    private final Set<ResourceLocation> sprites = Sets.newHashSet();
    private final Map<ResourceLocation, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
    private final ResourceLocation textureLocation;
    private final int maximumTextureSize;
    private Map<ResourceLocation, TextureAtlasSprite> mapRegisteredSprites = new LinkedHashMap<>();
    private Map<ResourceLocation, TextureAtlasSprite> mapMissingSprites = new LinkedHashMap<>();
    private TextureAtlasSprite[] iconGrid = null;
    private int iconGridSize = -1;
    private int iconGridCountX = -1;
    private int iconGridCountY = -1;
    private double iconGridSizeU = -1.0D;
    private double iconGridSizeV = -1.0D;
    private CounterInt counterIndexInMap = new CounterInt(0);
    public int atlasWidth = 0;
    public int atlasHeight = 0;
    public int mipmapLevel = 0;
    private int countAnimationsActive;
    private int frameCountAnimations;
    private boolean terrain;
    private boolean shaders;
    private boolean multiTexture;
    private ITextureFormat textureFormat;

    public AtlasTexture(ResourceLocation textureLocationIn)
    {
        this.textureLocation = textureLocationIn;
        this.maximumTextureSize = RenderSystem.maxSupportedTextureSize();
        this.terrain = textureLocationIn.equals(LOCATION_BLOCKS_TEXTURE);
        this.shaders = Config.isShaders();
        this.multiTexture = Config.isMultiTexture();

        if (this.terrain)
        {
            Config.setTextureMap(this);
        }
    }

    public void loadTexture(IResourceManager manager) throws IOException
    {
    }

    public void upload(AtlasTexture.SheetData sheetDataIn)
    {
        this.sprites.clear();
        this.sprites.addAll(sheetDataIn.spriteLocations);
        LOGGER.info("Created: {}x{}x{} {}-atlas", sheetDataIn.width, sheetDataIn.height, sheetDataIn.mipmapLevel, this.textureLocation);
        TextureUtil.prepareImage(this.getGlTextureId(), sheetDataIn.mipmapLevel, sheetDataIn.width, sheetDataIn.height);
        this.atlasWidth = sheetDataIn.width;
        this.atlasHeight = sheetDataIn.height;
        this.mipmapLevel = sheetDataIn.mipmapLevel;

        if (this.shaders)
        {
            ShadersTex.allocateTextureMapNS(sheetDataIn.mipmapLevel, sheetDataIn.width, sheetDataIn.height, this);
        }

        this.clear();

        for (TextureAtlasSprite textureatlassprite : sheetDataIn.sprites)
        {
            this.mapUploadedSprites.put(textureatlassprite.getName(), textureatlassprite);

            try
            {
                textureatlassprite.uploadMipmaps();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
                crashreportcategory.addDetail("Atlas path", this.textureLocation);
                crashreportcategory.addDetail("Sprite", textureatlassprite);
                throw new ReportedException(crashreport);
            }

            if (textureatlassprite.hasAnimationMetadata())
            {
                textureatlassprite.setAnimationIndex(this.listAnimatedSprites.size());
                this.listAnimatedSprites.add(textureatlassprite);
            }
        }

        TextureUtils.refreshCustomSprites(this);
        Config.log("Animated sprites: " + this.listAnimatedSprites.size());

        if (Config.isMultiTexture())
        {
            for (TextureAtlasSprite textureatlassprite1 : sheetDataIn.sprites)
            {
                uploadMipmapsSingle(textureatlassprite1);

                if (textureatlassprite1.spriteNormal != null)
                {
                    uploadMipmapsSingle(textureatlassprite1.spriteNormal);
                }

                if (textureatlassprite1.spriteSpecular != null)
                {
                    uploadMipmapsSingle(textureatlassprite1.spriteSpecular);
                }
            }

            GlStateManager.bindTexture(this.getGlTextureId());
        }

        if (Config.isShaders())
        {
            List list = sheetDataIn.sprites;

            if (Shaders.configNormalMap)
            {
                GlStateManager.bindTexture(this.getMultiTexID().norm);

                for (TextureAtlasSprite textureatlassprite2 : (List<TextureAtlasSprite>) list)
                {
                    TextureAtlasSprite textureatlassprite4 = textureatlassprite2.spriteNormal;

                    if (textureatlassprite4 != null)
                    {
                        textureatlassprite4.uploadMipmaps();
                    }
                }
            }

            if (Shaders.configSpecularMap)
            {
                GlStateManager.bindTexture(this.getMultiTexID().spec);

                for (TextureAtlasSprite textureatlassprite3 : (List<TextureAtlasSprite>) list)
                {
                    TextureAtlasSprite textureatlassprite5 = textureatlassprite3.spriteSpecular;

                    if (textureatlassprite5 != null)
                    {
                        textureatlassprite5.uploadMipmaps();
                    }
                }
            }

            GlStateManager.bindTexture(this.getGlTextureId());
        }

        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, this);
        this.updateIconGrid(sheetDataIn.width, sheetDataIn.height);

        if (Config.equals(System.getProperty("saveTextureMap"), "true"))
        {
            Config.dbg("Exporting texture map: " + this.textureLocation);
            TextureUtils.saveGlTexture("debug/" + this.textureLocation.getPath().replaceAll("/", "_"), this.getGlTextureId(), sheetDataIn.mipmapLevel, sheetDataIn.width, sheetDataIn.height);

            if (this.shaders)
            {
                if (Shaders.configNormalMap)
                {
                    TextureUtils.saveGlTexture("debug/" + this.textureLocation.getPath().replaceAll("/", "_").replace(".png", "_n.png"), this.multiTex.norm, sheetDataIn.mipmapLevel, sheetDataIn.width, sheetDataIn.height);
                }

                if (Shaders.configSpecularMap)
                {
                    TextureUtils.saveGlTexture("debug/" + this.textureLocation.getPath().replaceAll("/", "_").replace(".png", "_s.png"), this.multiTex.spec, sheetDataIn.mipmapLevel, sheetDataIn.width, sheetDataIn.height);
                }

                GlStateManager.bindTexture(this.getGlTextureId());
            }
        }
    }

    public AtlasTexture.SheetData stitch(IResourceManager resourceManagerIn, Stream<ResourceLocation> resourceLocationsIn, IProfiler profilerIn, int maxMipmapLevelIn)
    {
        this.terrain = this.textureLocation.equals(LOCATION_BLOCKS_TEXTURE);
        this.shaders = Config.isShaders();
        this.multiTexture = Config.isMultiTexture();
        this.textureFormat = ITextureFormat.readConfiguration();
        int i = maxMipmapLevelIn;
        this.mapRegisteredSprites.clear();
        this.mapMissingSprites.clear();
        this.counterIndexInMap.reset();
        profilerIn.startSection("preparing");
        Set<ResourceLocation> set = resourceLocationsIn.peek((p_lambda$stitch$0_0_) ->
        {
            if (p_lambda$stitch$0_0_ == null)
            {
                throw new IllegalArgumentException("Location cannot be null!");
            }
        }).collect(Collectors.toSet());
        Config.dbg("Multitexture: " + Config.isMultiTexture());
        TextureUtils.registerCustomSprites(this);
        set.addAll(this.mapRegisteredSprites.keySet());
        Set<ResourceLocation> set1 = newHashSet(set, this.mapRegisteredSprites.keySet());
        EmissiveTextures.updateIcons(this, set1);
        set.addAll(this.mapRegisteredSprites.keySet());

        if (maxMipmapLevelIn >= 4)
        {
            i = this.detectMaxMipmapLevel(set, resourceManagerIn);
            Config.log("Mipmap levels: " + i);
        }

        int j = TextureUtils.getGLMaximumTextureSize();
        Stitcher stitcher = new Stitcher(j, j, maxMipmapLevelIn);
        int k = Integer.MAX_VALUE;
        int l = getMinSpriteSize(i);
        this.iconGridSize = l;
        int i1 = 1 << maxMipmapLevelIn;
        profilerIn.endStartSection("extracting_frames");
        Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, this, set);

        for (TextureAtlasSprite.Info textureatlassprite$info : this.makeSprites(resourceManagerIn, set))
        {
            int j1 = textureatlassprite$info.getSpriteWidth();
            int k1 = textureatlassprite$info.getSpriteHeight();

            if (j1 >= 1 && k1 >= 1)
            {
                if (j1 < l || i > 0)
                {
                    int l1 = i > 0 ? TextureUtils.scaleToGrid(j1, l) : TextureUtils.scaleToMin(j1, l);

                    if (l1 != j1)
                    {
                        if (!TextureUtils.isPowerOfTwo(j1))
                        {
                            Config.log("Scaled non power of 2: " + textureatlassprite$info.getSpriteLocation() + ", " + j1 + " -> " + l1);
                        }
                        else
                        {
                            Config.log("Scaled too small texture: " + textureatlassprite$info.getSpriteLocation() + ", " + j1 + " -> " + l1);
                        }

                        int i2 = k1 * l1 / j1;
                        textureatlassprite$info.setSpriteWidth(l1);
                        textureatlassprite$info.setSpriteHeight(i2);
                        textureatlassprite$info.setScaleFactor((double)l1 * 1.0D / (double)j1);
                    }
                }

                k = Math.min(k, Math.min(textureatlassprite$info.getSpriteWidth(), textureatlassprite$info.getSpriteHeight()));
                int i3 = Math.min(Integer.lowestOneBit(textureatlassprite$info.getSpriteWidth()), Integer.lowestOneBit(textureatlassprite$info.getSpriteHeight()));

                if (i3 < i1)
                {
                    LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", textureatlassprite$info.getSpriteLocation(), textureatlassprite$info.getSpriteWidth(), textureatlassprite$info.getSpriteHeight(), MathHelper.log2(i1), MathHelper.log2(i3));
                    i1 = i3;
                }

                stitcher.addSprite(textureatlassprite$info);
            }
            else
            {
                Config.warn("Invalid sprite size: " + textureatlassprite$info.getSpriteLocation());
            }
        }

        int j2 = Math.min(k, i1);
        int k2 = MathHelper.log2(j2);

        if (k2 < 0)
        {
            k2 = 0;
        }

        int l2;

        if (k2 < maxMipmapLevelIn)
        {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.textureLocation, maxMipmapLevelIn, k2, j2);
            l2 = k2;
        }
        else
        {
            l2 = maxMipmapLevelIn;
        }

        profilerIn.endStartSection("register");
        TextureAtlasSprite.Info textureatlassprite$info1 = fixSpriteSize(MissingTextureSprite.getSpriteInfo(), l);
        stitcher.addSprite(textureatlassprite$info1);
        profilerIn.endStartSection("stitching");

        try
        {
            stitcher.doStitch();
        }
        catch (StitcherException stitcherexception)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(stitcherexception, "Stitching");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Stitcher");
            crashreportcategory.addDetail("Sprites", stitcherexception.getSpriteInfos().stream().map((p_lambda$stitch$1_0_) ->
            {
                return String.format("%s[%dx%d]", p_lambda$stitch$1_0_.getSpriteLocation(), p_lambda$stitch$1_0_.getSpriteWidth(), p_lambda$stitch$1_0_.getSpriteHeight());
            }).collect(Collectors.joining(",")));
            crashreportcategory.addDetail("Max Texture Size", j);
            throw new ReportedException(crashreport);
        }

        profilerIn.endStartSection("loading");
        List<TextureAtlasSprite> list = this.getStitchedSprites(resourceManagerIn, stitcher, l2);
        profilerIn.endSection();
        return new AtlasTexture.SheetData(set, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), l2, list);
    }

    private Collection<TextureAtlasSprite.Info> makeSprites(IResourceManager resourceManagerIn, Set<ResourceLocation> spriteLocationsIn)
    {
        List < CompletableFuture<? >> list = Lists.newArrayList();
        ConcurrentLinkedQueue<TextureAtlasSprite.Info> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();

        for (ResourceLocation resourcelocation : spriteLocationsIn)
        {
            if (!MissingTextureSprite.getLocation().equals(resourcelocation))
            {
                list.add(CompletableFuture.runAsync(() ->
                {
                    ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

                    TextureAtlasSprite.Info textureatlassprite$info;

                    try (IResource iresource = resourceManagerIn.getResource(resourcelocation1))
                    {
                        PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource.toString(), iresource.getInputStream());
                        AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);

                        if (animationmetadatasection == null)
                        {
                            animationmetadatasection = AnimationMetadataSection.EMPTY;
                        }

                        Pair<Integer, Integer> pair = animationmetadatasection.getSpriteSize(pngsizeinfo.width, pngsizeinfo.height);
                        textureatlassprite$info = new TextureAtlasSprite.Info(resourcelocation, pair.getFirst(), pair.getSecond(), animationmetadatasection);
                    }
                    catch (RuntimeException runtimeexception)
                    {
                        LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
                        this.onSpriteMissing(resourcelocation);
                        return;
                    }
                    catch (IOException ioexception1)
                    {
                        LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception1);
                        this.onSpriteMissing(resourcelocation);
                        return;
                    }

                    concurrentlinkedqueue.add(textureatlassprite$info);
                }, Util.getServerExecutor()));
            }
        }

        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return concurrentlinkedqueue;
    }

    private List<TextureAtlasSprite> getStitchedSprites(IResourceManager resourceManagerIn, Stitcher stitcherIn, int mipmapLevelIn)
    {
        ConcurrentLinkedQueue<TextureAtlasSprite> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();
        List < CompletableFuture<? >> list = Lists.newArrayList();
        stitcherIn.getStitchSlots((p_lambda$getStitchedSprites$4_5_, p_lambda$getStitchedSprites$4_6_, p_lambda$getStitchedSprites$4_7_, p_lambda$getStitchedSprites$4_8_, p_lambda$getStitchedSprites$4_9_) ->
        {
            if (p_lambda$getStitchedSprites$4_5_.getSpriteLocation().equals(MissingTextureSprite.getSpriteInfo().getSpriteLocation()))
            {
                MissingTextureSprite missingtexturesprite = new MissingTextureSprite(this, p_lambda$getStitchedSprites$4_5_, mipmapLevelIn, p_lambda$getStitchedSprites$4_6_, p_lambda$getStitchedSprites$4_7_, p_lambda$getStitchedSprites$4_8_, p_lambda$getStitchedSprites$4_9_);
                missingtexturesprite.update(resourceManagerIn);
                concurrentlinkedqueue.add(missingtexturesprite);
            }
            else {
                list.add(CompletableFuture.runAsync(() -> {
                    TextureAtlasSprite textureatlassprite = this.loadSprite(resourceManagerIn, p_lambda$getStitchedSprites$4_5_, p_lambda$getStitchedSprites$4_6_, p_lambda$getStitchedSprites$4_7_, mipmapLevelIn, p_lambda$getStitchedSprites$4_8_, p_lambda$getStitchedSprites$4_9_);

                    if (textureatlassprite != null)
                    {
                        concurrentlinkedqueue.add(textureatlassprite);
                    }
                }, Util.getServerExecutor()));
            }
        });
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(concurrentlinkedqueue);
    }

    @Nullable
    private TextureAtlasSprite loadSprite(IResourceManager resourceManagerIn, TextureAtlasSprite.Info spriteInfoIn, int widthIn, int heightIn, int mipmapLevelIn, int originX, int originY)
    {
        ResourceLocation resourcelocation = this.getSpritePath(spriteInfoIn.getSpriteLocation());

        try (IResource iresource = resourceManagerIn.getResource(resourcelocation))
        {
            NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
            TextureAtlasSprite textureatlassprite = new TextureAtlasSprite(this, spriteInfoIn, mipmapLevelIn, widthIn, heightIn, originX, originY, nativeimage);
            textureatlassprite.update(resourceManagerIn);
            return textureatlassprite;
        }
        catch (RuntimeException runtimeexception)
        {
            LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
            return null;
        }
        catch (IOException ioexception1)
        {
            LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception1);
            return null;
        }
    }

    public ResourceLocation getSpritePath(ResourceLocation location)
    {
        return this.isAbsoluteLocation(location) ? new ResourceLocation(location.getNamespace(), location.getPath() + ".png") : new ResourceLocation(location.getNamespace(), String.format("textures/%s%s", location.getPath(), ".png"));
    }

    public void updateAnimations()
    {
        boolean flag = false;
        boolean flag1 = false;

        if (!this.listAnimatedSprites.isEmpty())
        {
            this.bindTexture();
        }

        int i = 0;

        for (TextureAtlasSprite textureatlassprite : this.listAnimatedSprites)
        {
            if (this.isAnimationEnabled(textureatlassprite))
            {
                textureatlassprite.updateAnimation();

                if (textureatlassprite.isAnimationActive())
                {
                    ++i;
                }

                if (textureatlassprite.spriteNormal != null)
                {
                    flag = true;
                }

                if (textureatlassprite.spriteSpecular != null)
                {
                    flag1 = true;
                }
            }
        }

        if (Config.isShaders())
        {
            if (flag)
            {
                GlStateManager.bindTexture(this.getMultiTexID().norm);

                for (TextureAtlasSprite textureatlassprite1 : this.listAnimatedSprites)
                {
                    if (textureatlassprite1.spriteNormal != null && this.isAnimationEnabled(textureatlassprite1) && textureatlassprite1.isAnimationActive())
                    {
                        textureatlassprite1.spriteNormal.updateAnimation();

                        if (textureatlassprite1.spriteNormal.isAnimationActive())
                        {
                            ++i;
                        }
                    }
                }
            }

            if (flag1)
            {
                GlStateManager.bindTexture(this.getMultiTexID().spec);

                for (TextureAtlasSprite textureatlassprite2 : this.listAnimatedSprites)
                {
                    if (textureatlassprite2.spriteSpecular != null && this.isAnimationEnabled(textureatlassprite2) && textureatlassprite2.isAnimationActive())
                    {
                        textureatlassprite2.spriteSpecular.updateAnimation();

                        if (textureatlassprite2.spriteSpecular.isAnimationActive())
                        {
                            ++i;
                        }
                    }
                }
            }

            if (flag || flag1)
            {
                GlStateManager.bindTexture(this.getGlTextureId());
            }
        }

        if (Config.isMultiTexture())
        {
            for (TextureAtlasSprite textureatlassprite3 : this.listAnimatedSprites)
            {
                if (this.isAnimationEnabled(textureatlassprite3) && textureatlassprite3.isAnimationActive())
                {
                    i += updateAnimationSingle(textureatlassprite3);

                    if (textureatlassprite3.spriteNormal != null)
                    {
                        i += updateAnimationSingle(textureatlassprite3.spriteNormal);
                    }

                    if (textureatlassprite3.spriteSpecular != null)
                    {
                        i += updateAnimationSingle(textureatlassprite3.spriteSpecular);
                    }
                }
            }

            GlStateManager.bindTexture(this.getGlTextureId());
        }

        if (this.terrain)
        {
            int j = Config.getMinecraft().worldRenderer.getFrameCount();

            if (j != this.frameCountAnimations)
            {
                this.countAnimationsActive = i;
                this.frameCountAnimations = j;
            }

            if (SmartAnimations.isActive())
            {
                SmartAnimations.resetSpritesRendered(this);
            }
        }
    }

    public void tick()
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(this::updateAnimations);
        }
        else
        {
            this.updateAnimations();
        }
    }

    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(location);
        return textureatlassprite == null ? this.mapUploadedSprites.get(MissingTextureSprite.getLocation()) : textureatlassprite;
    }

    public void clear()
    {
        for (TextureAtlasSprite textureatlassprite : this.mapUploadedSprites.values())
        {
            textureatlassprite.close();
        }

        if (this.multiTexture)
        {
            for (TextureAtlasSprite textureatlassprite1 : this.mapUploadedSprites.values())
            {
                textureatlassprite1.deleteSpriteTexture();

                if (textureatlassprite1.spriteNormal != null)
                {
                    textureatlassprite1.spriteNormal.deleteSpriteTexture();
                }

                if (textureatlassprite1.spriteSpecular != null)
                {
                    textureatlassprite1.spriteSpecular.deleteSpriteTexture();
                }
            }
        }

        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
    }

    public ResourceLocation getTextureLocation()
    {
        return this.textureLocation;
    }

    public void setBlurMipmap(AtlasTexture.SheetData sheetDataIn)
    {
        this.setBlurMipmapDirect(false, sheetDataIn.mipmapLevel > 0);
    }

    private boolean isAbsoluteLocation(ResourceLocation p_isAbsoluteLocation_1_)
    {
        String s = p_isAbsoluteLocation_1_.getPath();
        return this.isAbsoluteLocationPath(s);
    }

    private boolean isAbsoluteLocationPath(String p_isAbsoluteLocationPath_1_)
    {
        String s = p_isAbsoluteLocationPath_1_.toLowerCase();
        return s.startsWith("optifine/");
    }

    public TextureAtlasSprite getRegisteredSprite(String p_getRegisteredSprite_1_)
    {
        ResourceLocation resourcelocation = new ResourceLocation(p_getRegisteredSprite_1_);
        return this.getRegisteredSprite(resourcelocation);
    }

    public TextureAtlasSprite getRegisteredSprite(ResourceLocation p_getRegisteredSprite_1_)
    {
        return this.mapRegisteredSprites.get(p_getRegisteredSprite_1_);
    }

    public TextureAtlasSprite getUploadedSprite(String p_getUploadedSprite_1_)
    {
        ResourceLocation resourcelocation = new ResourceLocation(p_getUploadedSprite_1_);
        return this.getUploadedSprite(resourcelocation);
    }

    public TextureAtlasSprite getUploadedSprite(ResourceLocation p_getUploadedSprite_1_)
    {
        return this.mapUploadedSprites.get(p_getUploadedSprite_1_);
    }

    private boolean isAnimationEnabled(TextureAtlasSprite p_isAnimationEnabled_1_)
    {
        if (!this.terrain)
        {
            return true;
        }
        else if (p_isAnimationEnabled_1_ != TextureUtils.iconWaterStill && p_isAnimationEnabled_1_ != TextureUtils.iconWaterFlow)
        {
            if (p_isAnimationEnabled_1_ != TextureUtils.iconLavaStill && p_isAnimationEnabled_1_ != TextureUtils.iconLavaFlow)
            {
                if (p_isAnimationEnabled_1_ != TextureUtils.iconFireLayer0 && p_isAnimationEnabled_1_ != TextureUtils.iconFireLayer1)
                {
                    if (p_isAnimationEnabled_1_ != TextureUtils.iconSoulFireLayer0 && p_isAnimationEnabled_1_ != TextureUtils.iconSoulFireLayer1)
                    {
                        if (p_isAnimationEnabled_1_ != TextureUtils.iconCampFire && p_isAnimationEnabled_1_ != TextureUtils.iconCampFireLogLit)
                        {
                            if (p_isAnimationEnabled_1_ != TextureUtils.iconSoulCampFire && p_isAnimationEnabled_1_ != TextureUtils.iconSoulCampFireLogLit)
                            {
                                return p_isAnimationEnabled_1_ == TextureUtils.iconPortal ? Config.isAnimatedPortal() : Config.isAnimatedTerrain();
                            }
                            else
                            {
                                return Config.isAnimatedFire();
                            }
                        }
                        else
                        {
                            return Config.isAnimatedFire();
                        }
                    }
                    else
                    {
                        return Config.isAnimatedFire();
                    }
                }
                else
                {
                    return Config.isAnimatedFire();
                }
            }
            else
            {
                return Config.isAnimatedLava();
            }
        }
        else
        {
            return Config.isAnimatedWater();
        }
    }

    private static void uploadMipmapsSingle(TextureAtlasSprite p_uploadMipmapsSingle_0_)
    {
        TextureAtlasSprite textureatlassprite = p_uploadMipmapsSingle_0_.spriteSingle;

        if (textureatlassprite != null)
        {
            textureatlassprite.setAnimationIndex(p_uploadMipmapsSingle_0_.getAnimationIndex());
            p_uploadMipmapsSingle_0_.bindSpriteTexture();

            try
            {
                textureatlassprite.uploadMipmaps();
            }
            catch (Exception exception)
            {
                Config.dbg("Error uploading sprite single: " + textureatlassprite + ", parent: " + p_uploadMipmapsSingle_0_);
                exception.printStackTrace();
            }
        }
    }

    private static int updateAnimationSingle(TextureAtlasSprite p_updateAnimationSingle_0_)
    {
        TextureAtlasSprite textureatlassprite = p_updateAnimationSingle_0_.spriteSingle;

        if (textureatlassprite != null)
        {
            p_updateAnimationSingle_0_.bindSpriteTexture();
            textureatlassprite.updateAnimation();

            if (textureatlassprite.isAnimationActive())
            {
                return 1;
            }
        }

        return 0;
    }

    public int getCountRegisteredSprites()
    {
        return this.counterIndexInMap.getValue();
    }

    private int detectMaxMipmapLevel(Set<ResourceLocation> p_detectMaxMipmapLevel_1_, IResourceManager p_detectMaxMipmapLevel_2_)
    {
        int i = this.detectMinimumSpriteSize(p_detectMaxMipmapLevel_1_, p_detectMaxMipmapLevel_2_, 20);

        if (i < 16)
        {
            i = 16;
        }

        i = MathHelper.smallestEncompassingPowerOfTwo(i);

        if (i > 16)
        {
            Config.log("Sprite size: " + i);
        }

        int j = MathHelper.log2(i);

        if (j < 4)
        {
            j = 4;
        }

        return j;
    }

    private int detectMinimumSpriteSize(Set<ResourceLocation> p_detectMinimumSpriteSize_1_, IResourceManager p_detectMinimumSpriteSize_2_, int p_detectMinimumSpriteSize_3_)
    {
        Map map = new HashMap();

        for (ResourceLocation resourcelocation : p_detectMinimumSpriteSize_1_)
        {
            ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

            try
            {
                IResource iresource = p_detectMinimumSpriteSize_2_.getResource(resourcelocation1);

                if (iresource != null)
                {
                    InputStream inputstream = iresource.getInputStream();

                    if (inputstream != null)
                    {
                        Dimension dimension = TextureUtils.getImageSize(inputstream, "png");
                        inputstream.close();

                        if (dimension != null)
                        {
                            int i = dimension.width;
                            int j = MathHelper.smallestEncompassingPowerOfTwo(i);

                            if (!map.containsKey(j))
                            {
                                map.put(j, 1);
                            }
                            else
                            {
                                int k = (int) map.get(j);
                                map.put(j, k + 1);
                            }
                        }
                    }
                }
            }
            catch (Exception exception)
            {
            }
        }

        int l = 0;
        Set<Integer> set = map.keySet();
        Set<Integer> set1 = new TreeSet(set);

        for (int j1 : set1)
        {
            int l1 = (int) map.get(j1);
            l += l1;
        }

        int i1 = 16;
        int k1 = 0;
        int i2 = l * p_detectMinimumSpriteSize_3_ / 100;

        for (int j2 : set1)
        {
            int k2 = (int) map.get(j2);
            k1 += k2;

            if (j2 > i1)
            {
                i1 = j2;
            }

            if (k1 > i2)
            {
                return i1;
            }
        }

        return i1;
    }

    private static int getMinSpriteSize(int p_getMinSpriteSize_0_)
    {
        int i = 1 << p_getMinSpriteSize_0_;

        if (i < 8)
        {
            i = 8;
        }

        return i;
    }

    private static TextureAtlasSprite.Info fixSpriteSize(TextureAtlasSprite.Info p_fixSpriteSize_0_, int p_fixSpriteSize_1_)
    {
        if (p_fixSpriteSize_0_.getSpriteWidth() >= p_fixSpriteSize_1_ && p_fixSpriteSize_0_.getSpriteHeight() >= p_fixSpriteSize_1_)
        {
            return p_fixSpriteSize_0_;
        }
        else
        {
            int i = Math.max(p_fixSpriteSize_0_.getSpriteWidth(), p_fixSpriteSize_1_);
            int j = Math.max(p_fixSpriteSize_0_.getSpriteHeight(), p_fixSpriteSize_1_);
            return new TextureAtlasSprite.Info(p_fixSpriteSize_0_.getSpriteLocation(), i, j, p_fixSpriteSize_0_.getSpriteAnimationMetadata());
        }
    }

    public boolean isTextureBound()
    {
        int i = GlStateManager.getBoundTexture();
        int j = this.getGlTextureId();
        return i == j;
    }

    private void updateIconGrid(int p_updateIconGrid_1_, int p_updateIconGrid_2_)
    {
        this.iconGridCountX = -1;
        this.iconGridCountY = -1;
        this.iconGrid = null;

        if (this.iconGridSize > 0)
        {
            this.iconGridCountX = p_updateIconGrid_1_ / this.iconGridSize;
            this.iconGridCountY = p_updateIconGrid_2_ / this.iconGridSize;
            this.iconGrid = new TextureAtlasSprite[this.iconGridCountX * this.iconGridCountY];
            this.iconGridSizeU = 1.0D / (double)this.iconGridCountX;
            this.iconGridSizeV = 1.0D / (double)this.iconGridCountY;

            for (TextureAtlasSprite textureatlassprite : this.mapUploadedSprites.values())
            {
                double d0 = 0.5D / (double)p_updateIconGrid_1_;
                double d1 = 0.5D / (double)p_updateIconGrid_2_;
                double d2 = (double)Math.min(textureatlassprite.getMinU(), textureatlassprite.getMaxU()) + d0;
                double d3 = (double)Math.min(textureatlassprite.getMinV(), textureatlassprite.getMaxV()) + d1;
                double d4 = (double)Math.max(textureatlassprite.getMinU(), textureatlassprite.getMaxU()) - d0;
                double d5 = (double)Math.max(textureatlassprite.getMinV(), textureatlassprite.getMaxV()) - d1;
                int i = (int)(d2 / this.iconGridSizeU);
                int j = (int)(d3 / this.iconGridSizeV);
                int k = (int)(d4 / this.iconGridSizeU);
                int l = (int)(d5 / this.iconGridSizeV);

                for (int i1 = i; i1 <= k; ++i1)
                {
                    if (i1 >= 0 && i1 < this.iconGridCountX)
                    {
                        for (int j1 = j; j1 <= l; ++j1)
                        {
                            if (j1 >= 0 && j1 < this.iconGridCountX)
                            {
                                int k1 = j1 * this.iconGridCountX + i1;
                                this.iconGrid[k1] = textureatlassprite;
                            }
                            else
                            {
                                Config.warn("Invalid grid V: " + j1 + ", icon: " + textureatlassprite.getName());
                            }
                        }
                    }
                    else
                    {
                        Config.warn("Invalid grid U: " + i1 + ", icon: " + textureatlassprite.getName());
                    }
                }
            }
        }
    }

    public TextureAtlasSprite getIconByUV(double p_getIconByUV_1_, double p_getIconByUV_3_)
    {
        if (this.iconGrid == null)
        {
            return null;
        }
        else
        {
            int i = (int)(p_getIconByUV_1_ / this.iconGridSizeU);
            int j = (int)(p_getIconByUV_3_ / this.iconGridSizeV);
            int k = j * this.iconGridCountX + i;
            return k >= 0 && k <= this.iconGrid.length ? this.iconGrid[k] : null;
        }
    }

    public int getCountAnimations()
    {
        return this.listAnimatedSprites.size();
    }

    public int getCountAnimationsActive()
    {
        return this.countAnimationsActive;
    }

    public TextureAtlasSprite registerSprite(ResourceLocation p_registerSprite_1_)
    {
        if (p_registerSprite_1_ == null)
        {
            throw new IllegalArgumentException("Location cannot be null!");
        }
        else
        {
            TextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(p_registerSprite_1_);

            if (textureatlassprite != null)
            {
                return textureatlassprite;
            }
            else
            {
                this.sprites.add(p_registerSprite_1_);
                textureatlassprite = new TextureAtlasSprite(p_registerSprite_1_);
                this.mapRegisteredSprites.put(p_registerSprite_1_, textureatlassprite);
                textureatlassprite.updateIndexInMap(this.counterIndexInMap);
                return textureatlassprite;
            }
        }
    }

    public Collection<TextureAtlasSprite> getRegisteredSprites()
    {
        return Collections.unmodifiableCollection(this.mapRegisteredSprites.values());
    }

    public boolean isTerrain()
    {
        return this.terrain;
    }

    public CounterInt getCounterIndexInMap()
    {
        return this.counterIndexInMap;
    }

    private void onSpriteMissing(ResourceLocation p_onSpriteMissing_1_)
    {
        TextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(p_onSpriteMissing_1_);

        if (textureatlassprite != null)
        {
            this.mapMissingSprites.put(p_onSpriteMissing_1_, textureatlassprite);
        }
    }

    private static <T> Set<T> newHashSet(Set<T> p_newHashSet_0_, Set<T> p_newHashSet_1_)
    {
        Set<T> set = new HashSet<>();
        set.addAll(p_newHashSet_0_);
        set.addAll(p_newHashSet_1_);
        return set;
    }

    public int getMipmapLevel()
    {
        return this.mipmapLevel;
    }

    public boolean isMipmaps()
    {
        return this.mipmapLevel > 0;
    }

    public ITextureFormat getTextureFormat()
    {
        return this.textureFormat;
    }

    public IColorBlender getShadersColorBlender(ShadersTextureType p_getShadersColorBlender_1_)
    {
        if (p_getShadersColorBlender_1_ == null)
        {
            return null;
        }
        else
        {
            return (IColorBlender)(this.textureFormat != null ? this.textureFormat.getColorBlender(p_getShadersColorBlender_1_) : new ColorBlenderLinear());
        }
    }

    public boolean isTextureBlend(ShadersTextureType p_isTextureBlend_1_)
    {
        if (p_isTextureBlend_1_ == null)
        {
            return true;
        }
        else
        {
            return this.textureFormat != null ? this.textureFormat.isTextureBlend(p_isTextureBlend_1_) : true;
        }
    }

    public boolean isNormalBlend()
    {
        return this.isTextureBlend(ShadersTextureType.NORMAL);
    }

    public boolean isSpecularBlend()
    {
        return this.isTextureBlend(ShadersTextureType.SPECULAR);
    }

    public String toString()
    {
        return "" + this.textureLocation;
    }

    public static class SheetData
    {
        final Set<ResourceLocation> spriteLocations;
        final int width;
        final int height;
        final int mipmapLevel;
        final List<TextureAtlasSprite> sprites;

        public SheetData(Set<ResourceLocation> spriteLocationsIn, int widthIn, int heightIn, int mipmapLevelIn, List<TextureAtlasSprite> spritesIn)
        {
            this.spriteLocations = spriteLocationsIn;
            this.width = widthIn;
            this.height = heightIn;
            this.mipmapLevel = mipmapLevelIn;
            this.sprites = spritesIn;
        }
    }
}
