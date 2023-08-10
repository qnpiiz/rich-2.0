package net.minecraft.client.gui.overlay;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.PlatformDescriptors;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.DataFixUtils;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.TextureAnimations;
import net.optifine.reflect.Reflector;
import net.optifine.util.GuiPoint;
import net.optifine.util.GuiRect;
import net.optifine.util.GuiUtils;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.NativeMemory;

public class DebugOverlayGui extends AbstractGui
{
    private static final Map<Heightmap.Type, String> HEIGHTMAP_NAMES = Util.make(new EnumMap<>(Heightmap.Type.class), (p_lambda$static$0_0_) ->
    {
        p_lambda$static$0_0_.put(Heightmap.Type.WORLD_SURFACE_WG, "SW");
        p_lambda$static$0_0_.put(Heightmap.Type.WORLD_SURFACE, "S");
        p_lambda$static$0_0_.put(Heightmap.Type.OCEAN_FLOOR_WG, "OW");
        p_lambda$static$0_0_.put(Heightmap.Type.OCEAN_FLOOR, "O");
        p_lambda$static$0_0_.put(Heightmap.Type.MOTION_BLOCKING, "M");
        p_lambda$static$0_0_.put(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML");
    });
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private RayTraceResult rayTraceBlock;
    private RayTraceResult rayTraceFluid;
    @Nullable
    private ChunkPos chunkPos;
    @Nullable
    private Chunk chunk;
    @Nullable
    private CompletableFuture<Chunk> futureChunk;
    private String debugOF = null;
    private List<String> debugInfoLeft = null;
    private List<String> debugInfoRight = null;
    private long updateInfoLeftTimeMs = 0L;
    private long updateInfoRightTimeMs = 0L;

    public DebugOverlayGui(Minecraft mc)
    {
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
    }

    public void resetChunk()
    {
        this.futureChunk = null;
        this.chunk = null;
    }

    public void render(MatrixStack p_194818_1_)
    {
        this.mc.getProfiler().startSection("debug");
        RenderSystem.pushMatrix();
        Entity entity = this.mc.getRenderViewEntity();
        this.rayTraceBlock = entity.pick(20.0D, 0.0F, false);
        this.rayTraceFluid = entity.pick(20.0D, 0.0F, true);
        this.renderDebugInfoLeft(p_194818_1_);
        this.renderDebugInfoRight(p_194818_1_);
        RenderSystem.popMatrix();

        if (this.mc.gameSettings.showLagometer)
        {
            int i = this.mc.getMainWindow().getScaledWidth();
            this.func_238509_a_(p_194818_1_, this.mc.getFrameTimer(), 0, i / 2, true);
            IntegratedServer integratedserver = this.mc.getIntegratedServer();

            if (integratedserver != null)
            {
                this.func_238509_a_(p_194818_1_, integratedserver.getFrameTimer(), i - Math.min(i / 2, 240), i / 2, false);
            }
        }

        this.mc.getProfiler().endSection();
    }

    protected void renderDebugInfoLeft(MatrixStack p_230024_1_)
    {
        List<String> list = this.debugInfoLeft;

        if (list == null || System.currentTimeMillis() > this.updateInfoLeftTimeMs)
        {
            list = this.getDebugInfoLeft();
            list.add("");
            boolean flag = this.mc.getIntegratedServer() != null;
            list.add("Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden") + (flag ? " FPS + TPS" : " FPS") + " [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
            list.add("For help: press F3 + Q");
            this.debugInfoLeft = list;
            this.updateInfoLeftTimeMs = System.currentTimeMillis() + 100L;
        }

        GuiPoint[] aguipoint = new GuiPoint[list.size()];
        GuiRect[] aguirect = new GuiRect[list.size()];

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = 9;
                int k = this.fontRenderer.getStringWidth(s);
                int l = 2;
                int i1 = 2 + j * i;
                aguirect[i] = new GuiRect(1, i1 - 1, 2 + k + 1, i1 + j - 1);
                aguipoint[i] = new GuiPoint(2, i1);
            }
        }

