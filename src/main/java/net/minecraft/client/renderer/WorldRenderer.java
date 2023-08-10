package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.SoundType;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Tuple3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.client.ICloudRenderHandler;
import net.minecraftforge.client.ISkyRenderHandler;
import net.minecraftforge.client.IWeatherParticleRenderHandler;
import net.minecraftforge.client.IWeatherRenderHandler;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lagometer;
import net.optifine.SmartAnimations;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.render.ChunkVisibility;
import net.optifine.render.RenderEnv;
import net.optifine.render.RenderStateManager;
import net.optifine.render.RenderUtils;
import net.optifine.render.VboRegion;
import net.optifine.shaders.RenderStage;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.shaders.ShadowUtils;
import net.optifine.util.BiomeUtils;
import net.optifine.util.ChunkUtils;
import net.optifine.util.MathUtils;
import net.optifine.util.PairInt;
import net.optifine.util.RenderChunkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import fun.rich.event.EventManager;
import fun.rich.event.events.impl.render.EventRender3D;

public class WorldRenderer implements IResourceManagerReloadListener, AutoCloseable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");
    private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");
    public static final Direction[] FACINGS = Direction.values();
    private final Minecraft mc;
    private final TextureManager textureManager;
    private final EntityRendererManager renderManager;
    private final RenderTypeBuffers renderTypeTextures;
    private ClientWorld world;
    private Set<ChunkRenderDispatcher.ChunkRender> chunksToUpdate = new ObjectLinkedOpenHashSet<>();
    private ObjectList<WorldRenderer.LocalRenderInformationContainer> renderInfos = new ObjectArrayList<>(69696);
    private final Set<TileEntity> setTileEntities = Sets.newHashSet();
    private ViewFrustum viewFrustum;
    private final VertexFormat skyVertexFormat = DefaultVertexFormats.POSITION;
    @Nullable
    private VertexBuffer starVBO;
    @Nullable
    private VertexBuffer skyVBO;
    @Nullable
    private VertexBuffer sky2VBO;
    private boolean cloudsNeedUpdate = true;
    @Nullable
    private VertexBuffer cloudsVBO;
    private final RenderTimeManager renderTimeManager = new RenderTimeManager(100);
    private int ticks;
    private final Int2ObjectMap<DestroyBlockProgress> damagedBlocks = new Int2ObjectOpenHashMap<>();
    private final Long2ObjectMap<SortedSet<DestroyBlockProgress>> damageProgress = new Long2ObjectOpenHashMap<>();
    private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
    @Nullable
    private Framebuffer entityOutlineFramebuffer;
    @Nullable

    /** Stores the shader group for the entity_outline shader */
    private ShaderGroup entityOutlineShader;
    @Nullable
    private Framebuffer field_239222_F_;
    @Nullable
    private Framebuffer field_239223_G_;
    @Nullable
    private Framebuffer field_239224_H_;
    @Nullable
    private Framebuffer field_239225_I_;
    @Nullable
    private Framebuffer field_239226_J_;
    @Nullable
    private ShaderGroup field_239227_K_;
    private double frustumUpdatePosX = Double.MIN_VALUE;
    private double frustumUpdatePosY = Double.MIN_VALUE;
    private double frustumUpdatePosZ = Double.MIN_VALUE;
    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    private double lastViewEntityX = Double.MIN_VALUE;
    private double lastViewEntityY = Double.MIN_VALUE;
    private double lastViewEntityZ = Double.MIN_VALUE;
    private double lastViewEntityPitch = Double.MIN_VALUE;
    private double lastViewEntityYaw = Double.MIN_VALUE;
    private int cloudsCheckX = Integer.MIN_VALUE;
    private int cloudsCheckY = Integer.MIN_VALUE;
    private int cloudsCheckZ = Integer.MIN_VALUE;
    private Vector3d cloudsCheckColor = Vector3d.ZERO;
    private CloudOption cloudOption;
    private ChunkRenderDispatcher renderDispatcher;
    private final VertexFormat blockVertexFormat = DefaultVertexFormats.BLOCK;
    private int renderDistanceChunks = -1;
    private int countEntitiesRendered;
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum;
    @Nullable
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
    private final Tuple3d debugTerrainFrustumPosition = new Tuple3d(0.0D, 0.0D, 0.0D);
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    private boolean displayListEntitiesDirty = true;
    private int frameId;
    private int rainSoundTime;
    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];
    public Entity renderedEntity;
    public Set chunksToResortTransparency = new LinkedHashSet();
    public Set chunksToUpdateForced = new LinkedHashSet();
    private Set<ChunkRenderDispatcher.ChunkRender> chunksToUpdatePrev = new ObjectLinkedOpenHashSet<>();
    private Deque visibilityDeque = new ArrayDeque();
    private List<WorldRenderer.LocalRenderInformationContainer> renderInfosEntities = new ArrayList<>(1024);
    private List<WorldRenderer.LocalRenderInformationContainer> renderInfosTileEntities = new ArrayList<>(1024);
    private ObjectList renderInfosNormal = new ObjectArrayList(1024);
    private List renderInfosEntitiesNormal = new ArrayList(1024);
    private List renderInfosTileEntitiesNormal = new ArrayList(1024);
    private ObjectList renderInfosShadow = new ObjectArrayList(1024);
    private List renderInfosEntitiesShadow = new ArrayList(1024);
    private List renderInfosTileEntitiesShadow = new ArrayList(1024);
    private int renderDistance = 0;
    private int renderDistanceSq = 0;
    private static final Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(Direction.VALUES)));
    private int countTileEntitiesRendered;
    private int countLoadedChunksPrev = 0;
    private RenderEnv renderEnv = new RenderEnv(Blocks.AIR.getDefaultState(), new BlockPos(0, 0, 0));
    public boolean renderOverlayDamaged = false;
    public boolean renderOverlayEyes = false;
    private boolean firstWorldLoad = false;
    private static int renderEntitiesCounter = 0;
    public int loadVisibleChunksCounter = -1;
    public static final int loadVisibleChunksMessageId = 201435902;
    private static boolean ambientOcclusion = false;
    private Map<String, List<Entity>> mapEntityLists = new HashMap<>();
    private Map<RenderType, Map> mapRegionLayers = new LinkedHashMap<>();

    public WorldRenderer(Minecraft mcIn, RenderTypeBuffers rainTimeBuffersIn)
    {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.renderTypeTextures = rainTimeBuffersIn;
        this.textureManager = mcIn.getTextureManager();

        for (int i = 0; i < 32; ++i)
        {
            for (int j = 0; j < 32; ++j)
            {
                float f = (float)(j - 16);
                float f1 = (float)(i - 16);
                float f2 = MathHelper.sqrt(f * f + f1 * f1);
                this.rainSizeX[i << 5 | j] = -f1 / f2;
                this.rainSizeZ[i << 5 | j] = f / f2;
            }
        }

        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }

    private void renderRainSnow(LightTexture lightmapIn, float partialTicks, double xIn, double yIn, double zIn)
    {
        if (Reflector.ForgeDimensionRenderInfo_getWeatherRenderHandler.exists())
        {
            IWeatherRenderHandler iweatherrenderhandler = (IWeatherRenderHandler)Reflector.call(this.world.func_239132_a_(), Reflector.ForgeDimensionRenderInfo_getWeatherRenderHandler);

            if (iweatherrenderhandler != null)
            {
                iweatherrenderhandler.render(this.ticks, partialTicks, this.world, this.mc, lightmapIn, xIn, yIn, zIn);
                return;
            }
        }

        float f5 = this.mc.world.getRainStrength(partialTicks);

        if (!(f5 <= 0.0F))
        {
            if (Config.isRainOff())
            {
                return;
            }

            lightmapIn.enableLightmap();
            World world = this.mc.world;
            int i = MathHelper.floor(xIn);
            int j = MathHelper.floor(yIn);
            int k = MathHelper.floor(zIn);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            RenderSystem.enableAlphaTest();
            RenderSystem.disableCull();
            RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableDepthTest();
            int l = 5;

            if (Config.isRainFancy())
            {
                l = 10;
            }

            RenderSystem.depthMask(Minecraft.isFabulousGraphicsEnabled());
            int i1 = -1;
            float f = (float)this.ticks + partialTicks;
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for (int j1 = k - l; j1 <= k + l; ++j1)
            {
                for (int k1 = i - l; k1 <= i + l; ++k1)
                {
                    int l1 = (j1 - k + 16) * 32 + k1 - i + 16;
                    double d0 = (double)this.rainSizeX[l1] * 0.5D;
                    double d1 = (double)this.rainSizeZ[l1] * 0.5D;
                    blockpos$mutable.setPos(k1, 0, j1);
                    Biome biome = world.getBiome(blockpos$mutable);

                    if (biome.getPrecipitation() != Biome.RainType.NONE)
                    {
                        int i2 = world.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos$mutable).getY();
                        int j2 = j - l;
                        int k2 = j + l;

                        if (j2 < i2)
                        {
                            j2 = i2;
                        }

                        if (k2 < i2)
                        {
                            k2 = i2;
                        }

                        int l2 = i2;

                        if (i2 < j)
                        {
                            l2 = j;
                        }

                        if (j2 != k2)
                        {
                            Random random = new Random((long)(k1 * k1 * 3121 + k1 * 45238971 ^ j1 * j1 * 418711 + j1 * 13761));
                            blockpos$mutable.setPos(k1, j2, j1);
                            float f1 = biome.getTemperature(blockpos$mutable);

                            if (f1 >= 0.15F)
                            {
                                if (i1 != 0)
                                {
                                    if (i1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    i1 = 0;
                                    this.mc.getTextureManager().bindTexture(RAIN_TEXTURES);
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                int i3 = this.ticks + k1 * k1 * 3121 + k1 * 45238971 + j1 * j1 * 418711 + j1 * 13761 & 31;
                                float f2 = -((float)i3 + partialTicks) / 32.0F * (3.0F + random.nextFloat());
                                double d2 = (double)((float)k1 + 0.5F) - xIn;
                                double d4 = (double)((float)j1 + 0.5F) - zIn;
                                float f3 = MathHelper.sqrt(d2 * d2 + d4 * d4) / (float)l;
                                float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f5;
                                blockpos$mutable.setPos(k1, l2, j1);
                                int j3 = getCombinedLight(world, blockpos$mutable);
                                bufferbuilder.pos((double)k1 - xIn - d0 + 0.5D, (double)k2 - yIn, (double)j1 - zIn - d1 + 0.5D).tex(0.0F, (float)j2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).lightmap(j3).endVertex();
                                bufferbuilder.pos((double)k1 - xIn + d0 + 0.5D, (double)k2 - yIn, (double)j1 - zIn + d1 + 0.5D).tex(1.0F, (float)j2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).lightmap(j3).endVertex();
                                bufferbuilder.pos((double)k1 - xIn + d0 + 0.5D, (double)j2 - yIn, (double)j1 - zIn + d1 + 0.5D).tex(1.0F, (float)k2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).lightmap(j3).endVertex();
                                bufferbuilder.pos((double)k1 - xIn - d0 + 0.5D, (double)j2 - yIn, (double)j1 - zIn - d1 + 0.5D).tex(0.0F, (float)k2 * 0.25F + f2).color(1.0F, 1.0F, 1.0F, f4).lightmap(j3).endVertex();
                            }
                            else
                            {
                                if (i1 != 1)
                                {
                                    if (i1 >= 0)
                                    {
                                        tessellator.draw();
                                    }

                                    i1 = 1;
                                    this.mc.getTextureManager().bindTexture(SNOW_TEXTURES);
                                    bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                float f6 = -((float)(this.ticks & 511) + partialTicks) / 512.0F;
                                float f7 = (float)(random.nextDouble() + (double)f * 0.01D * (double)((float)random.nextGaussian()));
                                float f8 = (float)(random.nextDouble() + (double)(f * (float)random.nextGaussian()) * 0.001D);
                                double d3 = (double)((float)k1 + 0.5F) - xIn;
                                double d5 = (double)((float)j1 + 0.5F) - zIn;
                                float f9 = MathHelper.sqrt(d3 * d3 + d5 * d5) / (float)l;
                                float f10 = ((1.0F - f9 * f9) * 0.3F + 0.5F) * f5;
                                blockpos$mutable.setPos(k1, l2, j1);
                                int k3 = getCombinedLight(world, blockpos$mutable);
                                int l3 = k3 >> 16 & 65535;
                                int i4 = (k3 & 65535) * 3;
                                int j4 = (l3 * 3 + 240) / 4;
                                int k4 = (i4 * 3 + 240) / 4;
                                bufferbuilder.pos((double)k1 - xIn - d0 + 0.5D, (double)k2 - yIn, (double)j1 - zIn - d1 + 0.5D).tex(0.0F + f7, (float)j2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                                bufferbuilder.pos((double)k1 - xIn + d0 + 0.5D, (double)k2 - yIn, (double)j1 - zIn + d1 + 0.5D).tex(1.0F + f7, (float)j2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                                bufferbuilder.pos((double)k1 - xIn + d0 + 0.5D, (double)j2 - yIn, (double)j1 - zIn + d1 + 0.5D).tex(1.0F + f7, (float)k2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                                bufferbuilder.pos((double)k1 - xIn - d0 + 0.5D, (double)j2 - yIn, (double)j1 - zIn - d1 + 0.5D).tex(0.0F + f7, (float)k2 * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).lightmap(k4, j4).endVertex();
                            }
                        }
                    }
                }
            }

            if (i1 >= 0)
            {
                tessellator.draw();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableAlphaTest();
            lightmapIn.disableLightmap();
        }
    }

    public void addRainParticles(ActiveRenderInfo activeRenderInfoIn)
    {
        if (Reflector.ForgeDimensionRenderInfo_getWeatherParticleRenderHandler.exists())
        {
            IWeatherParticleRenderHandler iweatherparticlerenderhandler = (IWeatherParticleRenderHandler)Reflector.call(this.world.func_239132_a_(), Reflector.ForgeDimensionRenderInfo_getWeatherParticleRenderHandler);

            if (iweatherparticlerenderhandler != null)
            {
                iweatherparticlerenderhandler.render(this.ticks, this.world, this.mc, activeRenderInfoIn);
                return;
            }
        }

        float f = this.mc.world.getRainStrength(1.0F) / (Minecraft.isFancyGraphicsEnabled() ? 1.0F : 2.0F);

        if (!Config.isRainFancy())
        {
            f /= 2.0F;
        }

        if (!(f <= 0.0F) && Config.isRainSplash())
        {
            Random random = new Random((long)this.ticks * 312987231L);
            IWorldReader iworldreader = this.mc.world;
            BlockPos blockpos = new BlockPos(activeRenderInfoIn.getProjectedView());
            BlockPos blockpos1 = null;
            int i = (int)(100.0F * f * f) / (this.mc.gameSettings.particles == ParticleStatus.DECREASED ? 2 : 1);

            for (int j = 0; j < i; ++j)
            {
                int k = random.nextInt(21) - 10;
                int l = random.nextInt(21) - 10;
                BlockPos blockpos2 = iworldreader.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos.add(k, 0, l)).down();
                Biome biome = iworldreader.getBiome(blockpos2);

                if (blockpos2.getY() > 0 && blockpos2.getY() <= blockpos.getY() + 10 && blockpos2.getY() >= blockpos.getY() - 10 && biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(blockpos2) >= 0.15F)
                {
                    blockpos1 = blockpos2;

                    if (this.mc.gameSettings.particles == ParticleStatus.MINIMAL)
                    {
                        break;
                    }

                    double d0 = random.nextDouble();
                    double d1 = random.nextDouble();
                    BlockState blockstate = iworldreader.getBlockState(blockpos2);
                    FluidState fluidstate = iworldreader.getFluidState(blockpos2);
                    VoxelShape voxelshape = blockstate.getCollisionShape(iworldreader, blockpos2);
                    double d2 = voxelshape.max(Direction.Axis.Y, d0, d1);
                    double d3 = (double)fluidstate.getActualHeight(iworldreader, blockpos2);
                    double d4 = Math.max(d2, d3);
                    IParticleData iparticledata = !fluidstate.isTagged(FluidTags.LAVA) && !blockstate.isIn(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLit(blockstate) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
                    this.mc.world.addParticle(iparticledata, (double)blockpos2.getX() + d0, (double)blockpos2.getY() + d4, (double)blockpos2.getZ() + d1, 0.0D, 0.0D, 0.0D);
                }
            }

            if (blockpos1 != null && random.nextInt(3) < this.rainSoundTime++)
            {
                this.rainSoundTime = 0;

                if (blockpos1.getY() > blockpos.getY() + 1 && iworldreader.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos).getY() > MathHelper.floor((float)blockpos.getY()))
                {
                    this.mc.world.playSound(blockpos1, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
                }
                else
                {
                    this.mc.world.playSound(blockpos1, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
                }
            }
        }
    }

    public void close()
    {
        if (this.entityOutlineShader != null)
        {
            this.entityOutlineShader.close();
        }

        if (this.field_239227_K_ != null)
        {
            this.field_239227_K_.close();
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.textureManager.bindTexture(FORCEFIELD_TEXTURES);
        RenderSystem.texParameter(3553, 10242, 10497);
        RenderSystem.texParameter(3553, 10243, 10497);
        RenderSystem.bindTexture(0);
        this.makeEntityOutlineShader();

        if (Minecraft.isFabulousGraphicsEnabled())
        {
            this.func_239233_v_();
        }
    }

    /**
     * Creates the entity outline shader to be stored in RenderGlobal.entityOutlineShader
     */
    public void makeEntityOutlineShader()
    {
        if (this.entityOutlineShader != null)
        {
            this.entityOutlineShader.close();
        }

        ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");

        try
        {
            this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
            this.entityOutlineShader.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
            this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
        }
        catch (IOException ioexception)
        {
            LOGGER.warn("Failed to load shader: {}", resourcelocation, ioexception);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
        catch (JsonSyntaxException jsonsyntaxexception)
        {
            LOGGER.warn("Failed to parse shader: {}", resourcelocation, jsonsyntaxexception);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
    }

    private void func_239233_v_()
    {
        this.func_239234_w_();
        ResourceLocation resourcelocation = new ResourceLocation("shaders/post/transparency.json");

        try
        {
            ShaderGroup shadergroup = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
            shadergroup.createBindFramebuffers(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
            Framebuffer framebuffer1 = shadergroup.getFramebufferRaw("translucent");
            Framebuffer framebuffer2 = shadergroup.getFramebufferRaw("itemEntity");
            Framebuffer framebuffer3 = shadergroup.getFramebufferRaw("particles");
            Framebuffer framebuffer4 = shadergroup.getFramebufferRaw("weather");
            Framebuffer framebuffer = shadergroup.getFramebufferRaw("clouds");
            this.field_239227_K_ = shadergroup;
            this.field_239222_F_ = framebuffer1;
            this.field_239223_G_ = framebuffer2;
            this.field_239224_H_ = framebuffer3;
            this.field_239225_I_ = framebuffer4;
            this.field_239226_J_ = framebuffer;
        }
        catch (Exception exception1)
        {
            String s = exception1 instanceof JsonSyntaxException ? "parse" : "load";
            String s1 = "Failed to " + s + " shader: " + resourcelocation;
            WorldRenderer.ShaderException worldrenderer$shaderexception = new WorldRenderer.ShaderException(s1, exception1);

            if (this.mc.getResourcePackList().func_232621_d_().size() > 1)
            {
                ITextComponent itextcomponent;

                try
                {
                    itextcomponent = new StringTextComponent(this.mc.getResourceManager().getResource(resourcelocation).getPackName());
                }
                catch (IOException ioexception1)
                {
                    itextcomponent = null;
                }

                this.mc.gameSettings.graphicFanciness = GraphicsFanciness.FANCY;
                this.mc.throwResourcePackLoadError(worldrenderer$shaderexception, itextcomponent);
            }
            else
            {
                CrashReport crashreport = this.mc.addGraphicsAndWorldToCrashReport(new CrashReport(s1, worldrenderer$shaderexception));
                this.mc.gameSettings.graphicFanciness = GraphicsFanciness.FANCY;
                this.mc.gameSettings.saveOptions();
                LOGGER.fatal(s1, (Throwable)worldrenderer$shaderexception);
                this.mc.freeMemory();
                Minecraft.displayCrashReport(crashreport);
            }
        }
    }

    private void func_239234_w_()
    {
        if (this.field_239227_K_ != null)
        {
            this.field_239227_K_.close();
            this.field_239222_F_.deleteFramebuffer();
            this.field_239223_G_.deleteFramebuffer();
            this.field_239224_H_.deleteFramebuffer();
            this.field_239225_I_.deleteFramebuffer();
            this.field_239226_J_.deleteFramebuffer();
            this.field_239227_K_ = null;
            this.field_239222_F_ = null;
            this.field_239223_G_ = null;
            this.field_239224_H_ = null;
            this.field_239225_I_ = null;
            this.field_239226_J_ = null;
        }
    }

    public void renderEntityOutlineFramebuffer()
    {
        if (this.isRenderEntityOutlines())
        {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight(), false);
            RenderSystem.disableBlend();
        }
    }

    public boolean isRenderEntityOutlines()
    {
        if (!Config.isShaders() && !Config.isAntialiasing())
        {
            return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.player != null;
        }
        else
        {
            return false;
        }
    }

    private void generateSky2()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.sky2VBO != null)
        {
            this.sky2VBO.close();
        }

        this.sky2VBO = new VertexBuffer(this.skyVertexFormat);
        this.renderSky(bufferbuilder, -16.0F, true);
        bufferbuilder.finishDrawing();
        this.sky2VBO.upload(bufferbuilder);
    }

    private void generateSky()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.skyVBO != null)
        {
            this.skyVBO.close();
        }

        this.skyVBO = new VertexBuffer(this.skyVertexFormat);
        this.renderSky(bufferbuilder, 16.0F, false);
        bufferbuilder.finishDrawing();
        this.skyVBO.upload(bufferbuilder);
    }

    private void renderSky(BufferBuilder bufferBuilderIn, float posY, boolean reverseX)
    {
        int i = 64;
        int j = 6;
        bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);
        int k = (this.renderDistance / 64 + 1) * 64 + 64;

        for (int l = -k; l <= k; l += 64)
        {
            for (int i1 = -k; i1 <= k; i1 += 64)
            {
                float f = (float)l;
                float f1 = (float)(l + 64);

                if (reverseX)
                {
                    f1 = (float)l;
                    f = (float)(l + 64);
                }

                bufferBuilderIn.pos((double)f, (double)posY, (double)i1).endVertex();
                bufferBuilderIn.pos((double)f1, (double)posY, (double)i1).endVertex();
                bufferBuilderIn.pos((double)f1, (double)posY, (double)(i1 + 64)).endVertex();
                bufferBuilderIn.pos((double)f, (double)posY, (double)(i1 + 64)).endVertex();
            }
        }
    }

    private void generateStars()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.starVBO != null)
        {
            this.starVBO.close();
        }

        this.starVBO = new VertexBuffer(this.skyVertexFormat);
        this.renderStars(bufferbuilder);
        bufferbuilder.finishDrawing();
        this.starVBO.upload(bufferbuilder);
    }

    private void renderStars(BufferBuilder bufferBuilderIn)
    {
        Random random = new Random(10842L);
        bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

        for (int i = 0; i < 1500; ++i)
        {
            double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
            double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
            double d4 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d4 < 1.0D && d4 > 0.01D)
            {
                d4 = 1.0D / Math.sqrt(d4);
                d0 = d0 * d4;
                d1 = d1 * d4;
                d2 = d2 * d4;
                double d5 = d0 * 100.0D;
                double d6 = d1 * 100.0D;
                double d7 = d2 * 100.0D;
                double d8 = Math.atan2(d0, d2);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = random.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                for (int j = 0; j < 4; ++j)
                {
                    double d17 = 0.0D;
                    double d18 = (double)((j & 2) - 1) * d3;
                    double d19 = (double)((j + 1 & 2) - 1) * d3;
                    double d20 = 0.0D;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
                }
            }
        }
    }

    /**
     * set null to clear
     */
    public void setWorldAndLoadRenderers(@Nullable ClientWorld worldClientIn)
    {
        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.setWorld(worldClientIn);
        this.world = worldClientIn;

        if (Config.isDynamicLights())
        {
            DynamicLights.clear();
        }

        ChunkVisibility.reset();
        this.renderEnv.reset((BlockState)null, (BlockPos)null);
        BiomeUtils.onWorldChanged(this.world);
        Shaders.checkWorldChanged(this.world);

        if (worldClientIn != null)
        {
            this.loadRenderers();
        }
        else
        {
            this.chunksToUpdate.clear();
            this.chunksToUpdatePrev.clear();
            this.clearRenderInfos();

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
                this.viewFrustum = null;
            }

            if (this.renderDispatcher != null)
            {
                this.renderDispatcher.stopWorkerThreads();
            }

            this.renderDispatcher = null;
            this.setTileEntities.clear();
        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
    public void loadRenderers()
    {
        if (this.world != null)
        {
            if (Minecraft.isFabulousGraphicsEnabled())
            {
                this.func_239233_v_();
            }
            else
            {
                this.func_239234_w_();
            }

            this.world.clearColorCaches();

            if (this.renderDispatcher == null)
            {
                this.renderDispatcher = new ChunkRenderDispatcher(this.world, this, Util.getServerExecutor(), this.mc.isJava64bit(), this.renderTypeTextures.getFixedBuilder());
            }
            else
            {
                this.renderDispatcher.setWorld(this.world);
            }

            this.displayListEntitiesDirty = true;
            this.cloudsNeedUpdate = true;
            RenderTypeLookup.setFancyGraphics(Config.isTreesFancy());
            BlockModelRenderer.updateAoLightValue();

            if (Config.isDynamicLights())
            {
                DynamicLights.clear();
            }

            SmartAnimations.update();
            ambientOcclusion = Minecraft.isAmbientOcclusionEnabled();
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            this.renderDistance = this.renderDistanceChunks * 16;
            this.renderDistanceSq = this.renderDistance * this.renderDistance;
            this.generateStars();
            this.generateSky();
            this.generateSky2();

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();

            synchronized (this.setTileEntities)
            {
                this.setTileEntities.clear();
            }

            this.viewFrustum = new ViewFrustum(this.renderDispatcher, this.world, this.mc.gameSettings.renderDistanceChunks, this);

            if (this.world != null)
            {
                Entity entity = this.mc.getRenderViewEntity();

                if (entity != null)
                {
                    this.viewFrustum.updateChunkPositions(entity.getPosX(), entity.getPosZ());
                }
            }
        }

        if (this.mc.player == null)
        {
            this.firstWorldLoad = true;
        }
    }

    protected void stopChunkUpdates()
    {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void createBindEntityOutlineFbs(int width, int height)
    {
        this.setDisplayListEntitiesDirty();

        if (this.entityOutlineShader != null)
        {
            this.entityOutlineShader.createBindFramebuffers(width, height);
        }

        if (this.field_239227_K_ != null)
        {
            this.field_239227_K_.createBindFramebuffers(width, height);
        }
    }

    /**
     * Gets the render info for use on the Debug screen
     */
    public String getDebugInfoRenders()
    {
        int i = this.viewFrustum.renderChunks.length;
        int j = this.getRenderedChunks();
        return String.format("C: %d/%d %sD: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher == null ? "null" : this.renderDispatcher.getDebugInfo());
    }

    protected int getRenderedChunks()
    {
        int i = 0;

        for (WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : this.renderInfos)
        {
            if (!worldrenderer$localrenderinformationcontainer.renderChunk.getCompiledChunk().isEmpty())
            {
                ++i;
            }
        }

        return i;
    }

    /**
     * Gets the entities info for use on the Debug screen
     */
    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.world.getCountLoadedEntities() + ", B: " + this.countEntitiesHidden + ", " + Config.getVersionDebug();
    }

    public void setupTerrain(ActiveRenderInfo activeRenderInfoIn, ClippingHelper camera, boolean debugCamera, int frameCount, boolean playerSpectator)
    {
        Vector3d vector3d = activeRenderInfoIn.getProjectedView();

        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks)
        {
            this.loadRenderers();
        }

        this.world.getProfiler().startSection("camera");
        double d0 = this.mc.player.getPosX() - this.frustumUpdatePosX;
        double d1 = this.mc.player.getPosY() - this.frustumUpdatePosY;
        double d2 = this.mc.player.getPosZ() - this.frustumUpdatePosZ;

        if (this.frustumUpdatePosChunkX != this.mc.player.chunkCoordX || this.frustumUpdatePosChunkY != this.mc.player.chunkCoordY || this.frustumUpdatePosChunkZ != this.mc.player.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D)
        {
            this.frustumUpdatePosX = this.mc.player.getPosX();
            this.frustumUpdatePosY = this.mc.player.getPosY();
            this.frustumUpdatePosZ = this.mc.player.getPosZ();
            this.frustumUpdatePosChunkX = this.mc.player.chunkCoordX;
            this.frustumUpdatePosChunkY = this.mc.player.chunkCoordY;
            this.frustumUpdatePosChunkZ = this.mc.player.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(this.mc.player.getPosX(), this.mc.player.getPosZ());
        }

        if (Config.isDynamicLights())
        {
            DynamicLights.update(this);
        }

        this.renderDispatcher.setRenderPosition(vector3d);
        this.world.getProfiler().endStartSection("cull");
        this.mc.getProfiler().endStartSection("culling");
        BlockPos blockpos = activeRenderInfoIn.getBlockPos();
        ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.viewFrustum.getRenderChunk(blockpos);
        int i = 16;
        BlockPos blockpos1 = new BlockPos(MathHelper.floor(vector3d.x / 16.0D) * 16, MathHelper.floor(vector3d.y / 16.0D) * 16, MathHelper.floor(vector3d.z / 16.0D) * 16);
        float f = activeRenderInfoIn.getPitch();
        float f1 = activeRenderInfoIn.getYaw();
        this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || vector3d.x != this.lastViewEntityX || vector3d.y != this.lastViewEntityY || vector3d.z != this.lastViewEntityZ || (double)f != this.lastViewEntityPitch || (double)f1 != this.lastViewEntityYaw;
        this.lastViewEntityX = vector3d.x;
        this.lastViewEntityY = vector3d.y;
        this.lastViewEntityZ = vector3d.z;
        this.lastViewEntityPitch = (double)f;
        this.lastViewEntityYaw = (double)f1;
        this.mc.getProfiler().endStartSection("update");
        Lagometer.timerVisibility.start();
        int j = this.getCountLoadedChunks();

        if (j != this.countLoadedChunksPrev)
        {
            this.countLoadedChunksPrev = j;
            this.displayListEntitiesDirty = true;
        }

        Entity entity = activeRenderInfoIn.getRenderViewEntity();
        int k = 256;

        if (!ChunkVisibility.isFinished())
        {
            this.displayListEntitiesDirty = true;
        }

        if (!debugCamera && this.displayListEntitiesDirty && Config.isIntegratedServerRunning() && !Shaders.isShadowPass)
        {
            k = ChunkVisibility.getMaxChunkY(this.world, entity, this.renderDistanceChunks);
        }

        ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender1 = this.viewFrustum.getRenderChunk(new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ()));

        if (Shaders.isShadowPass)
        {
            this.renderInfos = this.renderInfosShadow;
            this.renderInfosEntities = this.renderInfosEntitiesShadow;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;

            if (!debugCamera && this.displayListEntitiesDirty)
            {
                this.clearRenderInfos();

                if (chunkrenderdispatcher$chunkrender1 != null && chunkrenderdispatcher$chunkrender1.getPosition().getY() > k)
                {
                    this.renderInfosEntities.add(chunkrenderdispatcher$chunkrender1.getRenderInfo());
                }

                Iterator<ChunkRenderDispatcher.ChunkRender> iterator = ShadowUtils.makeShadowChunkIterator(this.world, 0.0D, entity, this.renderDistanceChunks, this.viewFrustum);

                while (iterator.hasNext())
                {
                    ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender2 = iterator.next();

                    if (chunkrenderdispatcher$chunkrender2 != null && chunkrenderdispatcher$chunkrender2.getPosition().getY() <= k)
                    {
                        WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer = chunkrenderdispatcher$chunkrender2.getRenderInfo();

                        if (!chunkrenderdispatcher$chunkrender2.compiledChunk.get().isEmpty())
                        {
                            this.renderInfos.add(worldrenderer$localrenderinformationcontainer);
                        }

                        if (ChunkUtils.hasEntities(chunkrenderdispatcher$chunkrender2.getChunk()))
                        {
                            this.renderInfosEntities.add(worldrenderer$localrenderinformationcontainer);
                        }

                        if (chunkrenderdispatcher$chunkrender2.getCompiledChunk().getTileEntities().size() > 0)
                        {
                            this.renderInfosTileEntities.add(worldrenderer$localrenderinformationcontainer);
                        }
                    }
                }
            }
        }
        else
        {
            this.renderInfos = this.renderInfosNormal;
            this.renderInfosEntities = this.renderInfosEntitiesNormal;
            this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
        }

        if (!debugCamera && this.displayListEntitiesDirty && !Shaders.isShadowPass)
        {
            this.displayListEntitiesDirty = false;
            this.clearRenderInfos();
            this.visibilityDeque.clear();
            Deque<WorldRenderer.LocalRenderInformationContainer> deque = this.visibilityDeque;
            Entity.setRenderDistanceWeight(MathHelper.clamp((double)this.mc.gameSettings.renderDistanceChunks / 8.0D, 1.0D, 2.5D) * (double)this.mc.gameSettings.entityDistanceScaling);
            boolean flag = this.mc.renderChunksMany;
            BlockPos blockpos2 = activeRenderInfoIn.getBlockPos();
            int l = blockpos2.getY();
            int i1 = l >> 4 << 4;

            if (i1 > k)
            {
                k += 16;

                if (i1 > k && k < 256)
                {
                    if (chunkrenderdispatcher$chunkrender1 != null)
                    {
                        this.renderInfosEntities.add(chunkrenderdispatcher$chunkrender1.getRenderInfo());
                    }

                    Vector3d vector3d1 = new Vector3d((double)blockpos2.getX(), (double)k, (double)blockpos2.getZ());
                    Vector3d vector3d2 = new Vector3d(vector3d1.getX(), vector3d1.getY(), vector3d1.getZ());
                    Vector3f vector3f = activeRenderInfoIn.getViewVector();
                    Vector3f vector3f1 = new Vector3f(vector3f.getX(), 0.0F, vector3f.getZ());

                    if (!vector3f1.normalize())
                    {
                        vector3f1 = new Vector3f(1.0F, 0.0F, 0.0F);
                    }

                    double d3 = (double)(vector3f1.getX() * 16.0F);
                    double d4 = (double)(vector3f1.getZ() * 16.0F);
                    double d5 = (double)(this.renderDistanceChunks * 16);

                    for (double d6 = d5 * d5; vector3d2.squareDistanceTo(vector3d1) < d6; vector3d2 = vector3d2.add(d3, 0.0D, d4))
                    {
                        ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender3 = this.viewFrustum.getRenderChunk(new BlockPos(vector3d2));

                        if (chunkrenderdispatcher$chunkrender3 == null)
                        {
                            break;
                        }

                        if (camera.isBoundingBoxInFrustum(chunkrenderdispatcher$chunkrender3.boundingBox))
                        {
                            chunkrenderdispatcher$chunkrender3.setFrameIndex(frameCount);
                            deque.add(new WorldRenderer.LocalRenderInformationContainer(chunkrenderdispatcher$chunkrender3, (Direction)null, 0));
                            break;
                        }
                    }
                }
            }

            if (deque.isEmpty())
            {
                if (chunkrenderdispatcher$chunkrender != null && chunkrenderdispatcher$chunkrender.getPosition().getY() <= k)
                {
                    if (playerSpectator && this.world.getBlockState(blockpos).isOpaqueCube(this.world, blockpos))
                    {
                        flag = false;
                    }

                    chunkrenderdispatcher$chunkrender.setFrameIndex(frameCount);
                    deque.add(new WorldRenderer.LocalRenderInformationContainer(chunkrenderdispatcher$chunkrender, (Direction)null, 0));
                }
                else
                {
                    int l1 = blockpos1.getY() > 0 ? Math.min(k, 248) : 8;

                    if (chunkrenderdispatcher$chunkrender1 != null)
                    {
                        this.renderInfosEntities.add(chunkrenderdispatcher$chunkrender1.getRenderInfo());
                    }

                    int i2 = MathHelper.floor(vector3d.x / 16.0D) * 16;
                    int j2 = MathHelper.floor(vector3d.z / 16.0D) * 16;
                    List<WorldRenderer.LocalRenderInformationContainer> list = Lists.newArrayList();

                    for (int k2 = -this.renderDistanceChunks; k2 <= this.renderDistanceChunks; ++k2)
                    {
                        for (int j1 = -this.renderDistanceChunks; j1 <= this.renderDistanceChunks; ++j1)
                        {
                            ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender6 = this.viewFrustum.getRenderChunk(new BlockPos(i2 + (k2 << 4) + 8, l1, j2 + (j1 << 4) + 8));

                            if (chunkrenderdispatcher$chunkrender6 != null && camera.isBoundingBoxInFrustum(chunkrenderdispatcher$chunkrender6.boundingBox))
                            {
                                chunkrenderdispatcher$chunkrender6.setFrameIndex(frameCount);
                                WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer1 = chunkrenderdispatcher$chunkrender6.getRenderInfo();
                                worldrenderer$localrenderinformationcontainer1.initialize((Direction)null, 0, 0);
                                list.add(worldrenderer$localrenderinformationcontainer1);
                            }
                        }
                    }

                    list.sort(Comparator.comparingDouble((p_lambda$setupTerrain$0_1_) ->
                    {
                        return blockpos.distanceSq(p_lambda$setupTerrain$0_1_.renderChunk.getPosition().add(8, 8, 8));
                    }));
                    deque.addAll(list);
                }
            }

            this.mc.getProfiler().startSection("iteration");
            boolean flag1 = Config.isFogOn();

            while (!deque.isEmpty())
            {
                WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer3 = deque.poll();
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender5 = worldrenderer$localrenderinformationcontainer3.renderChunk;
                Direction direction1 = worldrenderer$localrenderinformationcontainer3.facing;
                ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = chunkrenderdispatcher$chunkrender5.compiledChunk.get();

                if (!chunkrenderdispatcher$compiledchunk.isEmpty() || chunkrenderdispatcher$chunkrender5.needsUpdate())
                {
                    this.renderInfos.add(worldrenderer$localrenderinformationcontainer3);
                }

                if (ChunkUtils.hasEntities(chunkrenderdispatcher$chunkrender5.getChunk()))
                {
                    this.renderInfosEntities.add(worldrenderer$localrenderinformationcontainer3);
                }

                if (chunkrenderdispatcher$compiledchunk.getTileEntities().size() > 0)
                {
                    this.renderInfosTileEntities.add(worldrenderer$localrenderinformationcontainer3);
                }

                Direction[] adirection = flag ? ChunkVisibility.getFacingsNotOpposite(worldrenderer$localrenderinformationcontainer3.setFacing) : Direction.VALUES;

                for (Direction direction : adirection)
                {
                    if (!flag || direction1 == null || chunkrenderdispatcher$compiledchunk.isVisible(direction1.getOpposite(), direction))
                    {
                        ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender7 = this.getRenderChunkOffset(blockpos1, chunkrenderdispatcher$chunkrender5, direction, flag1, k);

                        if (chunkrenderdispatcher$chunkrender7 != null && chunkrenderdispatcher$chunkrender7.setFrameIndex(frameCount) && camera.isBoundingBoxInFrustum(chunkrenderdispatcher$chunkrender7.boundingBox))
                        {
                            int k1 = worldrenderer$localrenderinformationcontainer3.setFacing | 1 << direction.ordinal();
                            WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer4 = chunkrenderdispatcher$chunkrender7.getRenderInfo();
                            worldrenderer$localrenderinformationcontainer4.initialize(direction, k1, worldrenderer$localrenderinformationcontainer3.counter + 1);
                            deque.add(worldrenderer$localrenderinformationcontainer4);
                        }
                    }
                }
            }

            this.mc.getProfiler().endSection();
        }

        Lagometer.timerVisibility.end();

        if (Shaders.isShadowPass)
        {
            Shaders.mcProfilerEndSection();
        }
        else
        {
            this.mc.getProfiler().endStartSection("rebuildNear");
            Set<ChunkRenderDispatcher.ChunkRender> set = this.chunksToUpdate;
            this.chunksToUpdate = this.chunksToUpdatePrev;
            this.chunksToUpdatePrev = set;
            this.chunksToUpdate.clear();
            Lagometer.timerChunkUpdate.start();

            for (WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer2 : this.renderInfos)
            {
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender4 = worldrenderer$localrenderinformationcontainer2.renderChunk;

                if (chunkrenderdispatcher$chunkrender4.needsUpdate() || set.contains(chunkrenderdispatcher$chunkrender4))
                {
                    this.displayListEntitiesDirty = true;
                    BlockPos blockpos3 = chunkrenderdispatcher$chunkrender4.getPosition();
                    boolean flag2 = (double)MathUtils.distanceSq(blockpos1, (float)(blockpos3.getX() + 8), (float)(blockpos3.getY() + 8), (float)(blockpos3.getZ() + 8)) < 768.0D;

                    if (!chunkrenderdispatcher$chunkrender4.needsImmediateUpdate() && !flag2)
                    {
                        this.chunksToUpdate.add(chunkrenderdispatcher$chunkrender4);
                    }
                    else if (!chunkrenderdispatcher$chunkrender4.isPlayerUpdate())
                    {
                        this.chunksToUpdateForced.add(chunkrenderdispatcher$chunkrender4);
                    }
                    else
                    {
                        this.mc.getProfiler().startSection("build near");
                        this.renderDispatcher.rebuildChunk(chunkrenderdispatcher$chunkrender4);
                        chunkrenderdispatcher$chunkrender4.clearNeedsUpdate();
                        this.mc.getProfiler().endSection();
                    }
                }
            }

            Lagometer.timerChunkUpdate.end();
            this.chunksToUpdate.addAll(set);
            this.mc.getProfiler().endSection();
        }
    }

    @Nullable
    private ChunkRenderDispatcher.ChunkRender getRenderChunkOffset(BlockPos p_getRenderChunkOffset_1_, ChunkRenderDispatcher.ChunkRender p_getRenderChunkOffset_2_, Direction p_getRenderChunkOffset_3_, boolean p_getRenderChunkOffset_4_, int p_getRenderChunkOffset_5_)
    {
        ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = p_getRenderChunkOffset_2_.getRenderChunkNeighbour(p_getRenderChunkOffset_3_);

        if (chunkrenderdispatcher$chunkrender == null)
        {
            return null;
        }
        else if (chunkrenderdispatcher$chunkrender.getPosition().getY() > p_getRenderChunkOffset_5_)
        {
            return null;
        }
        else
        {
            if (p_getRenderChunkOffset_4_)
            {
                BlockPos blockpos = chunkrenderdispatcher$chunkrender.getPosition();
                int i = p_getRenderChunkOffset_1_.getX() - blockpos.getX();
                int j = p_getRenderChunkOffset_1_.getZ() - blockpos.getZ();
                int k = i * i + j * j;

                if (k > this.renderDistanceSq)
                {
                    return null;
                }
            }

            return chunkrenderdispatcher$chunkrender;
        }
    }

    private void captureFrustum(Matrix4f p_228419_1_, Matrix4f p_228419_2_, double p_228419_3_, double p_228419_5_, double p_228419_7_, ClippingHelper p_228419_9_)
    {
        this.debugFixedClippingHelper = p_228419_9_;
        Matrix4f matrix4f = p_228419_2_.copy();
        matrix4f.mul(p_228419_1_);
        matrix4f.invert();
        this.debugTerrainFrustumPosition.x = p_228419_3_;
        this.debugTerrainFrustumPosition.y = p_228419_5_;
        this.debugTerrainFrustumPosition.z = p_228419_7_;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 8; ++i)
        {
            this.debugTerrainMatrix[i].transform(matrix4f);
            this.debugTerrainMatrix[i].perspectiveDivide();
        }
    }

    public void updateCameraAndRender(MatrixStack matrixStackIn, float partialTicks, long finishTimeNano, boolean drawBlockOutline, ActiveRenderInfo activeRenderInfoIn, GameRenderer gameRendererIn, LightTexture lightmapIn, Matrix4f projectionIn)
    {
        TileEntityRendererDispatcher.instance.prepare(this.world, this.mc.getTextureManager(), this.mc.fontRenderer, activeRenderInfoIn, this.mc.objectMouseOver);
        this.renderManager.cacheActiveRenderInfo(this.world, activeRenderInfoIn, this.mc.pointedEntity);
        IProfiler iprofiler = this.world.getProfiler();
        iprofiler.endStartSection("light_updates");
        this.mc.world.getChunkProvider().getLightManager().tick(Integer.MAX_VALUE, true, true);
        Vector3d vector3d = activeRenderInfoIn.getProjectedView();
        double d0 = vector3d.getX();
        double d1 = vector3d.getY();
        double d2 = vector3d.getZ();
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        iprofiler.endStartSection("culling");
        boolean flag = this.debugFixedClippingHelper != null;
        ClippingHelper clippinghelper;

        if (flag)
        {
            clippinghelper = this.debugFixedClippingHelper;
            clippinghelper.setCameraPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
        }
        else
        {
            clippinghelper = new ClippingHelper(matrix4f, projectionIn);
            clippinghelper.setCameraPosition(d0, d1, d2);
        }

        this.mc.getProfiler().endStartSection("captureFrustum");

        if (this.debugFixTerrainFrustum)
        {
            this.captureFrustum(matrix4f, projectionIn, vector3d.x, vector3d.y, vector3d.z, flag ? new ClippingHelper(matrix4f, projectionIn) : clippinghelper);
            this.debugFixTerrainFrustum = false;
        }

        iprofiler.endStartSection("clear");

        if (Config.isShaders())
        {
            Shaders.setViewport(0, 0, this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
        }
        else
        {
            RenderSystem.viewport(0, 0, this.mc.getMainWindow().getFramebufferWidth(), this.mc.getMainWindow().getFramebufferHeight());
        }

        FogRenderer.updateFogColor(activeRenderInfoIn, partialTicks, this.mc.world, this.mc.gameSettings.renderDistanceChunks, gameRendererIn.getBossColorModifier(partialTicks));
        RenderSystem.clear(16640, Minecraft.IS_RUNNING_ON_MAC);
        boolean flag1 = Config.isShaders();

        if (flag1)
        {
            Shaders.clearRenderBuffer();
            Shaders.setCamera(matrixStackIn, activeRenderInfoIn, partialTicks);
            Shaders.renderPrepare();
        }

        clippinghelper.disabled = Config.isShaders() && !Shaders.isFrustumCulling();
        float f = gameRendererIn.getFarPlaneDistance();
        boolean flag2 = this.mc.world.func_239132_a_().func_230493_a_(MathHelper.floor(d0), MathHelper.floor(d1)) || this.mc.ingameGUI.getBossOverlay().shouldCreateFog();

        if ((Config.isSkyEnabled() || Config.isSunMoonEnabled() || Config.isStarsEnabled()) && !Shaders.isShadowPass)
        {
            FogRenderer.setupFog(activeRenderInfoIn, FogRenderer.FogType.FOG_SKY, f, flag2, partialTicks);
            iprofiler.endStartSection("sky");

            if (flag1)
            {
                Shaders.beginSky();
            }

            this.renderSky(matrixStackIn, partialTicks);

            if (flag1)
            {
                Shaders.endSky();
            }
        }
        else
        {
            GlStateManager.disableBlend();
        }

        iprofiler.endStartSection("fog");
        FogRenderer.setupFog(activeRenderInfoIn, FogRenderer.FogType.FOG_TERRAIN, Math.max(f - 16.0F, 32.0F), flag2, partialTicks);
        iprofiler.endStartSection("terrain_setup");
        this.checkLoadVisibleChunks(activeRenderInfoIn, clippinghelper, this.mc.player.isSpectator());
        this.setupTerrain(activeRenderInfoIn, clippinghelper, flag, this.frameId++, this.mc.player.isSpectator());
        iprofiler.endStartSection("updatechunks");
        int i = 30;
        int j = this.mc.gameSettings.framerateLimit;
        long k = 33333333L;
        long l;

        if ((double)j == AbstractOption.FRAMERATE_LIMIT.getMaxValue())
        {
            l = 0L;
        }
        else
        {
            l = (long)(1000000000 / j);
        }

        long i1 = Util.nanoTime() - finishTimeNano;
        long j1 = this.renderTimeManager.nextValue(i1);
        long k1 = j1 * 3L / 2L;
        long l1 = MathHelper.clamp(k1, l, 33333333L);
        Lagometer.timerChunkUpload.start();
        this.updateChunks(finishTimeNano + l1);
        Lagometer.timerChunkUpload.end();
        iprofiler.endStartSection("terrain");
        Lagometer.timerTerrain.start();

        if (this.mc.gameSettings.ofSmoothFps)
        {
            this.mc.getProfiler().endStartSection("finish");
            GL11.glFinish();
            this.mc.getProfiler().endStartSection("terrain");
        }

        if (Config.isFogOff() && FogRenderer.fogStandard)
        {
            GlStateManager.setFogAllowed(false);
        }

        this.renderBlockLayer(RenderType.getSolid(), matrixStackIn, d0, d1, d2);
        this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, this.mc.gameSettings.mipmapLevels > 0);
        this.renderBlockLayer(RenderType.getCutoutMipped(), matrixStackIn, d0, d1, d2);
        this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        this.renderBlockLayer(RenderType.getCutout(), matrixStackIn, d0, d1, d2);

        if (flag1)
        {
            ShadersRender.endTerrain();
        }

        Lagometer.timerTerrain.end();

        if (this.world.func_239132_a_().func_239217_c_())
        {
            RenderHelper.setupDiffuseGuiLighting(matrixStackIn.getLast().getMatrix());
        }
        else
        {
            RenderHelper.setupLevelDiffuseLighting(matrixStackIn.getLast().getMatrix());
        }

        if (flag1)
        {
            Shaders.beginEntities();
        }

        ItemFrameRenderer.updateItemRenderDistance();
        iprofiler.endStartSection("entities");
        ++renderEntitiesCounter;
        this.countEntitiesRendered = 0;
        this.countEntitiesHidden = 0;
        this.countTileEntitiesRendered = 0;

        if (this.field_239223_G_ != null)
        {
            this.field_239223_G_.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
            this.field_239223_G_.func_237506_a_(this.mc.getFramebuffer());
            this.mc.getFramebuffer().bindFramebuffer(false);
        }

        if (this.field_239225_I_ != null)
        {
            this.field_239225_I_.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        }

        if (this.isRenderEntityOutlines())
        {
            this.entityOutlineFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
            this.mc.getFramebuffer().bindFramebuffer(false);
        }

        boolean flag3 = false;
        IRenderTypeBuffer.Impl irendertypebuffer$impl = this.renderTypeTextures.getBufferSource();

        if (Config.isFastRender())
        {
            RenderStateManager.enableCache();
        }

        for (WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : this.renderInfosEntities)
        {
            ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = worldrenderer$localrenderinformationcontainer.renderChunk;
            Chunk chunk = chunkrenderdispatcher$chunkrender.getChunk();

            for (Entity entity : chunk.getEntityLists()[chunkrenderdispatcher$chunkrender.getPosition().getY() / 16])
            {
                boolean flag4 = entity == this.mc.player && !this.mc.player.isSpectator();

                if ((this.renderManager.shouldRender(entity, clippinghelper, d0, d1, d2) || entity.isRidingOrBeingRiddenBy(this.mc.player)) && (entity != activeRenderInfoIn.getRenderViewEntity() || activeRenderInfoIn.isThirdPerson() || activeRenderInfoIn.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)activeRenderInfoIn.getRenderViewEntity()).isSleeping()) && (!(entity instanceof ClientPlayerEntity) || activeRenderInfoIn.getRenderViewEntity() == entity || flag4))
                {
                    String s = entity.getClass().getName();
                    List<Entity> list = this.mapEntityLists.get(s);

                    if (list == null)
                    {
                        list = new ArrayList<>();
                        this.mapEntityLists.put(s, list);
                    }

                    list.add(entity);
                }
            }
        }

        for (List<Entity> list1 : this.mapEntityLists.values())
        {
            for (Entity entity1 : list1)
            {
                ++this.countEntitiesRendered;

                if (entity1.ticksExisted == 0)
                {
                    entity1.lastTickPosX = entity1.getPosX();
                    entity1.lastTickPosY = entity1.getPosY();
                    entity1.lastTickPosZ = entity1.getPosZ();
                }

                IRenderTypeBuffer irendertypebuffer;

                if (this.isRenderEntityOutlines() && this.mc.isEntityGlowing(entity1))
                {
                    flag3 = true;
                    OutlineLayerBuffer outlinelayerbuffer = this.renderTypeTextures.getOutlineBufferSource();
                    irendertypebuffer = outlinelayerbuffer;
                    int k2 = entity1.getTeamColor();
                    int l2 = 255;
                    int i3 = k2 >> 16 & 255;
                    int i2 = k2 >> 8 & 255;
                    int j2 = k2 & 255;
                    outlinelayerbuffer.setColor(i3, i2, j2, 255);
                }
                else
                {
                    irendertypebuffer = irendertypebuffer$impl;
                }

                this.renderedEntity = entity1;

                if (flag1)
                {
                    Shaders.nextEntity(entity1);
                }

                this.renderEntity(entity1, d0, d1, d2, partialTicks, matrixStackIn, irendertypebuffer);
                this.renderedEntity = null;
            }

            list1.clear();
        }

        this.checkMatrixStack(matrixStackIn);
        irendertypebuffer$impl.finish(RenderType.getEntitySolid(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
        irendertypebuffer$impl.finish(RenderType.getEntityCutout(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
        irendertypebuffer$impl.finish(RenderType.getEntityCutoutNoCull(AtlasTexture.LOCATION_BLOCKS_TEXTURE));
        irendertypebuffer$impl.finish(RenderType.getEntitySmoothCutout(AtlasTexture.LOCATION_BLOCKS_TEXTURE));

        if (flag1)
        {
            Shaders.endEntities();
            Shaders.beginBlockEntities();
        }

        iprofiler.endStartSection("blockentities");
        SignTileEntityRenderer.updateTextRenderDistance();
        boolean flag5 = Reflector.IForgeTileEntity_getRenderBoundingBox.exists();
        ClippingHelper clippinghelper1 = clippinghelper;
        label354:

        for (WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer1 : this.renderInfosTileEntities)
        {
            List<TileEntity> list2 = worldrenderer$localrenderinformationcontainer1.renderChunk.getCompiledChunk().getTileEntities();

            if (!list2.isEmpty())
            {
                Iterator iterator1 = list2.iterator();

                while (true)
                {
                    TileEntity tileentity1;
                    AxisAlignedBB axisalignedbb1;

                    do
                    {
                        if (!iterator1.hasNext())
                        {
                            continue label354;
                        }

                        tileentity1 = (TileEntity)iterator1.next();

                        if (!flag5)
                        {
                            break;
                        }

                        axisalignedbb1 = (AxisAlignedBB)Reflector.call(tileentity1, Reflector.IForgeTileEntity_getRenderBoundingBox);
                    }
                    while (axisalignedbb1 != null && !clippinghelper1.isBoundingBoxInFrustum(axisalignedbb1));

                    if (flag1)
                    {
                        Shaders.nextBlockEntity(tileentity1);
                    }

                    BlockPos blockpos3 = tileentity1.getPos();
                    IRenderTypeBuffer irendertypebuffer1 = irendertypebuffer$impl;
                    matrixStackIn.push();
                    matrixStackIn.translate((double)blockpos3.getX() - d0, (double)blockpos3.getY() - d1, (double)blockpos3.getZ() - d2);
                    SortedSet<DestroyBlockProgress> sortedset = this.damageProgress.get(blockpos3.toLong());

                    if (sortedset != null && !sortedset.isEmpty())
                    {
                        int j3 = sortedset.last().getPartialBlockDamage();

                        if (j3 >= 0)
                        {
                            MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
                            IVertexBuilder ivertexbuilder = new MatrixApplyingVertexBuilder(this.renderTypeTextures.getCrumblingBufferSource().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(j3)), matrixstack$entry.getMatrix(), matrixstack$entry.getNormal());
                            irendertypebuffer1 = (p_lambda$updateCameraAndRender$1_2_) ->
                            {
                                IVertexBuilder ivertexbuilder3 = irendertypebuffer$impl.getBuffer(p_lambda$updateCameraAndRender$1_2_);
                                return p_lambda$updateCameraAndRender$1_2_.isUseDelegate() ? VertexBuilderUtils.newDelegate(ivertexbuilder, ivertexbuilder3) : ivertexbuilder3;
                            };
                        }
                    }

                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity1, partialTicks, matrixStackIn, irendertypebuffer1);
                    matrixStackIn.pop();
                    ++this.countTileEntitiesRendered;
                }
            }
        }

        synchronized (this.setTileEntities)
        {
            Iterator iterator = this.setTileEntities.iterator();
            label329:

            while (true)
            {
                TileEntity tileentity;
                AxisAlignedBB axisalignedbb;

                do
                {
                    if (!iterator.hasNext())
                    {
                        break label329;
                    }

                    tileentity = (TileEntity)iterator.next();

                    if (!flag5)
                    {
                        break;
                    }

                    axisalignedbb = (AxisAlignedBB)Reflector.call(tileentity, Reflector.IForgeTileEntity_getRenderBoundingBox);
                }
                while (axisalignedbb != null && !clippinghelper1.isBoundingBoxInFrustum(axisalignedbb));

                if (flag1)
                {
                    Shaders.nextBlockEntity(tileentity);
                }

                BlockPos blockpos2 = tileentity.getPos();
                matrixStackIn.push();
                matrixStackIn.translate((double)blockpos2.getX() - d0, (double)blockpos2.getY() - d1, (double)blockpos2.getZ() - d2);
                TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, matrixStackIn, irendertypebuffer$impl);
                matrixStackIn.pop();
                ++this.countTileEntitiesRendered;
            }
        }

        this.checkMatrixStack(matrixStackIn);
        irendertypebuffer$impl.finish(RenderType.getSolid());
        irendertypebuffer$impl.finish(Atlases.getSolidBlockType());
        irendertypebuffer$impl.finish(Atlases.getCutoutBlockType());
        irendertypebuffer$impl.finish(Atlases.getBedType());
        irendertypebuffer$impl.finish(Atlases.getShulkerBoxType());
        irendertypebuffer$impl.finish(Atlases.getSignType());
        irendertypebuffer$impl.finish(Atlases.getChestType());
        irendertypebuffer$impl.finish(Atlases.getBannerType());
        this.renderTypeTextures.getOutlineBufferSource().finish();

        if (Config.isFastRender())
        {
            RenderStateManager.disableCache();
        }

        if (flag3)
        {
            this.entityOutlineShader.render(partialTicks);
            this.mc.getFramebuffer().bindFramebuffer(false);
        }

        if (flag1)
        {
            Shaders.endBlockEntities();
        }

        this.renderOverlayDamaged = true;
        iprofiler.endStartSection("destroyProgress");

        for (Entry<SortedSet<DestroyBlockProgress>> entry : this.damageProgress.long2ObjectEntrySet())
        {
            BlockPos blockpos1 = BlockPos.fromLong(entry.getLongKey());
            double d3 = (double)blockpos1.getX() - d0;
            double d4 = (double)blockpos1.getY() - d1;
            double d5 = (double)blockpos1.getZ() - d2;

            if (!(d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D))
            {
                SortedSet<DestroyBlockProgress> sortedset1 = entry.getValue();

                if (sortedset1 != null && !sortedset1.isEmpty())
                {
                    int k3 = sortedset1.last().getPartialBlockDamage();
                    matrixStackIn.push();
                    matrixStackIn.translate((double)blockpos1.getX() - d0, (double)blockpos1.getY() - d1, (double)blockpos1.getZ() - d2);
                    MatrixStack.Entry matrixstack$entry1 = matrixStackIn.getLast();
                    IVertexBuilder ivertexbuilder1 = new MatrixApplyingVertexBuilder(this.renderTypeTextures.getCrumblingBufferSource().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(k3)), matrixstack$entry1.getMatrix(), matrixstack$entry1.getNormal());
                    this.mc.getBlockRendererDispatcher().renderBlockDamage(this.world.getBlockState(blockpos1), blockpos1, this.world, matrixStackIn, ivertexbuilder1);
                    matrixStackIn.pop();
                }
            }
        }

        this.renderOverlayDamaged = false;
        RenderUtils.flushRenderBuffers();
        --renderEntitiesCounter;
        this.checkMatrixStack(matrixStackIn);
        RayTraceResult raytraceresult = this.mc.objectMouseOver;

        if (drawBlockOutline && raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK)
        {
            iprofiler.endStartSection("outline");
            BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
            BlockState blockstate = this.world.getBlockState(blockpos);

            if (flag1)
            {
                ShadersRender.beginOutline();
            }

            boolean flag7;

            if (Reflector.IForgeBlockState_isAir2.exists() && Reflector.ForgeHooksClient_onDrawBlockHighlight.exists())
            {
                flag7 = !Reflector.callBoolean(Reflector.ForgeHooksClient_onDrawBlockHighlight, this, activeRenderInfoIn, raytraceresult, partialTicks, matrixStackIn, irendertypebuffer$impl) && !Reflector.callBoolean(blockstate, Reflector.IForgeBlockState_isAir2, this.world, blockpos) && this.world.getWorldBorder().contains(blockpos);
            }
            else
            {
                flag7 = !blockstate.isAir() && this.world.getWorldBorder().contains(blockpos);
            }

            if (flag7)
            {
                IVertexBuilder ivertexbuilder2 = irendertypebuffer$impl.getBuffer(RenderType.getLines());
                this.drawSelectionBox(matrixStackIn, ivertexbuilder2, activeRenderInfoIn.getRenderViewEntity(), d0, d1, d2, blockpos, blockstate);
            }

            if (flag1)
            {
                irendertypebuffer$impl.finish(RenderType.getLines());
                ShadersRender.endOutline();
            }
        }
        else if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY)
        {
            Reflector.ForgeHooksClient_onDrawBlockHighlight.call(this, activeRenderInfoIn, raytraceresult, partialTicks, matrixStackIn, irendertypebuffer$impl);
        }

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
        boolean flag6 = GlStateManager.isFogEnabled();
        GlStateManager.disableFog();

        if (flag1)
        {
            ShadersRender.beginDebug();
        }

        this.mc.debugRenderer.render(matrixStackIn, irendertypebuffer$impl, d0, d1, d2);
        RenderSystem.popMatrix();
        irendertypebuffer$impl.finish(Atlases.getTranslucentCullBlockType());
        irendertypebuffer$impl.finish(Atlases.getBannerType());
        irendertypebuffer$impl.finish(Atlases.getShieldType());
        irendertypebuffer$impl.finish(RenderType.getArmorGlint());
        irendertypebuffer$impl.finish(RenderType.getArmorEntityGlint());
        irendertypebuffer$impl.finish(RenderType.getGlint());
        irendertypebuffer$impl.finish(RenderType.getGlintDirect());
        irendertypebuffer$impl.finish(RenderType.getGlintTranslucent());
        irendertypebuffer$impl.finish(RenderType.getEntityGlint());
        irendertypebuffer$impl.finish(RenderType.getEntityGlintDirect());
        irendertypebuffer$impl.finish(RenderType.getWaterMask());
        this.renderTypeTextures.getCrumblingBufferSource().finish();
        GlStateManager.setFogEnabled(flag6);

        if (flag1)
        {
            irendertypebuffer$impl.finish();
            ShadersRender.endDebug();
            Shaders.preRenderHand();
            ShadersRender.renderHand0(gameRendererIn, matrixStackIn, activeRenderInfoIn, partialTicks);
            Shaders.preWater();
        }

        if (this.field_239227_K_ != null)
        {
            irendertypebuffer$impl.finish(RenderType.getLines());
            irendertypebuffer$impl.finish();
            this.field_239222_F_.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
            this.field_239222_F_.func_237506_a_(this.mc.getFramebuffer());
            iprofiler.endStartSection("translucent");
            this.renderBlockLayer(RenderType.getTranslucent(), matrixStackIn, d0, d1, d2);
            iprofiler.endStartSection("string");
            this.renderBlockLayer(RenderType.getTripwire(), matrixStackIn, d0, d1, d2);
            this.field_239224_H_.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
            this.field_239224_H_.func_237506_a_(this.mc.getFramebuffer());
            RenderState.field_239237_T_.setupRenderState();
            iprofiler.endStartSection("particles");
            this.mc.particles.renderParticles(matrixStackIn, irendertypebuffer$impl, lightmapIn, activeRenderInfoIn, partialTicks, clippinghelper);
            RenderState.field_239237_T_.clearRenderState();
        }
        else
        {
            iprofiler.endStartSection("translucent");

            if (flag1)
            {
                Shaders.beginWater();
            }

            this.renderBlockLayer(RenderType.getTranslucent(), matrixStackIn, d0, d1, d2);

            if (flag1)
            {
                Shaders.endWater();
            }

            irendertypebuffer$impl.finish(RenderType.getLines());
            irendertypebuffer$impl.finish();
            iprofiler.endStartSection("string");
            this.renderBlockLayer(RenderType.getTripwire(), matrixStackIn, d0, d1, d2);
            iprofiler.endStartSection("particles");

            if (flag1)
            {
                Shaders.beginParticles();
            }

            this.mc.particles.renderParticles(matrixStackIn, irendertypebuffer$impl, lightmapIn, activeRenderInfoIn, partialTicks, clippinghelper);

            if (flag1)
            {
                Shaders.endParticles();
            }
        }

        GlStateManager.setFogAllowed(true);
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
        EventManager.call(new EventRender3D(partialTicks));

        if (this.mc.gameSettings.getCloudOption() != CloudOption.OFF)
        {
            if (this.field_239227_K_ != null)
            {
                this.field_239226_J_.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
                RenderState.field_239239_V_.setupRenderState();
                iprofiler.endStartSection("clouds");
                this.renderClouds(matrixStackIn, partialTicks, d0, d1, d2);
                RenderState.field_239239_V_.clearRenderState();
            }
            else
            {
                iprofiler.endStartSection("clouds");
                this.renderClouds(matrixStackIn, partialTicks, d0, d1, d2);
            }
        }

        if (this.field_239227_K_ != null)
        {
            RenderState.field_239238_U_.setupRenderState();
            iprofiler.endStartSection("weather");
            this.renderRainSnow(lightmapIn, partialTicks, d0, d1, d2);
            this.renderWorldBorder(activeRenderInfoIn);
            RenderState.field_239238_U_.clearRenderState();
            this.field_239227_K_.render(partialTicks);
            this.mc.getFramebuffer().bindFramebuffer(false);
        }
        else
        {
            RenderSystem.depthMask(false);

            if (Config.isShaders())
            {
                GlStateManager.depthMask(Shaders.isRainDepth());
            }

            iprofiler.endStartSection("weather");

            if (flag1)
            {
                Shaders.beginWeather();
            }

            this.renderRainSnow(lightmapIn, partialTicks, d0, d1, d2);

            if (flag1)
            {
                Shaders.endWeather();
            }

            this.renderWorldBorder(activeRenderInfoIn);
            RenderSystem.depthMask(true);
        }

        this.renderDebug(activeRenderInfoIn);
        RenderSystem.shadeModel(7424);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        FogRenderer.resetFog();
    }

    public void checkMatrixStack(MatrixStack matrixStackIn)
    {
        if (!matrixStackIn.clear())
        {
            throw new IllegalStateException("Pose stack not empty");
        }
    }

    public void renderEntity(Entity entityIn, double camX, double camY, double camZ, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn)
    {
        double d0 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
        double d1 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
        double d2 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());
        float f = MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw);
        this.renderManager.renderEntityStatic(entityIn, d0 - camX, d1 - camY, d2 - camZ, f, partialTicks, matrixStackIn, bufferIn, this.renderManager.getPackedLight(entityIn, partialTicks));
    }

    public void renderBlockLayer(RenderType blockLayerIn, MatrixStack matrixStackIn, double xIn, double yIn, double zIn)
    {
        blockLayerIn.setupRenderState();
        boolean flag = Config.isShaders();

        if (blockLayerIn == RenderType.getTranslucent() && !Shaders.isShadowPass)
        {
            this.mc.getProfiler().startSection("translucent_sort");
            double d0 = xIn - this.prevRenderSortX;
            double d1 = yIn - this.prevRenderSortY;
            double d2 = zIn - this.prevRenderSortZ;

            if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)
            {
                this.prevRenderSortX = xIn;
                this.prevRenderSortY = yIn;
                this.prevRenderSortZ = zIn;
                int i = 0;
                this.chunksToResortTransparency.clear();

                for (WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer1 : this.renderInfos)
                {
                    if (i < 15 && worldrenderer$localrenderinformationcontainer1.renderChunk.getCompiledChunk().isLayerStarted(blockLayerIn))
                    {
                        this.chunksToResortTransparency.add(worldrenderer$localrenderinformationcontainer1.renderChunk);
                        ++i;
                    }
                }
            }

            this.mc.getProfiler().endSection();
        }

        this.mc.getProfiler().startSection("filterempty");

        if (flag)
        {
            ShadersRender.preRenderChunkLayer(blockLayerIn);
        }

        boolean flag2 = SmartAnimations.isActive();
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrixStackIn.getLast().getMatrix());
        this.mc.getProfiler().endStartSection(() ->
        {
            return "render_" + blockLayerIn;
        });
        boolean flag1 = blockLayerIn != RenderType.getTranslucent();
        ObjectListIterator<WorldRenderer.LocalRenderInformationContainer> objectlistiterator = this.renderInfos.listIterator(flag1 ? 0 : this.renderInfos.size());

        if (Config.isRenderRegions())
        {
            int j = Integer.MIN_VALUE;
            int k = Integer.MIN_VALUE;
            VboRegion vboregion2 = null;
            Map<PairInt, Map<VboRegion, List<VertexBuffer>>> map = this.mapRegionLayers.computeIfAbsent(blockLayerIn, (p_lambda$renderBlockLayer$3_0_) ->
            {
                return new LinkedHashMap(16);
            });
            Map<VboRegion, List<VertexBuffer>> map1 = null;
            List<VertexBuffer> list1 = null;

            while (true)
            {
                if (flag1)
                {
                    if (!objectlistiterator.hasNext())
                    {
                        break;
                    }
                }
                else if (!objectlistiterator.hasPrevious())
                {
                    break;
                }

                WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer2 = flag1 ? objectlistiterator.next() : objectlistiterator.previous();
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = worldrenderer$localrenderinformationcontainer2.renderChunk;

                if (!chunkrenderdispatcher$chunkrender.getCompiledChunk().isLayerEmpty(blockLayerIn))
                {
                    VertexBuffer vertexbuffer1 = chunkrenderdispatcher$chunkrender.getVertexBuffer(blockLayerIn);
                    VboRegion vboregion = vertexbuffer1.getVboRegion();

                    if (chunkrenderdispatcher$chunkrender.regionX != j || chunkrenderdispatcher$chunkrender.regionZ != k)
                    {
                        PairInt pairint = PairInt.of(chunkrenderdispatcher$chunkrender.regionX, chunkrenderdispatcher$chunkrender.regionZ);
                        map1 = map.computeIfAbsent(pairint, (p_lambda$renderBlockLayer$4_0_) ->
                        {
                            return new LinkedHashMap(8);
                        });
                        j = chunkrenderdispatcher$chunkrender.regionX;
                        k = chunkrenderdispatcher$chunkrender.regionZ;
                        vboregion2 = null;
                    }

                    if (vboregion != vboregion2)
                    {
                        list1 = map1.computeIfAbsent(vboregion, (p_lambda$renderBlockLayer$5_0_) ->
                        {
                            return new ArrayList();
                        });
                        vboregion2 = vboregion;
                    }

                    list1.add(vertexbuffer1);

                    if (SmartAnimations.isActive())
                    {
                        BitSet bitset1 = chunkrenderdispatcher$chunkrender.getCompiledChunk().getAnimatedSprites(blockLayerIn);

                        if (bitset1 != null)
                        {
                            SmartAnimations.spritesRendered(bitset1);
                        }
                    }
                }
            }

            for (java.util.Map.Entry<PairInt, Map<VboRegion, List<VertexBuffer>>> entry : map.entrySet())
            {
                PairInt pairint1 = entry.getKey();
                Map<VboRegion, List<VertexBuffer>> map2 = entry.getValue();

                for (java.util.Map.Entry<VboRegion, List<VertexBuffer>> entry1 : map2.entrySet())
                {
                    VboRegion vboregion1 = entry1.getKey();
                    List<VertexBuffer> list = entry1.getValue();

                    for (VertexBuffer vertexbuffer2 : list)
                    {
                        vertexbuffer2.draw(7);
                    }

                    this.drawRegion(pairint1.getLeft(), 0, pairint1.getRight(), xIn, yIn, zIn, vboregion1);
                    list.clear();
                }
            }
        }
        else
        {
            while (true)
            {
                if (flag1)
                {
                    if (!objectlistiterator.hasNext())
                    {
                        break;
                    }
                }
                else if (!objectlistiterator.hasPrevious())
                {
                    break;
                }

                WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer = flag1 ? objectlistiterator.next() : objectlistiterator.previous();
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender1 = worldrenderer$localrenderinformationcontainer.renderChunk;

                if (!chunkrenderdispatcher$chunkrender1.getCompiledChunk().isLayerEmpty(blockLayerIn))
                {
                    VertexBuffer vertexbuffer = chunkrenderdispatcher$chunkrender1.getVertexBuffer(blockLayerIn);
                    GlStateManager.pushMatrix();
                    BlockPos blockpos = chunkrenderdispatcher$chunkrender1.getPosition();
                    GlStateManager.translated((double)blockpos.getX() - xIn, (double)blockpos.getY() - yIn, (double)blockpos.getZ() - zIn);
                    vertexbuffer.bindBuffer();
                    DefaultVertexFormats.BLOCK.setupBufferState(0L);
                    GlStateManager.lockClientState();

                    if (flag)
                    {
                        ShadersRender.setupArrayPointersVbo();
                    }

                    vertexbuffer.draw(7);
                    GlStateManager.popMatrix();

                    if (flag2)
                    {
                        BitSet bitset = chunkrenderdispatcher$chunkrender1.getCompiledChunk().getAnimatedSprites(blockLayerIn);

                        if (bitset != null)
                        {
                            SmartAnimations.spritesRendered(bitset);
                        }
                    }
                }
            }
        }

        GlStateManager.unlockClientState();
        RenderSystem.popMatrix();

        if (Config.isMultiTexture())
        {
            this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        }

        VertexBuffer.unbindBuffer();
        RenderSystem.clearCurrentColor();
        DefaultVertexFormats.BLOCK.clearBufferState();
        this.mc.getProfiler().endSection();

        if (flag)
        {
            ShadersRender.postRenderChunkLayer(blockLayerIn);
        }

        blockLayerIn.clearRenderState();
    }

    private void drawRegion(int p_drawRegion_1_, int p_drawRegion_2_, int p_drawRegion_3_, double p_drawRegion_4_, double p_drawRegion_6_, double p_drawRegion_8_, VboRegion p_drawRegion_10_)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translated((double)p_drawRegion_1_ - p_drawRegion_4_, (double)p_drawRegion_2_ - p_drawRegion_6_, (double)p_drawRegion_3_ - p_drawRegion_8_);
        p_drawRegion_10_.finishDraw();
        GlStateManager.lockClientState();
        GlStateManager.popMatrix();
    }

    private void renderDebug(ActiveRenderInfo activeRenderInfoIn)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        if (this.mc.debugWireframe || this.mc.debugChunkPath)
        {
            double d0 = activeRenderInfoIn.getProjectedView().getX();
            double d1 = activeRenderInfoIn.getProjectedView().getY();
            double d2 = activeRenderInfoIn.getProjectedView().getZ();
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableTexture();

            for (WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : this.renderInfos)
            {
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = worldrenderer$localrenderinformationcontainer.renderChunk;
                RenderSystem.pushMatrix();
                BlockPos blockpos = chunkrenderdispatcher$chunkrender.getPosition();
                RenderSystem.translated((double)blockpos.getX() - d0, (double)blockpos.getY() - d1, (double)blockpos.getZ() - d2);

                if (this.mc.debugWireframe)
                {
                    bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0F);
                    int i = worldrenderer$localrenderinformationcontainer.counter == 0 ? 0 : MathHelper.hsvToRGB((float)worldrenderer$localrenderinformationcontainer.counter / 50.0F, 0.9F, 0.9F);
                    int j = i >> 16 & 255;
                    int k = i >> 8 & 255;
                    int l = i & 255;
                    Direction direction = worldrenderer$localrenderinformationcontainer.facing;

                    if (direction != null)
                    {
                        bufferbuilder.pos(8.0D, 8.0D, 8.0D).color(j, k, l, 255).endVertex();
                        bufferbuilder.pos((double)(8 - 16 * direction.getXOffset()), (double)(8 - 16 * direction.getYOffset()), (double)(8 - 16 * direction.getZOffset())).color(j, k, l, 255).endVertex();
                    }

                    tessellator.draw();
                    RenderSystem.lineWidth(1.0F);
                }

                if (this.mc.debugChunkPath && !chunkrenderdispatcher$chunkrender.getCompiledChunk().isEmpty())
                {
                    bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
                    RenderSystem.lineWidth(10.0F);
                    int i1 = 0;

                    for (Direction direction2 : FACINGS)
                    {
                        for (Direction direction1 : FACINGS)
                        {
                            boolean flag = chunkrenderdispatcher$chunkrender.getCompiledChunk().isVisible(direction2, direction1);

                            if (!flag)
                            {
                                ++i1;
                                bufferbuilder.pos((double)(8 + 8 * direction2.getXOffset()), (double)(8 + 8 * direction2.getYOffset()), (double)(8 + 8 * direction2.getZOffset())).color(1, 0, 0, 1).endVertex();
                                bufferbuilder.pos((double)(8 + 8 * direction1.getXOffset()), (double)(8 + 8 * direction1.getYOffset()), (double)(8 + 8 * direction1.getZOffset())).color(1, 0, 0, 1).endVertex();
                            }
                        }
                    }

                    tessellator.draw();
                    RenderSystem.lineWidth(1.0F);

                    if (i1 > 0)
                    {
                        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                        float f = 0.5F;
                        float f1 = 0.2F;
                        bufferbuilder.pos(0.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(15.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        bufferbuilder.pos(0.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                        tessellator.draw();
                    }
                }

                RenderSystem.popMatrix();
            }

            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
        }

        if (this.debugFixedClippingHelper != null)
        {
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(10.0F);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.debugTerrainFrustumPosition.x - activeRenderInfoIn.getProjectedView().x), (float)(this.debugTerrainFrustumPosition.y - activeRenderInfoIn.getProjectedView().y), (float)(this.debugTerrainFrustumPosition.z - activeRenderInfoIn.getProjectedView().z));
            RenderSystem.depthMask(true);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            this.addDebugQuad(bufferbuilder, 0, 1, 2, 3, 0, 1, 1);
            this.addDebugQuad(bufferbuilder, 4, 5, 6, 7, 1, 0, 0);
            this.addDebugQuad(bufferbuilder, 0, 1, 5, 4, 1, 1, 0);
            this.addDebugQuad(bufferbuilder, 2, 3, 7, 6, 0, 0, 1);
            this.addDebugQuad(bufferbuilder, 0, 4, 7, 3, 0, 1, 0);
            this.addDebugQuad(bufferbuilder, 1, 5, 6, 2, 1, 0, 1);
            tessellator.draw();
            RenderSystem.depthMask(false);
            bufferbuilder.begin(1, DefaultVertexFormats.POSITION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.addDebugVertex(bufferbuilder, 0);
            this.addDebugVertex(bufferbuilder, 1);
            this.addDebugVertex(bufferbuilder, 1);
            this.addDebugVertex(bufferbuilder, 2);
            this.addDebugVertex(bufferbuilder, 2);
            this.addDebugVertex(bufferbuilder, 3);
            this.addDebugVertex(bufferbuilder, 3);
            this.addDebugVertex(bufferbuilder, 0);
            this.addDebugVertex(bufferbuilder, 4);
            this.addDebugVertex(bufferbuilder, 5);
            this.addDebugVertex(bufferbuilder, 5);
            this.addDebugVertex(bufferbuilder, 6);
            this.addDebugVertex(bufferbuilder, 6);
            this.addDebugVertex(bufferbuilder, 7);
            this.addDebugVertex(bufferbuilder, 7);
            this.addDebugVertex(bufferbuilder, 4);
            this.addDebugVertex(bufferbuilder, 0);
            this.addDebugVertex(bufferbuilder, 4);
            this.addDebugVertex(bufferbuilder, 1);
            this.addDebugVertex(bufferbuilder, 5);
            this.addDebugVertex(bufferbuilder, 2);
            this.addDebugVertex(bufferbuilder, 6);
            this.addDebugVertex(bufferbuilder, 3);
            this.addDebugVertex(bufferbuilder, 7);
            tessellator.draw();
            RenderSystem.popMatrix();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableTexture();
            RenderSystem.lineWidth(1.0F);
        }
    }

    private void addDebugVertex(IVertexBuilder bufferIn, int vertex)
    {
        bufferIn.pos((double)this.debugTerrainMatrix[vertex].getX(), (double)this.debugTerrainMatrix[vertex].getY(), (double)this.debugTerrainMatrix[vertex].getZ()).endVertex();
    }

    private void addDebugQuad(IVertexBuilder bufferIn, int vertex1, int vertex2, int vertex3, int vertex4, int red, int green, int blue)
    {
        float f = 0.25F;
        bufferIn.pos((double)this.debugTerrainMatrix[vertex1].getX(), (double)this.debugTerrainMatrix[vertex1].getY(), (double)this.debugTerrainMatrix[vertex1].getZ()).color((float)red, (float)green, (float)blue, 0.25F).endVertex();
        bufferIn.pos((double)this.debugTerrainMatrix[vertex2].getX(), (double)this.debugTerrainMatrix[vertex2].getY(), (double)this.debugTerrainMatrix[vertex2].getZ()).color((float)red, (float)green, (float)blue, 0.25F).endVertex();
        bufferIn.pos((double)this.debugTerrainMatrix[vertex3].getX(), (double)this.debugTerrainMatrix[vertex3].getY(), (double)this.debugTerrainMatrix[vertex3].getZ()).color((float)red, (float)green, (float)blue, 0.25F).endVertex();
        bufferIn.pos((double)this.debugTerrainMatrix[vertex4].getX(), (double)this.debugTerrainMatrix[vertex4].getY(), (double)this.debugTerrainMatrix[vertex4].getZ()).color((float)red, (float)green, (float)blue, 0.25F).endVertex();
    }

    public void tick()
    {
        ++this.ticks;

        if (this.ticks % 20 == 0)
        {
            Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext())
            {
                DestroyBlockProgress destroyblockprogress = iterator.next();
                int i = destroyblockprogress.getCreationCloudUpdateTick();

                if (this.ticks - i > 400)
                {
                    iterator.remove();
                    this.removeDamageProgress(destroyblockprogress);
                }
            }
        }

        if (Config.isRenderRegions() && this.ticks % 20 == 0)
        {
            this.mapRegionLayers.clear();
        }
    }

    private void removeDamageProgress(DestroyBlockProgress progressIn)
    {
        long i = progressIn.getPosition().toLong();
        Set<DestroyBlockProgress> set = this.damageProgress.get(i);
        set.remove(progressIn);

        if (set.isEmpty())
        {
            this.damageProgress.remove(i);
        }
    }

    private void renderSkyEnd(MatrixStack matrixStackIn)
    {
        if (Config.isSkyEnabled())
        {
            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            this.textureManager.bindTexture(END_SKY_TEXTURES);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            for (int i = 0; i < 6; ++i)
            {
                matrixStackIn.push();

                if (i == 1)
                {
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                }

                if (i == 2)
                {
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-90.0F));
                }

                if (i == 3)
                {
                    matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
                }

                if (i == 4)
                {
                    matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
                }

                if (i == 5)
                {
                    matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-90.0F));
                }

                Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int j = 40;
                int k = 40;
                int l = 40;

                if (Config.isCustomColors())
                {
                    Vector3d vector3d = new Vector3d((double)j / 255.0D, (double)k / 255.0D, (double)l / 255.0D);
                    vector3d = CustomColors.getWorldSkyColor(vector3d, this.world, this.mc.getRenderViewEntity(), 0.0F);
                    j = (int)(vector3d.x * 255.0D);
                    k = (int)(vector3d.y * 255.0D);
                    l = (int)(vector3d.z * 255.0D);
                }

                bufferbuilder.pos(matrix4f, -100.0F, -100.0F, -100.0F).tex(0.0F, 0.0F).color(j, k, l, 255).endVertex();
                bufferbuilder.pos(matrix4f, -100.0F, -100.0F, 100.0F).tex(0.0F, 16.0F).color(j, k, l, 255).endVertex();
                bufferbuilder.pos(matrix4f, 100.0F, -100.0F, 100.0F).tex(16.0F, 16.0F).color(j, k, l, 255).endVertex();
                bufferbuilder.pos(matrix4f, 100.0F, -100.0F, -100.0F).tex(16.0F, 0.0F).color(j, k, l, 255).endVertex();
                tessellator.draw();
                matrixStackIn.pop();
            }

            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
        }
    }

    public void renderSky(MatrixStack matrixStackIn, float partialTicks)
    {
        if (Reflector.ForgeDimensionRenderInfo_getSkyRenderHandler.exists())
        {
            ISkyRenderHandler iskyrenderhandler = (ISkyRenderHandler)Reflector.call(this.world.func_239132_a_(), Reflector.ForgeDimensionRenderInfo_getSkyRenderHandler);

            if (iskyrenderhandler != null)
            {
                iskyrenderhandler.render(this.ticks, partialTicks, matrixStackIn, this.world, this.mc);
                return;
            }
        }

        if (this.mc.world.func_239132_a_().func_241683_c_() == DimensionRenderInfo.FogType.END)
        {
            this.renderSkyEnd(matrixStackIn);
        }
        else if (this.mc.world.func_239132_a_().func_241683_c_() == DimensionRenderInfo.FogType.NORMAL)
        {
            RenderSystem.disableTexture();
            boolean flag = Config.isShaders();

            if (flag)
            {
                Shaders.disableTexture2D();
            }

            Vector3d vector3d = this.world.getSkyColor(this.mc.gameRenderer.getActiveRenderInfo().getBlockPos(), partialTicks);
            vector3d = CustomColors.getSkyColor(vector3d, this.mc.world, this.mc.getRenderViewEntity().getPosX(), this.mc.getRenderViewEntity().getPosY() + 1.0D, this.mc.getRenderViewEntity().getPosZ());

            if (flag)
            {
                Shaders.setSkyColor(vector3d);
            }

            float f = (float)vector3d.x;
            float f1 = (float)vector3d.y;
            float f2 = (float)vector3d.z;
            FogRenderer.applyFog();
            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.depthMask(false);
            RenderSystem.enableFog();

            if (flag)
            {
                Shaders.enableFog();
            }

            RenderSystem.color3f(f, f1, f2);

            if (flag)
            {
                Shaders.preSkyList(matrixStackIn);
            }

            if (Config.isSkyEnabled())
            {
                this.skyVBO.bindBuffer();
                this.skyVertexFormat.setupBufferState(0L);
                this.skyVBO.draw(matrixStackIn.getLast().getMatrix(), 7);
                VertexBuffer.unbindBuffer();
                this.skyVertexFormat.clearBufferState();
            }

            RenderSystem.disableFog();

            if (flag)
            {
                Shaders.disableFog();
            }

            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float[] afloat = this.world.func_239132_a_().func_230492_a_(this.world.func_242415_f(partialTicks), partialTicks);

            if (afloat != null && Config.isSunMoonEnabled())
            {
                RenderSystem.disableTexture();

                if (flag)
                {
                    Shaders.disableTexture2D();
                }

                if (flag)
                {
                    Shaders.setRenderStage(RenderStage.SUNSET);
                }

                RenderSystem.shadeModel(7425);
                matrixStackIn.push();
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                float f3 = MathHelper.sin(this.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F;
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(f3));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
                float f4 = afloat[0];
                float f5 = afloat[1];
                float f6 = afloat[2];
                Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, afloat[3]).endVertex();
                int i = 16;

                for (int j = 0; j <= 16; ++j)
                {
                    float f7 = (float)j * ((float)Math.PI * 2F) / 16.0F;
                    float f8 = MathHelper.sin(f7);
                    float f9 = MathHelper.cos(f7);
                    bufferbuilder.pos(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * afloat[3]).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
                }

                bufferbuilder.finishDrawing();
                WorldVertexBufferUploader.draw(bufferbuilder);
                matrixStackIn.pop();
                RenderSystem.shadeModel(7424);
            }

            RenderSystem.enableTexture();

            if (flag)
            {
                Shaders.enableTexture2D();
            }

            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            matrixStackIn.push();
            float f10 = 1.0F - this.world.getRainStrength(partialTicks);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, f10);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-90.0F));
            CustomSky.renderSky(this.world, this.textureManager, matrixStackIn, partialTicks);

            if (flag)
            {
                Shaders.preCelestialRotate(matrixStackIn);
            }

            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(this.world.func_242415_f(partialTicks) * 360.0F));

            if (flag)
            {
                Shaders.postCelestialRotate(matrixStackIn);
            }

            Matrix4f matrix4f1 = matrixStackIn.getLast().getMatrix();
            float f11 = 30.0F;

            if (Config.isSunTexture())
            {
                if (flag)
                {
                    Shaders.setRenderStage(RenderStage.SUN);
                }

                this.textureManager.bindTexture(SUN_TEXTURES);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(matrix4f1, -f11, 100.0F, -f11).tex(0.0F, 0.0F).endVertex();
                bufferbuilder.pos(matrix4f1, f11, 100.0F, -f11).tex(1.0F, 0.0F).endVertex();
                bufferbuilder.pos(matrix4f1, f11, 100.0F, f11).tex(1.0F, 1.0F).endVertex();
                bufferbuilder.pos(matrix4f1, -f11, 100.0F, f11).tex(0.0F, 1.0F).endVertex();
                bufferbuilder.finishDrawing();
                WorldVertexBufferUploader.draw(bufferbuilder);
            }

            f11 = 20.0F;

            if (Config.isMoonTexture())
            {
                if (flag)
                {
                    Shaders.setRenderStage(RenderStage.MOON);
                }

                this.textureManager.bindTexture(MOON_PHASES_TEXTURES);
                int k = this.world.getMoonPhase();
                int l = k % 4;
                int i1 = k / 4 % 2;
                float f13 = (float)(l + 0) / 4.0F;
                float f14 = (float)(i1 + 0) / 2.0F;
                float f15 = (float)(l + 1) / 4.0F;
                float f16 = (float)(i1 + 1) / 2.0F;
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(matrix4f1, -f11, -100.0F, f11).tex(f15, f16).endVertex();
                bufferbuilder.pos(matrix4f1, f11, -100.0F, f11).tex(f13, f16).endVertex();
                bufferbuilder.pos(matrix4f1, f11, -100.0F, -f11).tex(f13, f14).endVertex();
                bufferbuilder.pos(matrix4f1, -f11, -100.0F, -f11).tex(f15, f14).endVertex();
                bufferbuilder.finishDrawing();
                WorldVertexBufferUploader.draw(bufferbuilder);
            }

            RenderSystem.disableTexture();

            if (flag)
            {
                Shaders.disableTexture2D();
            }

            float f12 = this.world.getStarBrightness(partialTicks) * f10;

            if (f12 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.world))
            {
                if (flag)
                {
                    Shaders.setRenderStage(RenderStage.STARS);
                }

                RenderSystem.color4f(f12, f12, f12, f12);
                this.starVBO.bindBuffer();
                this.skyVertexFormat.setupBufferState(0L);
                this.starVBO.draw(matrixStackIn.getLast().getMatrix(), 7);
                VertexBuffer.unbindBuffer();
                this.skyVertexFormat.clearBufferState();
            }

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableFog();

            if (flag)
            {
                Shaders.enableFog();
            }

            matrixStackIn.pop();
            RenderSystem.disableTexture();

            if (flag)
            {
                Shaders.disableTexture2D();
            }

            RenderSystem.color3f(0.0F, 0.0F, 0.0F);
            double d0 = this.mc.player.getEyePosition(partialTicks).y - this.world.getWorldInfo().getVoidFogHeight();
            boolean flag1 = false;

            if (d0 < 0.0D)
            {
                if (flag)
                {
                    Shaders.setRenderStage(RenderStage.VOID);
                }

                matrixStackIn.push();
                matrixStackIn.translate(0.0D, 12.0D, 0.0D);
                this.sky2VBO.bindBuffer();
                this.skyVertexFormat.setupBufferState(0L);
                this.sky2VBO.draw(matrixStackIn.getLast().getMatrix(), 7);
                VertexBuffer.unbindBuffer();
                this.skyVertexFormat.clearBufferState();
                matrixStackIn.pop();
                flag1 = true;
            }

            if (this.world.func_239132_a_().func_239216_b_())
            {
                RenderSystem.color3f(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
            }
            else
            {
                RenderSystem.color3f(f, f1, f2);
            }

            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
            RenderSystem.disableFog();
        }
    }

    public void renderClouds(MatrixStack matrixStackIn, float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ)
    {
        if (!Config.isCloudsOff())
        {
            if (Reflector.ForgeDimensionRenderInfo_getCloudRenderHandler.exists())
            {
                ICloudRenderHandler icloudrenderhandler = (ICloudRenderHandler)Reflector.call(this.world.func_239132_a_(), Reflector.ForgeDimensionRenderInfo_getCloudRenderHandler);

                if (icloudrenderhandler != null)
                {
                    icloudrenderhandler.render(this.ticks, partialTicks, matrixStackIn, this.world, this.mc, viewEntityX, viewEntityY, viewEntityZ);
                    return;
                }
            }

            float f5 = this.world.func_239132_a_().func_239213_a_();

            if (!Float.isNaN(f5))
            {
                if (Config.isShaders())
                {
                    Shaders.beginClouds();
                }

                RenderSystem.disableCull();
                RenderSystem.enableBlend();
                RenderSystem.enableAlphaTest();
                RenderSystem.enableDepthTest();
                RenderSystem.defaultAlphaFunc();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.enableFog();
                RenderSystem.depthMask(true);
                float f = 12.0F;
                float f1 = 4.0F;
                double d0 = 2.0E-4D;
                double d1 = (double)(((float)this.ticks + partialTicks) * 0.03F);
                double d2 = (viewEntityX + d1) / 12.0D;
                double d3 = (double)(f5 - (float)viewEntityY + 0.33F);
                d3 = d3 + this.mc.gameSettings.ofCloudsHeight * 128.0D;
                double d4 = viewEntityZ / 12.0D + (double)0.33F;
                d2 = d2 - (double)(MathHelper.floor(d2 / 2048.0D) * 2048);
                d4 = d4 - (double)(MathHelper.floor(d4 / 2048.0D) * 2048);
                float f2 = (float)(d2 - (double)MathHelper.floor(d2));
                float f3 = (float)(d3 / 4.0D - (double)MathHelper.floor(d3 / 4.0D)) * 4.0F;
                float f4 = (float)(d4 - (double)MathHelper.floor(d4));
                Vector3d vector3d = this.world.getCloudColor(partialTicks);
                int i = (int)Math.floor(d2);
                int j = (int)Math.floor(d3 / 4.0D);
                int k = (int)Math.floor(d4);

                if (i != this.cloudsCheckX || j != this.cloudsCheckY || k != this.cloudsCheckZ || this.mc.gameSettings.getCloudOption() != this.cloudOption || this.cloudsCheckColor.squareDistanceTo(vector3d) > 2.0E-4D)
                {
                    this.cloudsCheckX = i;
                    this.cloudsCheckY = j;
                    this.cloudsCheckZ = k;
                    this.cloudsCheckColor = vector3d;
                    this.cloudOption = this.mc.gameSettings.getCloudOption();
                    this.cloudsNeedUpdate = true;
                }

                if (this.cloudsNeedUpdate)
                {
                    this.cloudsNeedUpdate = false;
                    BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

                    if (this.cloudsVBO != null)
                    {
                        this.cloudsVBO.close();
                    }

                    this.cloudsVBO = new VertexBuffer(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    this.drawClouds(bufferbuilder, d2, d3, d4, vector3d);
                    bufferbuilder.finishDrawing();
                    this.cloudsVBO.upload(bufferbuilder);
                }

                this.textureManager.bindTexture(CLOUDS_TEXTURES);
                matrixStackIn.push();
                matrixStackIn.scale(12.0F, 1.0F, 12.0F);
                matrixStackIn.translate((double)(-f2), (double)f3, (double)(-f4));

                if (this.cloudsVBO != null)
                {
                    this.cloudsVBO.bindBuffer();
                    DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL.setupBufferState(0L);
                    int i1 = this.cloudOption == CloudOption.FANCY ? 0 : 1;

                    for (int l = i1; l < 2; ++l)
                    {
                        if (l == 0)
                        {
                            RenderSystem.colorMask(false, false, false, false);
                        }
                        else
                        {
                            RenderSystem.colorMask(true, true, true, true);
                        }

                        this.cloudsVBO.draw(matrixStackIn.getLast().getMatrix(), 7);
                    }

                    VertexBuffer.unbindBuffer();
                    DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL.clearBufferState();
                }

                matrixStackIn.pop();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.disableAlphaTest();
                RenderSystem.enableCull();
                RenderSystem.disableBlend();
                RenderSystem.disableFog();

                if (Config.isShaders())
                {
                    Shaders.endClouds();
                }
            }
        }
    }

    private void drawClouds(BufferBuilder bufferIn, double cloudsX, double cloudsY, double cloudsZ, Vector3d cloudsColor)
    {
        float f = 4.0F;
        float f1 = 0.00390625F;
        int i = 8;
        int j = 4;
        float f2 = 9.765625E-4F;
        float f3 = (float)MathHelper.floor(cloudsX) * 0.00390625F;
        float f4 = (float)MathHelper.floor(cloudsZ) * 0.00390625F;
        float f5 = (float)cloudsColor.x;
        float f6 = (float)cloudsColor.y;
        float f7 = (float)cloudsColor.z;
        float f8 = f5 * 0.9F;
        float f9 = f6 * 0.9F;
        float f10 = f7 * 0.9F;
        float f11 = f5 * 0.7F;
        float f12 = f6 * 0.7F;
        float f13 = f7 * 0.7F;
        float f14 = f5 * 0.8F;
        float f15 = f6 * 0.8F;
        float f16 = f7 * 0.8F;
        bufferIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        float f17 = (float)Math.floor(cloudsY / 4.0D) * 4.0F;

        if (Config.isCloudsFancy())
        {
            for (int k = -3; k <= 4; ++k)
            {
                for (int l = -3; l <= 4; ++l)
                {
                    float f18 = (float)(k * 8);
                    float f19 = (float)(l * 8);

                    if (f17 > -5.0F)
                    {
                        bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (f17 <= 5.0F)
                    {
                        bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    if (k > -1)
                    {
                        for (int i1 = 0; i1 < 8; ++i1)
                        {
                            bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).tex((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).tex((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((f18 + (float)i1 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (k <= 1)
                    {
                        for (int j2 = 0; j2 < 8; ++j2)
                        {
                            bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).tex((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 8.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).tex((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((f18 + (float)j2 + 0.5F) * 0.00390625F + f3, (f19 + 0.0F) * 0.00390625F + f4).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (l > -1)
                    {
                        for (int k2 = 0; k2 < 8; ++k2)
                        {
                            bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)k2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (l <= 1)
                    {
                        for (int l2 = 0; l2 < 8; ++l2)
                        {
                            bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((f18 + 8.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((f18 + 0.0F) * 0.00390625F + f3, (f19 + (float)l2 + 0.5F) * 0.00390625F + f4).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }
                }
            }
        }
        else
        {
            int j1 = 1;
            int k1 = 32;

            for (int l1 = -32; l1 < 32; l1 += 32)
            {
                for (int i2 = -32; i2 < 32; i2 += 32)
                {
                    bufferIn.pos((double)(l1 + 0), (double)f17, (double)(i2 + 32)).tex((float)(l1 + 0) * 0.00390625F + f3, (float)(i2 + 32) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    bufferIn.pos((double)(l1 + 32), (double)f17, (double)(i2 + 32)).tex((float)(l1 + 32) * 0.00390625F + f3, (float)(i2 + 32) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    bufferIn.pos((double)(l1 + 32), (double)f17, (double)(i2 + 0)).tex((float)(l1 + 32) * 0.00390625F + f3, (float)(i2 + 0) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    bufferIn.pos((double)(l1 + 0), (double)f17, (double)(i2 + 0)).tex((float)(l1 + 0) * 0.00390625F + f3, (float)(i2 + 0) * 0.00390625F + f4).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                }
            }
        }
    }

    public void updateChunks(long finishTimeNano)
    {
        finishTimeNano = (long)((double)finishTimeNano + 1.0E8D);
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads();
        long i = Util.nanoTime();
        int j = 0;

        if (this.chunksToUpdateForced.size() > 0)
        {
            Iterator iterator = this.chunksToUpdateForced.iterator();

            while (iterator.hasNext())
            {
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = (ChunkRenderDispatcher.ChunkRender)iterator.next();

                if (!this.renderDispatcher.updateChunkLater(chunkrenderdispatcher$chunkrender))
                {
                    break;
                }

                chunkrenderdispatcher$chunkrender.clearNeedsUpdate();
                iterator.remove();
                this.chunksToUpdate.remove(chunkrenderdispatcher$chunkrender);
                this.chunksToResortTransparency.remove(chunkrenderdispatcher$chunkrender);
            }
        }

        if (this.chunksToResortTransparency.size() > 0)
        {
            Iterator iterator2 = this.chunksToResortTransparency.iterator();

            if (iterator2.hasNext())
            {
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender2 = (ChunkRenderDispatcher.ChunkRender)iterator2.next();

                if (this.renderDispatcher.updateTransparencyLater(chunkrenderdispatcher$chunkrender2))
                {
                    iterator2.remove();
                }
            }
        }

        double d1 = 0.0D;
        int k = Config.getUpdatesPerFrame();

        if (!this.chunksToUpdate.isEmpty())
        {
            Iterator<ChunkRenderDispatcher.ChunkRender> iterator1 = this.chunksToUpdate.iterator();

            while (iterator1.hasNext())
            {
                ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender1 = iterator1.next();
                boolean flag1 = chunkrenderdispatcher$chunkrender1.isChunkRegionEmpty();
                boolean flag;

                if (!chunkrenderdispatcher$chunkrender1.needsImmediateUpdate() && !flag1)
                {
                    flag = this.renderDispatcher.updateChunkLater(chunkrenderdispatcher$chunkrender1);
                }
                else
                {
                    flag = this.renderDispatcher.updateChunkNow(chunkrenderdispatcher$chunkrender1);
                }

                if (!flag)
                {
                    break;
                }

                chunkrenderdispatcher$chunkrender1.clearNeedsUpdate();
                iterator1.remove();

                if (!flag1)
                {
                    double d0 = 2.0D * RenderChunkUtils.getRelativeBufferSize(chunkrenderdispatcher$chunkrender1);
                    d1 += d0;

                    if (d1 > (double)k)
                    {
                        break;
                    }
                }
            }
        }
    }

    private void renderWorldBorder(ActiveRenderInfo activeRenderInfoIn)
    {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        WorldBorder worldborder = this.world.getWorldBorder();
        double d0 = (double)(this.mc.gameSettings.renderDistanceChunks * 16);

        if (!(activeRenderInfoIn.getProjectedView().x < worldborder.maxX() - d0) || !(activeRenderInfoIn.getProjectedView().x > worldborder.minX() + d0) || !(activeRenderInfoIn.getProjectedView().z < worldborder.maxZ() - d0) || !(activeRenderInfoIn.getProjectedView().z > worldborder.minZ() + d0))
        {
            if (Config.isShaders())
            {
                Shaders.pushProgram();
                Shaders.useProgram(Shaders.ProgramTexturedLit);
                Shaders.setRenderStage(RenderStage.WORLD_BORDER);
            }

            double d1 = 1.0D - worldborder.getClosestDistance(activeRenderInfoIn.getProjectedView().x, activeRenderInfoIn.getProjectedView().z) / d0;
            d1 = Math.pow(d1, 4.0D);
            double d2 = activeRenderInfoIn.getProjectedView().x;
            double d3 = activeRenderInfoIn.getProjectedView().y;
            double d4 = activeRenderInfoIn.getProjectedView().z;
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            this.textureManager.bindTexture(FORCEFIELD_TEXTURES);
            RenderSystem.depthMask(Minecraft.isFabulousGraphicsEnabled());
            RenderSystem.pushMatrix();
            int i = worldborder.getStatus().getColor();
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            RenderSystem.color4f(f, f1, f2, (float)d1);
            RenderSystem.polygonOffset(-3.0F, -3.0F);
            RenderSystem.enablePolygonOffset();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.enableAlphaTest();
            RenderSystem.disableCull();
            float f3 = (float)(Util.milliTime() % 3000L) / 3000.0F;
            float f4 = 0.0F;
            float f5 = 0.0F;
            float f6 = 128.0F;
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            double d5 = Math.max((double)MathHelper.floor(d4 - d0), worldborder.minZ());
            double d6 = Math.min((double)MathHelper.ceil(d4 + d0), worldborder.maxZ());

            if (d2 > worldborder.maxX() - d0)
            {
                float f7 = 0.0F;

                for (double d7 = d5; d7 < d6; f7 += 0.5F)
                {
                    double d8 = Math.min(1.0D, d6 - d7);
                    float f8 = (float)d8 * 0.5F;
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.maxX(), 256, d7, f3 + f7, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.maxX(), 256, d7 + d8, f3 + f8 + f7, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.maxX(), 0, d7 + d8, f3 + f8 + f7, f3 + 128.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.maxX(), 0, d7, f3 + f7, f3 + 128.0F);
                    ++d7;
                }
            }

            if (d2 < worldborder.minX() + d0)
            {
                float f9 = 0.0F;

                for (double d9 = d5; d9 < d6; f9 += 0.5F)
                {
                    double d12 = Math.min(1.0D, d6 - d9);
                    float f12 = (float)d12 * 0.5F;
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.minX(), 256, d9, f3 + f9, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.minX(), 256, d9 + d12, f3 + f12 + f9, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.minX(), 0, d9 + d12, f3 + f12 + f9, f3 + 128.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, worldborder.minX(), 0, d9, f3 + f9, f3 + 128.0F);
                    ++d9;
                }
            }

            d5 = Math.max((double)MathHelper.floor(d2 - d0), worldborder.minX());
            d6 = Math.min((double)MathHelper.ceil(d2 + d0), worldborder.maxX());

            if (d4 > worldborder.maxZ() - d0)
            {
                float f10 = 0.0F;

                for (double d10 = d5; d10 < d6; f10 += 0.5F)
                {
                    double d13 = Math.min(1.0D, d6 - d10);
                    float f13 = (float)d13 * 0.5F;
                    this.addVertex(bufferbuilder, d2, d3, d4, d10, 256, worldborder.maxZ(), f3 + f10, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, d10 + d13, 256, worldborder.maxZ(), f3 + f13 + f10, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, d10 + d13, 0, worldborder.maxZ(), f3 + f13 + f10, f3 + 128.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, d10, 0, worldborder.maxZ(), f3 + f10, f3 + 128.0F);
                    ++d10;
                }
            }

            if (d4 < worldborder.minZ() + d0)
            {
                float f11 = 0.0F;

                for (double d11 = d5; d11 < d6; f11 += 0.5F)
                {
                    double d14 = Math.min(1.0D, d6 - d11);
                    float f14 = (float)d14 * 0.5F;
                    this.addVertex(bufferbuilder, d2, d3, d4, d11, 256, worldborder.minZ(), f3 + f11, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, d11 + d14, 256, worldborder.minZ(), f3 + f14 + f11, f3 + 0.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, d11 + d14, 0, worldborder.minZ(), f3 + f14 + f11, f3 + 128.0F);
                    this.addVertex(bufferbuilder, d2, d3, d4, d11, 0, worldborder.minZ(), f3 + f11, f3 + 128.0F);
                    ++d11;
                }
            }

            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            RenderSystem.enableCull();
            RenderSystem.disableAlphaTest();
            RenderSystem.polygonOffset(0.0F, 0.0F);
            RenderSystem.disablePolygonOffset();
            RenderSystem.enableAlphaTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
            RenderSystem.depthMask(true);

            if (Config.isShaders())
            {
                Shaders.popProgram();
                Shaders.setRenderStage(RenderStage.NONE);
            }
        }
    }

    private void addVertex(BufferBuilder bufferIn, double camX, double camY, double camZ, double xIn, int yIn, double zIn, float texU, float texV)
    {
        bufferIn.pos(xIn - camX, (double)yIn - camY, zIn - camZ).tex(texU, texV).endVertex();
    }

    private void drawSelectionBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, double xIn, double yIn, double zIn, BlockPos blockPosIn, BlockState blockStateIn)
    {
        if (!Config.isCustomEntityModels() || !CustomEntityModels.isCustomModel(blockStateIn))
        {
            drawShape(matrixStackIn, bufferIn, blockStateIn.getShape(this.world, blockPosIn, ISelectionContext.forEntity(entityIn)), (double)blockPosIn.getX() - xIn, (double)blockPosIn.getY() - yIn, (double)blockPosIn.getZ() - zIn, 0.0F, 0.0F, 0.0F, 0.4F);
        }
    }

    public static void drawVoxelShapeParts(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha)
    {
        List<AxisAlignedBB> list = shapeIn.toBoundingBoxList();
        int i = MathHelper.ceil((double)list.size() / 3.0D);

        for (int j = 0; j < list.size(); ++j)
        {
            AxisAlignedBB axisalignedbb = list.get(j);
            float f = ((float)j % (float)i + 1.0F) / (float)i;
            float f1 = (float)(j / i);
            float f2 = f * (float)(f1 == 0.0F ? 1 : 0);
            float f3 = f * (float)(f1 == 1.0F ? 1 : 0);
            float f4 = f * (float)(f1 == 2.0F ? 1 : 0);
            drawShape(matrixStackIn, bufferIn, VoxelShapes.create(axisalignedbb.offset(0.0D, 0.0D, 0.0D)), xIn, yIn, zIn, f2, f3, f4, 1.0F);
        }
    }

    private static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha)
    {
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        shapeIn.forEachEdge((p_lambda$drawShape$6_12_, p_lambda$drawShape$6_14_, p_lambda$drawShape$6_16_, p_lambda$drawShape$6_18_, p_lambda$drawShape$6_20_, p_lambda$drawShape$6_22_) ->
        {
            bufferIn.pos(matrix4f, (float)(p_lambda$drawShape$6_12_ + xIn), (float)(p_lambda$drawShape$6_14_ + yIn), (float)(p_lambda$drawShape$6_16_ + zIn)).color(red, green, blue, alpha).endVertex();
            bufferIn.pos(matrix4f, (float)(p_lambda$drawShape$6_18_ + xIn), (float)(p_lambda$drawShape$6_20_ + yIn), (float)(p_lambda$drawShape$6_22_ + zIn)).color(red, green, blue, alpha).endVertex();
        });
    }

    public static void drawBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, AxisAlignedBB aabbIn, float red, float green, float blue, float alpha)
    {
        drawBoundingBox(matrixStackIn, bufferIn, aabbIn.minX, aabbIn.minY, aabbIn.minZ, aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ, red, green, blue, alpha, red, green, blue);
    }

    public static void drawBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        drawBoundingBox(matrixStackIn, bufferIn, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha, red, green, blue);
    }

    public static void drawBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float red2, float green2, float blue2)
    {
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        float f = (float)minX;
        float f1 = (float)minY;
        float f2 = (float)minZ;
        float f3 = (float)maxX;
        float f4 = (float)maxY;
        float f5 = (float)maxZ;
        bufferIn.pos(matrix4f, f, f1, f2).color(red, green2, blue2, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f1, f2).color(red, green2, blue2, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f1, f2).color(red2, green, blue2, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f2).color(red2, green, blue2, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f1, f2).color(red2, green2, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f1, f5).color(red2, green2, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f1, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f4, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f4, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f1, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f1, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f1, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f1, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f1, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f4, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f1, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f4, f5).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f4, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f4, f5).color(red, green, blue, alpha).endVertex();
    }

    public static void drawBoundingBox2(MatrixStack matrixStackIn, IVertexBuilder bufferIn, AxisAlignedBB aabbIn, float red, float green, float blue, float alpha)
    {
        drawBoundingBox2(matrixStackIn, bufferIn, aabbIn.minX, aabbIn.minY, aabbIn.minZ, aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ, red, green, blue, alpha, red, green, blue);
    }

    public static void drawBoundingBox2(MatrixStack matrixStackIn, IVertexBuilder bufferIn, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float red2, float green2, float blue2)
    {
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        float f = (float)minX;
        float f1 = (float)minY;
        float f2 = (float)minZ;
        float f3 = (float)maxX;
        float f4 = (float)maxY;
        float f5 = (float)maxZ;

        bufferIn.pos(matrix4f, f, f1, f2).color(red, green2, blue2, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f1, f2).color(red, green2, blue2, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f1, f2).color(red2, green, blue2, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f2).color(red2, green, blue2, alpha).endVertex();

        bufferIn.pos(matrix4f, f3, f1, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f3, f4, f2).color(red, green, blue, alpha).endVertex();

        bufferIn.pos(matrix4f, f3, f4, f2).color(red, green, blue, alpha).endVertex();
        bufferIn.pos(matrix4f, f, f4, f2).color(red, green, blue, alpha).endVertex();
    }

    public static void addChainedFilledBoxVertices(BufferBuilder builder, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha)
    {
        builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
    }

    public void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, BlockState oldState, BlockState newState, int flags)
    {
        this.notifyBlockUpdate(pos, (flags & 8) != 0);
    }

    private void notifyBlockUpdate(BlockPos posIn, boolean rerenderOnMainThread)
    {
        for (int i = posIn.getZ() - 1; i <= posIn.getZ() + 1; ++i)
        {
            for (int j = posIn.getX() - 1; j <= posIn.getX() + 1; ++j)
            {
                for (int k = posIn.getY() - 1; k <= posIn.getY() + 1; ++k)
                {
                    this.markForRerender(j >> 4, k >> 4, i >> 4, rerenderOnMainThread);
                }
            }
        }
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
     */
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        for (int i = z1 - 1; i <= z2 + 1; ++i)
        {
            for (int j = x1 - 1; j <= x2 + 1; ++j)
            {
                for (int k = y1 - 1; k <= y2 + 1; ++k)
                {
                    this.markForRerender(j >> 4, k >> 4, i >> 4);
                }
            }
        }
    }

    public void markBlockRangeForRenderUpdate(BlockPos blockPosIn, BlockState oldState, BlockState newState)
    {
        if (this.mc.getModelManager().needsRenderUpdate(oldState, newState))
        {
            this.markBlockRangeForRenderUpdate(blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ(), blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
        }
    }

    public void markSurroundingsForRerender(int sectionX, int sectionY, int sectionZ)
    {
        for (int i = sectionZ - 1; i <= sectionZ + 1; ++i)
        {
            for (int j = sectionX - 1; j <= sectionX + 1; ++j)
            {
                for (int k = sectionY - 1; k <= sectionY + 1; ++k)
                {
                    this.markForRerender(j, k, i);
                }
            }
        }
    }

    public void markForRerender(int sectionX, int sectionY, int sectionZ)
    {
        this.markForRerender(sectionX, sectionY, sectionZ, false);
    }

    private void markForRerender(int sectionX, int sectionY, int sectionZ, boolean rerenderOnMainThread)
    {
        this.viewFrustum.markForRerender(sectionX, sectionY, sectionZ, rerenderOnMainThread);
    }

    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos)
    {
        this.playRecord(soundIn, pos, soundIn == null ? null : MusicDiscItem.getBySound(soundIn));
    }

    public void playRecord(@Nullable SoundEvent p_playRecord_1_, BlockPos p_playRecord_2_, @Nullable MusicDiscItem p_playRecord_3_)
    {
        ISound isound = this.mapSoundPositions.get(p_playRecord_2_);

        if (isound != null)
        {
            this.mc.getSoundHandler().stop(isound);
            this.mapSoundPositions.remove(p_playRecord_2_);
        }

        if (p_playRecord_1_ != null)
        {
            MusicDiscItem musicdiscitem = MusicDiscItem.getBySound(p_playRecord_1_);

            if (Reflector.MinecraftForgeClient.exists())
            {
                musicdiscitem = p_playRecord_3_;
            }

            if (musicdiscitem != null)
            {
                this.mc.ingameGUI.func_238451_a_(musicdiscitem.getDescription());
            }

            ISound isound1 = SimpleSound.record(p_playRecord_1_, (double)p_playRecord_2_.getX(), (double)p_playRecord_2_.getY(), (double)p_playRecord_2_.getZ());
            this.mapSoundPositions.put(p_playRecord_2_, isound1);
            this.mc.getSoundHandler().play(isound1);
        }

        this.setPartying(this.world, p_playRecord_2_, p_playRecord_1_ != null);
    }

    /**
     * Called when a record starts or stops playing. Used to make parrots start or stop partying.
     */
    private void setPartying(World worldIn, BlockPos pos, boolean isPartying)
    {
        for (LivingEntity livingentity : worldIn.getEntitiesWithinAABB(LivingEntity.class, (new AxisAlignedBB(pos)).grow(3.0D)))
        {
            livingentity.setPartying(pos, isPartying);
        }
    }

    public void addParticle(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        this.addParticle(particleData, alwaysRender, false, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        try
        {
            this.addParticleUnchecked(particleData, ignoreRange, minimizeLevel, x, y, z, xSpeed, ySpeed, zSpeed);
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
            crashreportcategory.addDetail("ID", Registry.PARTICLE_TYPE.getKey(particleData.getType()));
            crashreportcategory.addDetail("Parameters", particleData.getParameters());
            crashreportcategory.addDetail("Position", () ->
            {
                return CrashReportCategory.getCoordinateInfo(x, y, z);
            });
            throw new ReportedException(crashreport);
        }
    }

    private <T extends IParticleData> void addParticleUnchecked(T particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        this.addParticle(particleData, particleData.getType().getAlwaysShow(), x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Nullable
    private Particle addParticleUnchecked(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        return this.addParticleUnchecked(particleData, alwaysRender, false, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Nullable
    private Particle addParticleUnchecked(IParticleData particleData, boolean alwaysRender, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
    {
        ActiveRenderInfo activerenderinfo = this.mc.gameRenderer.getActiveRenderInfo();

        if (this.mc != null && activerenderinfo.isValid() && this.mc.particles != null)
        {
            ParticleStatus particlestatus = this.calculateParticleLevel(minimizeLevel);

            if (particleData == ParticleTypes.EXPLOSION_EMITTER && !Config.isAnimatedExplosion())
            {
                return null;
            }
            else if (particleData == ParticleTypes.EXPLOSION && !Config.isAnimatedExplosion())
            {
                return null;
            }
            else if (particleData == ParticleTypes.POOF && !Config.isAnimatedExplosion())
            {
                return null;
            }
            else if (particleData == ParticleTypes.UNDERWATER && !Config.isWaterParticles())
            {
                return null;
            }
            else if (particleData == ParticleTypes.SMOKE && !Config.isAnimatedSmoke())
            {
                return null;
            }
            else if (particleData == ParticleTypes.LARGE_SMOKE && !Config.isAnimatedSmoke())
            {
                return null;
            }
            else if (particleData == ParticleTypes.ENTITY_EFFECT && !Config.isPotionParticles())
            {
                return null;
            }
            else if (particleData == ParticleTypes.AMBIENT_ENTITY_EFFECT && !Config.isPotionParticles())
            {
                return null;
            }
            else if (particleData == ParticleTypes.EFFECT && !Config.isPotionParticles())
            {
                return null;
            }
            else if (particleData == ParticleTypes.INSTANT_EFFECT && !Config.isPotionParticles())
            {
                return null;
            }
            else if (particleData == ParticleTypes.WITCH && !Config.isPotionParticles())
            {
                return null;
            }
            else if (particleData == ParticleTypes.PORTAL && !Config.isPortalParticles())
            {
                return null;
            }
            else if (particleData == ParticleTypes.FLAME && !Config.isAnimatedFlame())
            {
                return null;
            }
            else if (particleData == ParticleTypes.SOUL_FIRE_FLAME && !Config.isAnimatedFlame())
            {
                return null;
            }
            else if (particleData == ParticleTypes.DUST && !Config.isAnimatedRedstone())
            {
                return null;
            }
            else if (particleData == ParticleTypes.DRIPPING_WATER && !Config.isDrippingWaterLava())
            {
                return null;
            }
            else if (particleData == ParticleTypes.DRIPPING_LAVA && !Config.isDrippingWaterLava())
            {
                return null;
            }
            else if (particleData == ParticleTypes.FIREWORK && !Config.isFireworkParticles())
            {
                return null;
            }
            else
            {
                if (!alwaysRender)
                {
                    double d0 = 1024.0D;

                    if (particleData == ParticleTypes.CRIT)
                    {
                        d0 = 38416.0D;
                    }

                    if (activerenderinfo.getProjectedView().squareDistanceTo(x, y, z) > d0)
                    {
                        return null;
                    }

                    if (particlestatus == ParticleStatus.MINIMAL)
                    {
                        return null;
                    }
                }

                Particle particle = this.mc.particles.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);

                if (particleData == ParticleTypes.BUBBLE)
                {
                    CustomColors.updateWaterFX(particle, this.world, x, y, z, this.renderEnv);
                }

                if (particleData == ParticleTypes.SPLASH)
                {
                    CustomColors.updateWaterFX(particle, this.world, x, y, z, this.renderEnv);
                }

                if (particleData == ParticleTypes.RAIN)
                {
                    CustomColors.updateWaterFX(particle, this.world, x, y, z, this.renderEnv);
                }

                if (particleData == ParticleTypes.MYCELIUM)
                {
                    CustomColors.updateMyceliumFX(particle);
                }

                if (particleData == ParticleTypes.PORTAL)
                {
                    CustomColors.updatePortalFX(particle);
                }

                if (particleData == ParticleTypes.DUST)
                {
                    CustomColors.updateReddustFX(particle, this.world, x, y, z);
                }

                return particle;
            }
        }
        else
        {
            return null;
        }
    }

    private ParticleStatus calculateParticleLevel(boolean minimiseLevel)
    {
        ParticleStatus particlestatus = this.mc.gameSettings.particles;

        if (minimiseLevel && particlestatus == ParticleStatus.MINIMAL && this.world.rand.nextInt(10) == 0)
        {
            particlestatus = ParticleStatus.DECREASED;
        }

        if (particlestatus == ParticleStatus.DECREASED && this.world.rand.nextInt(3) == 0)
        {
            particlestatus = ParticleStatus.MINIMAL;
        }

        return particlestatus;
    }

    /**
     * Deletes all display lists
     */
    public void deleteAllDisplayLists()
    {
    }

    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        switch (soundID)
        {
            case 1023:
            case 1028:
            case 1038:
                ActiveRenderInfo activerenderinfo = this.mc.gameRenderer.getActiveRenderInfo();

                if (activerenderinfo.isValid())
                {
                    double d0 = (double)pos.getX() - activerenderinfo.getProjectedView().x;
                    double d1 = (double)pos.getY() - activerenderinfo.getProjectedView().y;
                    double d2 = (double)pos.getZ() - activerenderinfo.getProjectedView().z;
                    double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    double d4 = activerenderinfo.getProjectedView().x;
                    double d5 = activerenderinfo.getProjectedView().y;
                    double d6 = activerenderinfo.getProjectedView().z;

                    if (d3 > 0.0D)
                    {
                        d4 += d0 / d3 * 2.0D;
                        d5 += d1 / d3 * 2.0D;
                        d6 += d2 / d3 * 2.0D;
                    }

                    if (soundID == 1023)
                    {
                        this.world.playSound(d4, d5, d6, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    }
                    else if (soundID == 1038)
                    {
                        this.world.playSound(d4, d5, d6, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
                    }
                    else
                    {
                        this.world.playSound(d4, d5, d6, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
                    }
                }

            default:
        }
    }

    public void playEvent(PlayerEntity player, int type, BlockPos blockPosIn, int data)
    {
        Random random = this.world.rand;

        switch (type)
        {
            case 1000:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1001:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;

            case 1002:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
                break;

            case 1003:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;

            case 1004:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
                break;

            case 1005:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1006:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1007:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1008:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1009:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
                break;

            case 1010:
                if (Item.getItemById(data) instanceof MusicDiscItem)
                {
                    if (Reflector.MinecraftForgeClient.exists())
                    {
                        this.playRecord(((MusicDiscItem)Item.getItemById(data)).getSound(), blockPosIn, (MusicDiscItem)Item.getItemById(data));
                    }
                    else
                    {
                        this.playRecord(((MusicDiscItem)Item.getItemById(data)).getSound(), blockPosIn);
                    }
                }
                else
                {
                    this.playRecord((SoundEvent)null, blockPosIn);
                }

                break;

            case 1011:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1012:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1013:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1014:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1015:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1016:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1017:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1018:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1019:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1020:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1021:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1022:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1024:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1025:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1026:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1027:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1029:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1030:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1031:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1032:
                this.mc.getSoundHandler().play(SimpleSound.ambientWithoutAttenuation(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F, 0.25F));
                break;

            case 1033:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1034:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1035:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                break;

            case 1036:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1037:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1039:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1040:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1041:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1042:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1043:
                this.world.playSound(blockPosIn, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1044:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1500:
                ComposterBlock.playEvent(this.world, blockPosIn, data > 0);
                break;

            case 1501:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);

                for (int l1 = 0; l1 < 8; ++l1)
                {
                    this.world.addParticle(ParticleTypes.LARGE_SMOKE, (double)blockPosIn.getX() + random.nextDouble(), (double)blockPosIn.getY() + 1.2D, (double)blockPosIn.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                break;

            case 1502:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);

                for (int k1 = 0; k1 < 5; ++k1)
                {
                    double d14 = (double)blockPosIn.getX() + random.nextDouble() * 0.6D + 0.2D;
                    double d16 = (double)blockPosIn.getY() + random.nextDouble() * 0.6D + 0.2D;
                    double d17 = (double)blockPosIn.getZ() + random.nextDouble() * 0.6D + 0.2D;
                    this.world.addParticle(ParticleTypes.SMOKE, d14, d16, d17, 0.0D, 0.0D, 0.0D);
                }

                break;

            case 1503:
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

                for (int j1 = 0; j1 < 16; ++j1)
                {
                    double d13 = (double)blockPosIn.getX() + (5.0D + random.nextDouble() * 6.0D) / 16.0D;
                    double d15 = (double)blockPosIn.getY() + 0.8125D;
                    double d1 = (double)blockPosIn.getZ() + (5.0D + random.nextDouble() * 6.0D) / 16.0D;
                    this.world.addParticle(ParticleTypes.SMOKE, d13, d15, d1, 0.0D, 0.0D, 0.0D);
                }

                break;

            case 2000:
                Direction direction = Direction.byIndex(data);
                int i = direction.getXOffset();
                int j = direction.getYOffset();
                int k = direction.getZOffset();
                double d0 = (double)blockPosIn.getX() + (double)i * 0.6D + 0.5D;
                double d2 = (double)blockPosIn.getY() + (double)j * 0.6D + 0.5D;
                double d3 = (double)blockPosIn.getZ() + (double)k * 0.6D + 0.5D;

                for (int i2 = 0; i2 < 10; ++i2)
                {
                    double d18 = random.nextDouble() * 0.2D + 0.01D;
                    double d19 = d0 + (double)i * 0.01D + (random.nextDouble() - 0.5D) * (double)k * 0.5D;
                    double d20 = d2 + (double)j * 0.01D + (random.nextDouble() - 0.5D) * (double)j * 0.5D;
                    double d21 = d3 + (double)k * 0.01D + (random.nextDouble() - 0.5D) * (double)i * 0.5D;
                    double d22 = (double)i * d18 + random.nextGaussian() * 0.01D;
                    double d23 = (double)j * d18 + random.nextGaussian() * 0.01D;
                    double d27 = (double)k * d18 + random.nextGaussian() * 0.01D;
                    this.addParticleUnchecked(ParticleTypes.SMOKE, d19, d20, d21, d22, d23, d27);
                }

                break;

            case 2001:
                BlockState blockstate = Block.getStateById(data);

                if (!ReflectorForge.isAir(blockstate, this.world, blockPosIn))
                {
                    SoundType soundtype = blockstate.getSoundType();

                    if (Reflector.IForgeBlockState_getSoundType3.exists())
                    {
                        soundtype = (SoundType)Reflector.call(blockstate, Reflector.IForgeBlockState_getSoundType3, this.world, blockPosIn, null);
                    }

                    this.world.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
                }

                this.mc.particles.addBlockDestroyEffects(blockPosIn, blockstate);
                break;

            case 2002:
            case 2007:
                Vector3d vector3d = Vector3d.copyCenteredHorizontally(blockPosIn);

                for (int l = 0; l < 8; ++l)
                {
                    this.addParticleUnchecked(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), vector3d.x, vector3d.y, vector3d.z, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
                }

                float f5 = (float)(data >> 16 & 255) / 255.0F;
                float f = (float)(data >> 8 & 255) / 255.0F;
                float f1 = (float)(data >> 0 & 255) / 255.0F;
                IParticleData iparticledata = type == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

                for (int j2 = 0; j2 < 100; ++j2)
                {
                    double d5 = random.nextDouble() * 4.0D;
                    double d7 = random.nextDouble() * Math.PI * 2.0D;
                    double d9 = Math.cos(d7) * d5;
                    double d26 = 0.01D + random.nextDouble() * 0.5D;
                    double d29 = Math.sin(d7) * d5;
                    Particle particle1 = this.addParticleUnchecked(iparticledata, iparticledata.getType().getAlwaysShow(), vector3d.x + d9 * 0.1D, vector3d.y + 0.3D, vector3d.z + d29 * 0.1D, d9, d26, d29);

                    if (particle1 != null)
                    {
                        float f4 = 0.75F + random.nextFloat() * 0.25F;
                        particle1.setColor(f5 * f4, f * f4, f1 * f4);
                        particle1.multiplyVelocity((float)d5);
                    }
                }

                this.world.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2003:
                double d4 = (double)blockPosIn.getX() + 0.5D;
                double d6 = (double)blockPosIn.getY();
                double d8 = (double)blockPosIn.getZ() + 0.5D;

                for (int i3 = 0; i3 < 8; ++i3)
                {
                    this.addParticleUnchecked(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), d4, d6, d8, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
                }

                for (double d24 = 0.0D; d24 < (Math.PI * 2D); d24 += 0.15707963267948966D)
                {
                    this.addParticleUnchecked(ParticleTypes.PORTAL, d4 + Math.cos(d24) * 5.0D, d6 - 0.4D, d8 + Math.sin(d24) * 5.0D, Math.cos(d24) * -5.0D, 0.0D, Math.sin(d24) * -5.0D);
                    this.addParticleUnchecked(ParticleTypes.PORTAL, d4 + Math.cos(d24) * 5.0D, d6 - 0.4D, d8 + Math.sin(d24) * 5.0D, Math.cos(d24) * -7.0D, 0.0D, Math.sin(d24) * -7.0D);
                }

                break;

            case 2004:
                for (int l2 = 0; l2 < 20; ++l2)
                {
                    double d25 = (double)blockPosIn.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
                    double d28 = (double)blockPosIn.getY() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
                    double d30 = (double)blockPosIn.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
                    this.world.addParticle(ParticleTypes.SMOKE, d25, d28, d30, 0.0D, 0.0D, 0.0D);
                    this.world.addParticle(ParticleTypes.FLAME, d25, d28, d30, 0.0D, 0.0D, 0.0D);
                }

                break;

            case 2005:
                BoneMealItem.spawnBonemealParticles(this.world, blockPosIn, data);
                break;

            case 2006:
                for (int k2 = 0; k2 < 200; ++k2)
                {
                    float f2 = random.nextFloat() * 4.0F;
                    float f3 = random.nextFloat() * ((float)Math.PI * 2F);
                    double d10 = (double)(MathHelper.cos(f3) * f2);
                    double d11 = 0.01D + random.nextDouble() * 0.5D;
                    double d12 = (double)(MathHelper.sin(f3) * f2);
                    Particle particle = this.addParticleUnchecked(ParticleTypes.DRAGON_BREATH, false, (double)blockPosIn.getX() + d10 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d12 * 0.1D, d10, d11, d12);

                    if (particle != null)
                    {
                        particle.multiplyVelocity(f2);
                    }
                }

                if (data == 1)
                {
                    this.world.playSound(blockPosIn, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, random.nextFloat() * 0.1F + 0.9F, false);
                }

                break;

            case 2008:
                this.world.addParticle(ParticleTypes.EXPLOSION, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                break;

            case 2009:
                for (int i1 = 0; i1 < 8; ++i1)
                {
                    this.world.addParticle(ParticleTypes.CLOUD, (double)blockPosIn.getX() + random.nextDouble(), (double)blockPosIn.getY() + 1.2D, (double)blockPosIn.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                break;

            case 3000:
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
                this.world.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
                break;

            case 3001:
                this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.rand.nextFloat() * 0.3F, false);
        }
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        if (progress >= 0 && progress < 10)
        {
            DestroyBlockProgress destroyblockprogress1 = this.damagedBlocks.get(breakerId);

            if (destroyblockprogress1 != null)
            {
                this.removeDamageProgress(destroyblockprogress1);
            }

            if (destroyblockprogress1 == null || destroyblockprogress1.getPosition().getX() != pos.getX() || destroyblockprogress1.getPosition().getY() != pos.getY() || destroyblockprogress1.getPosition().getZ() != pos.getZ())
            {
                destroyblockprogress1 = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(breakerId, destroyblockprogress1);
            }

            destroyblockprogress1.setPartialBlockDamage(progress);
            destroyblockprogress1.setCloudUpdateTick(this.ticks);
            this.damageProgress.computeIfAbsent(destroyblockprogress1.getPosition().toLong(), (p_lambda$sendBlockBreakProgress$8_0_) ->
            {
                return Sets.newTreeSet();
            }).add(destroyblockprogress1);
        }
        else
        {
            DestroyBlockProgress destroyblockprogress = this.damagedBlocks.remove(breakerId);

            if (destroyblockprogress != null)
            {
                this.removeDamageProgress(destroyblockprogress);
            }
        }
    }

    public boolean hasNoChunkUpdates()
    {
        return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasNoChunkUpdates();
    }

    public void setDisplayListEntitiesDirty()
    {
        this.displayListEntitiesDirty = true;
        this.cloudsNeedUpdate = true;
    }

    public int getCountRenderers()
    {
        return this.viewFrustum.renderChunks.length;
    }

    public int getCountActiveRenderers()
    {
        return this.renderInfos.size();
    }

    public int getCountEntitiesRendered()
    {
        return this.countEntitiesRendered;
    }

    public int getCountTileEntitiesRendered()
    {
        return this.countTileEntitiesRendered;
    }

    public int getCountLoadedChunks()
    {
        if (this.world == null)
        {
            return 0;
        }
        else
        {
            ClientChunkProvider clientchunkprovider = this.world.getChunkProvider();
            return clientchunkprovider == null ? 0 : clientchunkprovider.getLoadedChunksCount();
        }
    }

    public int getCountChunksToUpdate()
    {
        return this.chunksToUpdate.size();
    }

    public ChunkRenderDispatcher.ChunkRender getRenderChunk(BlockPos p_getRenderChunk_1_)
    {
        return this.viewFrustum.getRenderChunk(p_getRenderChunk_1_);
    }

    public ClientWorld getWorld()
    {
        return this.world;
    }

    private void clearRenderInfos()
    {
        if (renderEntitiesCounter > 0)
        {
            this.renderInfos = new ObjectArrayList<>(this.renderInfos.size() + 16);
            this.renderInfosEntities = new ArrayList<>(this.renderInfosEntities.size() + 16);
            this.renderInfosTileEntities = new ArrayList<>(this.renderInfosTileEntities.size() + 16);
        }
        else
        {
            this.renderInfos.clear();
            this.renderInfosEntities.clear();
            this.renderInfosTileEntities.clear();
        }
    }

    public void onPlayerPositionSet()
    {
        if (this.firstWorldLoad)
        {
            this.loadRenderers();
            this.firstWorldLoad = false;
        }
    }

    public void pauseChunkUpdates()
    {
        if (this.renderDispatcher != null)
        {
            this.renderDispatcher.pauseChunkUpdates();
        }
    }

    public void resumeChunkUpdates()
    {
        if (this.renderDispatcher != null)
        {
            this.renderDispatcher.resumeChunkUpdates();
        }
    }

    public int getFrameCount()
    {
        return this.frameId;
    }

    public int getNextFrameCount()
    {
        return ++this.frameId;
    }

    public RenderTypeBuffers getRenderTypeTextures()
    {
        return this.renderTypeTextures;
    }

    public List<WorldRenderer.LocalRenderInformationContainer> getRenderInfosEntities()
    {
        return this.renderInfosEntities;
    }

    public List<WorldRenderer.LocalRenderInformationContainer> getRenderInfosTileEntities()
    {
        return this.renderInfosTileEntities;
    }

    private void checkLoadVisibleChunks(ActiveRenderInfo p_checkLoadVisibleChunks_1_, ClippingHelper p_checkLoadVisibleChunks_2_, boolean p_checkLoadVisibleChunks_3_)
    {
        if (this.loadVisibleChunksCounter == 0)
        {
            this.loadAllVisibleChunks(p_checkLoadVisibleChunks_1_, p_checkLoadVisibleChunks_2_, p_checkLoadVisibleChunks_3_);
            this.mc.ingameGUI.getChatGUI().deleteChatLine(201435902);
        }

        if (this.loadVisibleChunksCounter > -1)
        {
            --this.loadVisibleChunksCounter;
        }
    }

    private void loadAllVisibleChunks(ActiveRenderInfo p_loadAllVisibleChunks_1_, ClippingHelper p_loadAllVisibleChunks_2_, boolean p_loadAllVisibleChunks_3_)
    {
        int i = this.mc.gameSettings.ofChunkUpdates;
        boolean flag = this.mc.gameSettings.ofLazyChunkLoading;

        try
        {
            this.mc.gameSettings.ofChunkUpdates = 1000;
            this.mc.gameSettings.ofLazyChunkLoading = false;
            WorldRenderer worldrenderer = Config.getRenderGlobal();
            int j = worldrenderer.getCountLoadedChunks();
            long k = System.currentTimeMillis();
            Config.dbg("Loading visible chunks");
            long l = System.currentTimeMillis() + 5000L;
            int i1 = 0;
            boolean flag1 = false;

            do
            {
                flag1 = false;

                for (int j1 = 0; j1 < 100; ++j1)
                {
                    worldrenderer.setDisplayListEntitiesDirty();
                    worldrenderer.setupTerrain(p_loadAllVisibleChunks_1_, p_loadAllVisibleChunks_2_, false, this.frameId++, p_loadAllVisibleChunks_3_);

                    if (!worldrenderer.hasNoChunkUpdates())
                    {
                        flag1 = true;
                    }

                    i1 = i1 + worldrenderer.getCountChunksToUpdate();

                    while (!worldrenderer.hasNoChunkUpdates())
                    {
                        worldrenderer.updateChunks(System.nanoTime() + 1000000000L);
                    }

                    i1 = i1 - worldrenderer.getCountChunksToUpdate();

                    if (!flag1)
                    {
                        break;
                    }
                }

                if (worldrenderer.getCountLoadedChunks() != j)
                {
                    flag1 = true;
                    j = worldrenderer.getCountLoadedChunks();
                }

                if (System.currentTimeMillis() > l)
                {
                    Config.log("Chunks loaded: " + i1);
                    l = System.currentTimeMillis() + 5000L;
                }
            }
            while (flag1);

            Config.log("Chunks loaded: " + i1);
            Config.log("Finished loading visible chunks");
            ChunkRenderDispatcher.renderChunksUpdated = 0;
        }
        finally
        {
            this.mc.gameSettings.ofChunkUpdates = i;
            this.mc.gameSettings.ofLazyChunkLoading = flag;
        }
    }

    public IResourceType getResourceType()
    {
        return VanillaResourceType.MODELS;
    }

    public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd)
    {
        synchronized (this.setTileEntities)
        {
            this.setTileEntities.removeAll(tileEntitiesToRemove);
            this.setTileEntities.addAll(tileEntitiesToAdd);
        }
    }

    public static int getCombinedLight(IBlockDisplayReader lightReaderIn, BlockPos blockPosIn)
    {
        return getPackedLightmapCoords(lightReaderIn, lightReaderIn.getBlockState(blockPosIn), blockPosIn);
    }

    public static int getPackedLightmapCoords(IBlockDisplayReader lightReaderIn, BlockState blockStateIn, BlockPos blockPosIn)
    {
        if (blockStateIn.isEmissiveRendering(lightReaderIn, blockPosIn))
        {
            return 15728880;
        }
        else
        {
            int i = lightReaderIn.getLightFor(LightType.SKY, blockPosIn);
            int j = lightReaderIn.getLightFor(LightType.BLOCK, blockPosIn);
            int k = blockStateIn.getLightValue(lightReaderIn, blockPosIn);

            if (j < k)
            {
                j = k;
            }

            int l = i << 20 | j << 4;

            if (Config.isDynamicLights() && lightReaderIn instanceof IBlockReader && (!ambientOcclusion || !blockStateIn.isOpaqueCube(lightReaderIn, blockPosIn)))
            {
                l = DynamicLights.getCombinedLight(blockPosIn, l);
            }

            return l;
        }
    }

    @Nullable
    public Framebuffer getEntityOutlineFramebuffer()
    {
        return this.entityOutlineFramebuffer;
    }

    @Nullable
    public Framebuffer func_239228_q_()
    {
        return this.field_239222_F_;
    }

    @Nullable
    public Framebuffer func_239229_r_()
    {
        return this.field_239223_G_;
    }

    @Nullable
    public Framebuffer func_239230_s_()
    {
        return this.field_239224_H_;
    }

    @Nullable
    public Framebuffer func_239231_t_()
    {
        return this.field_239225_I_;
    }

    @Nullable
    public Framebuffer func_239232_u_()
    {
        return this.field_239226_J_;
    }

    public static class LocalRenderInformationContainer
    {
        public final ChunkRenderDispatcher.ChunkRender renderChunk;
        private Direction facing;
        private int setFacing;
        private int counter;

        public LocalRenderInformationContainer(ChunkRenderDispatcher.ChunkRender p_i242106_1_, @Nullable Direction p_i242106_2_, int p_i242106_3_)
        {
            this.renderChunk = p_i242106_1_;
            this.facing = p_i242106_2_;
            this.setFacing = p_i242106_3_;
        }

        public void setDirection(byte dir, Direction facingIn)
        {
            this.setFacing = this.setFacing | dir | 1 << this.facing.ordinal();
        }

        public boolean hasDirection(Direction facingIn)
        {
            return (this.setFacing & 1 << facingIn.ordinal()) > 0;
        }

        private void initialize(Direction p_initialize_1_, int p_initialize_2_, int p_initialize_3_)
        {
            this.facing = p_initialize_1_;
            this.setFacing = p_initialize_2_;
            this.counter = p_initialize_3_;
        }
    }

    public static class ShaderException extends RuntimeException
    {
        public ShaderException(String p_i232463_1_, Throwable p_i232463_2_)
        {
            super(p_i232463_1_, p_i232463_2_);
        }
    }
}