        GuiUtils.fill(p_230024_1_.getLast().getMatrix(), aguirect, -1873784752);
        this.fontRenderer.renderStrings(list, aguipoint, 14737632, p_230024_1_.getLast().getMatrix(), false, this.fontRenderer.getBidiFlag());
    }

    protected void renderDebugInfoRight(MatrixStack p_230025_1_)
    {
        List<String> list = this.debugInfoRight;

        if (list == null || System.currentTimeMillis() > this.updateInfoRightTimeMs)
        {
            list = this.getDebugInfoRight();
            this.debugInfoRight = list;
            this.updateInfoRightTimeMs = System.currentTimeMillis() + 100L;
        }

        GuiPoint[] aguipoint = new GuiPoint[list.size()];
        GuiRect[] aguirect = new GuiRect[list.size()];

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = 9;
                int k = this.fontRenderer.getStringWidth(s);
                int l = this.mc.getMainWindow().getScaledWidth() - 2 - k;
                int i1 = 2 + j * i;
                aguirect[i] = new GuiRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1);
                aguipoint[i] = new GuiPoint(l, i1);
            }
        }

        GuiUtils.fill(p_230025_1_.getLast().getMatrix(), aguirect, -1873784752);
        this.fontRenderer.renderStrings(list, aguipoint, 14737632, p_230025_1_.getLast().getMatrix(), false, this.fontRenderer.getBidiFlag());
    }

    protected List<String> getDebugInfoLeft()
    {
        if (this.mc.debug != this.debugOF)
        {
            StringBuffer stringbuffer = new StringBuffer(this.mc.debug);
            int i = Config.getChunkUpdates();
            int j = this.mc.debug.indexOf("T: ");

            if (j >= 0)
            {
                stringbuffer.insert(j, "(" + i + " chunk updates) ");
            }

            int k = Config.getFpsMin();
            int l = this.mc.debug.indexOf(" fps ");

            if (l >= 0)
            {
                stringbuffer.replace(0, l + 4, Config.getFpsString());
            }

            if (Config.isSmoothFps())
            {
                stringbuffer.append(" sf");
            }

            if (Config.isFastRender())
            {
                stringbuffer.append(" fr");
            }

            if (Config.isAnisotropicFiltering())
            {
                stringbuffer.append(" af");
            }

            if (Config.isAntialiasing())
            {
                stringbuffer.append(" aa");
            }

            if (Config.isRenderRegions())
            {
                stringbuffer.append(" reg");
            }

            if (Config.isShaders())
            {
                stringbuffer.append(" sh");
            }

            this.mc.debug = stringbuffer.toString();
            this.debugOF = this.mc.debug;
        }

        List<String> list = this.getInfoLeft();
        StringBuilder stringbuilder = new StringBuilder();
        AtlasTexture atlastexture = Config.getTextureMap();
        stringbuilder.append(", A: ");

        if (SmartAnimations.isActive())
        {
            stringbuilder.append(atlastexture.getCountAnimationsActive() + TextureAnimations.getCountAnimationsActive());
            stringbuilder.append("/");
        }

        stringbuilder.append(atlastexture.getCountAnimations() + TextureAnimations.getCountAnimations());
        String s1 = stringbuilder.toString();

        for (int i1 = 0; i1 < list.size(); ++i1)
        {
            String s = list.get(i1);

            if (s != null && s.startsWith("P: "))
            {
                s = s + s1;
                list.set(i1, s);
                break;
            }
        }

        return list;
    }

    protected List<String> getInfoLeft()
    {
        IntegratedServer integratedserver = this.mc.getIntegratedServer();
        NetworkManager networkmanager = this.mc.getConnection().getNetworkManager();
        float f = networkmanager.getPacketsSent();
        float f1 = networkmanager.getPacketsReceived();
        String s;

        if (integratedserver != null)
        {
            s = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedserver.getTickTime(), f, f1);
        }
        else
        {
            s = String.format("\"%s\" server, %.0f tx, %.0f rx", this.mc.player.getServerBrand(), f, f1);
        }

        BlockPos blockpos = this.mc.getRenderViewEntity().getPosition();

        if (this.mc.isReducedDebug())
        {
            return Lists.newArrayList("Minecraft " + SharedConstants.getVersion().getName() + " (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, s, this.mc.worldRenderer.getDebugInfoRenders(), this.mc.worldRenderer.getDebugInfoEntities(), "P: " + this.mc.particles.getStatistics() + ". T: " + this.mc.world.getCountLoadedEntities(), this.mc.world.getProviderName(), "", String.format("Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            Direction direction = entity.getHorizontalFacing();
            String s1;

            switch (direction)
            {
                case NORTH:
                    s1 = "Towards negative Z";
                    break;

                case SOUTH:
                    s1 = "Towards positive Z";
                    break;

                case WEST:
                    s1 = "Towards negative X";
                    break;

                case EAST:
                    s1 = "Towards positive X";
                    break;

                default:
                    s1 = "Invalid";
            }

            ChunkPos chunkpos = new ChunkPos(blockpos);

            if (!Objects.equals(this.chunkPos, chunkpos))
            {
                this.chunkPos = chunkpos;
                this.resetChunk();
            }

            World world = this.getWorld();
            LongSet longset = (LongSet)(world instanceof ServerWorld ? ((ServerWorld)world).getForcedChunks() : LongSets.EMPTY_SET);
            List<String> list = Lists.newArrayList("Minecraft " + SharedConstants.getVersion().getName() + " (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType()) + ")", this.mc.debug, s, this.mc.worldRenderer.getDebugInfoRenders(), this.mc.worldRenderer.getDebugInfoEntities(), "P: " + this.mc.particles.getStatistics() + ". T: " + this.mc.world.getCountLoadedEntities(), this.mc.world.getProviderName());
            String s2 = this.getServerChunkStats();

            if (s2 != null)
            {
                list.add(s2);
            }

            list.add(this.mc.world.getDimensionKey().getLocation() + " FC: " + longset.size());
            list.add("");
            list.add(String.format(Locale.ROOT, "XYZ: %.3f / %.5f / %.3f", this.mc.getRenderViewEntity().getPosX(), this.mc.getRenderViewEntity().getPosY(), this.mc.getRenderViewEntity().getPosZ()));
            list.add(String.format("Block: %d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()));
            list.add(String.format("Chunk: %d %d %d in %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15, blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4));
            list.add(String.format(Locale.ROOT, "Facing: %s (%s) (%.1f / %.1f)", direction, s1, MathHelper.wrapDegrees(entity.rotationYaw), MathHelper.wrapDegrees(entity.rotationPitch)));

            if (this.mc.world != null)
            {
                if (this.mc.world.isBlockLoaded(blockpos))
                {
                    Chunk chunk = this.getChunk();

                    if (chunk.isEmpty())
                    {
                        list.add("Waiting for chunk...");
                    }
                    else
                    {
                        int i = this.mc.world.getChunkProvider().getLightManager().getLightSubtracted(blockpos, 0);
                        int j = this.mc.world.getLightFor(LightType.SKY, blockpos);
                        int k = this.mc.world.getLightFor(LightType.BLOCK, blockpos);
                        list.add("Client Light: " + i + " (" + j + " sky, " + k + " block)");
                        Chunk chunk1 = this.getServerChunk();

                        if (chunk1 != null)
                        {
                            WorldLightManager worldlightmanager = world.getChunkProvider().getLightManager();
                            list.add("Server Light: (" + worldlightmanager.getLightEngine(LightType.SKY).getLightFor(blockpos) + " sky, " + worldlightmanager.getLightEngine(LightType.BLOCK).getLightFor(blockpos) + " block)");
                        }
                        else
                        {
                            list.add("Server Light: (?? sky, ?? block)");
                        }

                        StringBuilder stringbuilder = new StringBuilder("CH");

                        for (Heightmap.Type heightmap$type : Heightmap.Type.values())
                        {
                            if (heightmap$type.isUsageClient())
                            {
                                stringbuilder.append(" ").append(HEIGHTMAP_NAMES.get(heightmap$type)).append(": ").append(chunk.getTopBlockY(heightmap$type, blockpos.getX(), blockpos.getZ()));
                            }
                        }

                        list.add(stringbuilder.toString());
                        stringbuilder.setLength(0);
                        stringbuilder.append("SH");

                        for (Heightmap.Type heightmap$type1 : Heightmap.Type.values())
                        {
                            if (heightmap$type1.isUsageNotWorldgen())
                            {
                                stringbuilder.append(" ").append(HEIGHTMAP_NAMES.get(heightmap$type1)).append(": ");

                                if (chunk1 != null)
                                {
                                    stringbuilder.append(chunk1.getTopBlockY(heightmap$type1, blockpos.getX(), blockpos.getZ()));
                                }
                                else
                                {
                                    stringbuilder.append("??");
                                }
                            }
                        }

                        list.add(stringbuilder.toString());

                        if (blockpos.getY() >= 0 && blockpos.getY() < 256)
                        {
                            list.add("Biome: " + this.mc.world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(this.mc.world.getBiome(blockpos)));
                            long i1 = 0L;
                            float f2 = 0.0F;

                            if (chunk1 != null)
                            {
                                f2 = world.getMoonFactor();
                                i1 = chunk1.getInhabitedTime();
                            }

                            DifficultyInstance difficultyinstance = new DifficultyInstance(world.getDifficulty(), world.getDayTime(), i1, f2);
                            list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day %d)", difficultyinstance.getAdditionalDifficulty(), difficultyinstance.getClampedAdditionalDifficulty(), this.mc.world.getDayTime() / 24000L));
                        }
                    }
                }
                else
                {
                    list.add("Outside of world...");
                }
            }
            else
            {
                list.add("Outside of world...");
            }

            ServerWorld serverworld = this.func_238515_d_();

            if (serverworld != null)
            {
                WorldEntitySpawner.EntityDensityManager worldentityspawner$entitydensitymanager = serverworld.getChunkProvider().func_241101_k_();

                if (worldentityspawner$entitydensitymanager != null)
                {
                    Object2IntMap<EntityClassification> object2intmap = worldentityspawner$entitydensitymanager.func_234995_b_();
                    int l = worldentityspawner$entitydensitymanager.func_234988_a_();
                    list.add("SC: " + l + ", " + (String)Stream.of(EntityClassification.values()).map((p_lambda$getInfoLeft$1_1_) ->
                    {
                        return Character.toUpperCase(p_lambda$getInfoLeft$1_1_.getName().charAt(0)) + ": " + object2intmap.getInt(p_lambda$getInfoLeft$1_1_);
                    }).collect(Collectors.joining(", ")));
                }
                else
                {
                    list.add("SC: N/A");
                }
            }

            ShaderGroup shadergroup = this.mc.gameRenderer.getShaderGroup();

            if (shadergroup != null)
            {
                list.add("Shader: " + shadergroup.getShaderGroupName());
            }

            list.add(this.mc.getSoundHandler().getDebugString() + String.format(" (Mood %d%%)", Math.round(this.mc.player.getDarknessAmbience() * 100.0F)));
            return list;
        }
    }

    @Nullable
    private ServerWorld func_238515_d_()
    {
        IntegratedServer integratedserver = this.mc.getIntegratedServer();
        return integratedserver != null ? integratedserver.getWorld(this.mc.world.getDimensionKey()) : null;
    }

    @Nullable
    private String getServerChunkStats()
    {
        ServerWorld serverworld = this.func_238515_d_();
        return serverworld != null ? serverworld.getProviderName() : null;
    }

    private World getWorld()
    {
        return DataFixUtils.orElse(Optional.ofNullable(this.mc.getIntegratedServer()).flatMap((p_lambda$getWorld$2_1_) ->
        {
            return Optional.ofNullable(p_lambda$getWorld$2_1_.getWorld(this.mc.world.getDimensionKey()));
        }), this.mc.world);
    }

    @Nullable
    private Chunk getServerChunk()
    {
        if (this.futureChunk == null)
        {
            ServerWorld serverworld = this.func_238515_d_();

            if (serverworld != null)
            {
                this.futureChunk = serverworld.getChunkProvider().func_217232_b(this.chunkPos.x, this.chunkPos.z, ChunkStatus.FULL, false).thenApply((p_lambda$getServerChunk$5_0_) ->
                {
                    return p_lambda$getServerChunk$5_0_.map((p_lambda$null$3_0_) -> {
                        return (Chunk)p_lambda$null$3_0_;
                    }, (p_lambda$null$4_0_) -> {
                        return null;
                    });
                });
            }

            if (this.futureChunk == null)
            {
                this.futureChunk = CompletableFuture.completedFuture(this.getChunk());
            }
        }

        return this.futureChunk.getNow((Chunk)null);
    }

    private Chunk getChunk()
    {
        if (this.chunk == null)
        {
            this.chunk = this.mc.world.getChunk(this.chunkPos.x, this.chunkPos.z);
        }

        return this.chunk;
    }

    protected List<String> getDebugInfoRight()
    {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        List<String> list = Lists.newArrayList(String.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), String.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMb(l), bytesToMb(i)), String.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMb(j)), "", String.format("CPU: %s", PlatformDescriptors.getCpuInfo()), "", String.format("Display: %dx%d (%s)", Minecraft.getInstance().getMainWindow().getFramebufferWidth(), Minecraft.getInstance().getMainWindow().getFramebufferHeight(), PlatformDescriptors.getGlVendor()), PlatformDescriptors.getGlRenderer(), PlatformDescriptors.getGlVersion());
        long i1 = NativeMemory.getBufferAllocated();
        long j1 = NativeMemory.getBufferMaximum();
        long k1 = NativeMemory.getImageAllocated();
        String s = "Native: " + bytesToMb(i1) + "/" + bytesToMb(j1) + "+" + bytesToMb(k1) + "MB";
        list.add(3, s);
        list.set(4, "Allocation: " + MemoryMonitor.getAllocationRateAvgMb() + "MB/s");

        if (Reflector.BrandingControl_getBrandings.exists())
        {
            list.add("");

            for (String s1 : (Set<String>)(Set<?>)Reflector.call(Reflector.BrandingControl_getBrandings, true, false))
            {
                if (!s1.startsWith("Minecraft "))
                {
                    list.add(s1);
                }
            }
        }

        if (this.mc.isReducedDebug())
        {
            return list;
        }
        else
        {
            if (this.rayTraceBlock.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = ((BlockRayTraceResult)this.rayTraceBlock).getPos();
                BlockState blockstate = this.mc.world.getBlockState(blockpos);
                list.add("");
                list.add(TextFormatting.UNDERLINE + "Targeted Block: " + blockpos.getX() + ", " + blockpos.getY() + ", " + blockpos.getZ());
                list.add(String.valueOf((Object)Registry.BLOCK.getKey(blockstate.getBlock())));

                for (Entry < Property<?>, Comparable<? >> entry : blockstate.getValues().entrySet())
                {
                    list.add(this.getPropertyString(entry));
                }

                Collection<ResourceLocation> collection1;

                if (Reflector.IForgeBlock_getTags.exists())
                {
                    collection1 = (Collection)Reflector.call(blockstate.getBlock(), Reflector.IForgeBlock_getTags);
                }
                else
                {
                    collection1 = this.mc.getConnection().getTags().getBlockTags().getOwningTags(blockstate.getBlock());
                }

                for (ResourceLocation resourcelocation : collection1)
                {
                    list.add("#" + resourcelocation);
                }
            }

            if (this.rayTraceFluid.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos1 = ((BlockRayTraceResult)this.rayTraceFluid).getPos();
                FluidState fluidstate = this.mc.world.getFluidState(blockpos1);
                list.add("");
                list.add(TextFormatting.UNDERLINE + "Targeted Fluid: " + blockpos1.getX() + ", " + blockpos1.getY() + ", " + blockpos1.getZ());
                list.add(String.valueOf((Object)Registry.FLUID.getKey(fluidstate.getFluid())));

                for (Entry < Property<?>, Comparable<? >> entry1 : fluidstate.getValues().entrySet())
                {
                    list.add(this.getPropertyString(entry1));
                }

                Collection<ResourceLocation> collection2;

                if (Reflector.ForgeFluid_getTags.exists())
                {
                    collection2 = (Collection)Reflector.call(fluidstate.getFluid(), Reflector.ForgeFluid_getTags);
                }
                else
                {
                    collection2 = this.mc.getConnection().getTags().getFluidTags().getOwningTags(fluidstate.getFluid());
                }

                for (ResourceLocation resourcelocation1 : collection2)
                {
                    list.add("#" + resourcelocation1);
                }
            }

            Entity entity = this.mc.pointedEntity;

            if (entity != null)
            {
                list.add("");
                list.add(TextFormatting.UNDERLINE + "Targeted Entity");
                list.add(String.valueOf((Object)Registry.ENTITY_TYPE.getKey(entity.getType())));

                if (Reflector.ForgeEntityType_getTags.exists())
                {
                    Collection<ResourceLocation> collection = (Collection)Reflector.call(entity.getType(), Reflector.ForgeEntityType_getTags);
                    collection.forEach((p_lambda$getDebugInfoRight$6_1_) ->
                    {
                        list.add("#" + p_lambda$getDebugInfoRight$6_1_);
                    });
                }
            }

            return list;
        }
    }

    private String getPropertyString(Entry < Property<?>, Comparable<? >> entryIn)
    {
        Property<?> property = entryIn.getKey();
        Comparable<?> comparable = entryIn.getValue();
        String s = Util.getValueName(property, comparable);

        if (Boolean.TRUE.equals(comparable))
        {
            s = TextFormatting.GREEN + s;
        }
        else if (Boolean.FALSE.equals(comparable))
        {
            s = TextFormatting.RED + s;
        }

        return property.getName() + ": " + s;
    }

    private void func_238509_a_(MatrixStack p_238509_1_, FrameTimer p_238509_2_, int p_238509_3_, int p_238509_4_, boolean p_238509_5_)
    {
        if (!p_238509_5_)
        {
            int i = (int)(512.0D / this.mc.getMainWindow().getGuiScaleFactor());
            p_238509_3_ = Math.max(p_238509_3_, i);
            p_238509_4_ = this.mc.getMainWindow().getScaledWidth() - p_238509_3_;
            RenderSystem.disableDepthTest();
            int j = p_238509_2_.getLastIndex();
            int k = p_238509_2_.getIndex();
            long[] along = p_238509_2_.getFrames();
            int l = p_238509_3_;
            int i1 = Math.max(0, along.length - p_238509_4_);
            int j1 = along.length - i1;
            int k1 = p_238509_2_.parseIndex(j + i1);
            long l1 = 0L;
            int i2 = Integer.MAX_VALUE;
            int j2 = Integer.MIN_VALUE;

            for (int k2 = 0; k2 < j1; ++k2)
            {
                int l2 = (int)(along[p_238509_2_.parseIndex(k1 + k2)] / 1000000L);
                i2 = Math.min(i2, l2);
                j2 = Math.max(j2, l2);
                l1 += (long)l2;
            }

            int l4 = this.mc.getMainWindow().getScaledHeight();
            fill(p_238509_1_, p_238509_3_, l4 - 60, p_238509_3_ + j1, l4, -1873784752);
            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

            for (Matrix4f matrix4f = TransformationMatrix.identity().getMatrix(); k1 != k; k1 = p_238509_2_.parseIndex(k1 + 1))
            {
                int i3 = p_238509_2_.getLineHeight(along[k1], p_238509_5_ ? 30 : 60, p_238509_5_ ? 60 : 20);
                int j3 = p_238509_5_ ? 100 : 60;
                int k3 = this.getFrameColor(MathHelper.clamp(i3, 0, j3), 0, j3 / 2, j3);
                int l3 = k3 >> 24 & 255;
                int i4 = k3 >> 16 & 255;
                int j4 = k3 >> 8 & 255;
                int k4 = k3 & 255;
                bufferbuilder.pos(matrix4f, (float)(l + 1), (float)l4, 0.0F).color(i4, j4, k4, l3).endVertex();
                bufferbuilder.pos(matrix4f, (float)(l + 1), (float)(l4 - i3 + 1), 0.0F).color(i4, j4, k4, l3).endVertex();
                bufferbuilder.pos(matrix4f, (float)l, (float)(l4 - i3 + 1), 0.0F).color(i4, j4, k4, l3).endVertex();
                bufferbuilder.pos(matrix4f, (float)l, (float)l4, 0.0F).color(i4, j4, k4, l3).endVertex();
                ++l;
            }

            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            if (p_238509_5_)
            {
                fill(p_238509_1_, p_238509_3_ + 1, l4 - 30 + 1, p_238509_3_ + 14, l4 - 30 + 10, -1873784752);
                this.fontRenderer.drawString(p_238509_1_, "60 FPS", (float)(p_238509_3_ + 2), (float)(l4 - 30 + 2), 14737632);
                this.hLine(p_238509_1_, p_238509_3_, p_238509_3_ + j1 - 1, l4 - 30, -1);
                fill(p_238509_1_, p_238509_3_ + 1, l4 - 60 + 1, p_238509_3_ + 14, l4 - 60 + 10, -1873784752);
                this.fontRenderer.drawString(p_238509_1_, "30 FPS", (float)(p_238509_3_ + 2), (float)(l4 - 60 + 2), 14737632);
                this.hLine(p_238509_1_, p_238509_3_, p_238509_3_ + j1 - 1, l4 - 60, -1);
            }
            else
            {
                fill(p_238509_1_, p_238509_3_ + 1, l4 - 60 + 1, p_238509_3_ + 14, l4 - 60 + 10, -1873784752);
                this.fontRenderer.drawString(p_238509_1_, "20 TPS", (float)(p_238509_3_ + 2), (float)(l4 - 60 + 2), 14737632);
                this.hLine(p_238509_1_, p_238509_3_, p_238509_3_ + j1 - 1, l4 - 60, -1);
            }

            this.hLine(p_238509_1_, p_238509_3_, p_238509_3_ + j1 - 1, l4 - 1, -1);
            this.vLine(p_238509_1_, p_238509_3_, l4 - 60, l4, -1);
            this.vLine(p_238509_1_, p_238509_3_ + j1 - 1, l4 - 60, l4, -1);

            if (p_238509_5_ && this.mc.gameSettings.framerateLimit > 0 && this.mc.gameSettings.framerateLimit <= 250)
            {
                this.hLine(p_238509_1_, p_238509_3_, p_238509_3_ + j1 - 1, l4 - 1 - (int)(1800.0D / (double)this.mc.gameSettings.framerateLimit), -16711681);
            }

            String s = i2 + " ms min";
            String s1 = l1 / (long)j1 + " ms avg";
            String s2 = j2 + " ms max";
            this.fontRenderer.drawStringWithShadow(p_238509_1_, s, (float)(p_238509_3_ + 2), (float)(l4 - 60 - 9), 14737632);
            this.fontRenderer.drawStringWithShadow(p_238509_1_, s1, (float)(p_238509_3_ + j1 / 2 - this.fontRenderer.getStringWidth(s1) / 2), (float)(l4 - 60 - 9), 14737632);
            this.fontRenderer.drawStringWithShadow(p_238509_1_, s2, (float)(p_238509_3_ + j1 - this.fontRenderer.getStringWidth(s2)), (float)(l4 - 60 - 9), 14737632);
            RenderSystem.enableDepthTest();
        }
    }

    private int getFrameColor(int height, int heightMin, int heightMid, int heightMax)
    {
        return height < heightMid ? this.blendColors(-16711936, -256, (float)height / (float)heightMid) : this.blendColors(-256, -65536, (float)(height - heightMid) / (float)(heightMax - heightMid));
    }

    private int blendColors(int col1, int col2, float factor)
    {
        int i = col1 >> 24 & 255;
        int j = col1 >> 16 & 255;
        int k = col1 >> 8 & 255;
        int l = col1 & 255;
        int i1 = col2 >> 24 & 255;
        int j1 = col2 >> 16 & 255;
        int k1 = col2 >> 8 & 255;
        int l1 = col2 & 255;
        int i2 = MathHelper.clamp((int)MathHelper.lerp(factor, (float)i, (float)i1), 0, 255);
        int j2 = MathHelper.clamp((int)MathHelper.lerp(factor, (float)j, (float)j1), 0, 255);
        int k2 = MathHelper.clamp((int)MathHelper.lerp(factor, (float)k, (float)k1), 0, 255);
        int l2 = MathHelper.clamp((int)MathHelper.lerp(factor, (float)l, (float)l1), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }
}
