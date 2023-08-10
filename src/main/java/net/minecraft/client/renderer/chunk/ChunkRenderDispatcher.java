package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.client.extensions.IForgeRenderChunk;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.optifine.BlockPosM;
import net.optifine.Config;
import net.optifine.CustomBlockLayers;
import net.optifine.override.ChunkCacheOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.render.AabbFrame;
import net.optifine.render.ChunkLayerMap;
import net.optifine.render.ChunkLayerSet;
import net.optifine.render.ICamera;
import net.optifine.render.RenderEnv;
import net.optifine.render.RenderTypes;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;
import net.optifine.util.ChunkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderDispatcher
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final PriorityQueue<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask> renderTasks = Queues.newPriorityQueue();
    private final Queue<RegionRenderCacheBuilder> freeBuilders;
    private final Queue<Runnable> uploadTasks = Queues.newConcurrentLinkedQueue();
    private volatile int countRenderTasks;
    private volatile int countFreeBuilders;
    private final RegionRenderCacheBuilder fixedBuilder;
    private final DelegatedTaskExecutor<Runnable> delegatedTaskExecutor;
    private final Executor executor;
    private World world;
    private final WorldRenderer worldRenderer;
    private Vector3d renderPosition = Vector3d.ZERO;
    private int countRenderBuilders;
    private List<RegionRenderCacheBuilder> listPausedBuilders = new ArrayList<>();
    public static final RenderType[] BLOCK_RENDER_LAYERS = RenderType.getBlockRenderTypes().toArray(new RenderType[0]);
    private static final boolean FORGE = Reflector.ForgeHooksClient.exists();
    private static final boolean FORGE_CAN_RENDER_IN_LAYER_BS = Reflector.ForgeRenderTypeLookup_canRenderInLayerBs.exists();
    private static final boolean FORGE_CAN_RENDER_IN_LAYER_FS = Reflector.ForgeRenderTypeLookup_canRenderInLayerBs.exists();
    private static final boolean FORGE_SET_RENDER_LAYER = Reflector.ForgeHooksClient_setRenderLayer.exists();
    public static int renderChunksUpdated;

    public ChunkRenderDispatcher(World worldIn, WorldRenderer worldRendererIn, Executor executorIn, boolean java64bit, RegionRenderCacheBuilder fixedBuilderIn)
    {
        this(worldIn, worldRendererIn, executorIn, java64bit, fixedBuilderIn, -1);
    }

    public ChunkRenderDispatcher(World p_i242112_1_, WorldRenderer p_i242112_2_, Executor p_i242112_3_, boolean p_i242112_4_, RegionRenderCacheBuilder p_i242112_5_, int p_i242112_6_)
    {
        this.world = p_i242112_1_;
        this.worldRenderer = p_i242112_2_;
        int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / (RenderType.getBlockRenderTypes().stream().mapToInt(RenderType::getBufferSize).sum() * 4) - 1);
        int j = Runtime.getRuntime().availableProcessors();
        int k = p_i242112_4_ ? j : Math.min(j, 4);
        int l = Math.max(1, Math.min(k, i));

        if (p_i242112_6_ > 0)
        {
            l = p_i242112_6_;
        }

        this.fixedBuilder = p_i242112_5_;
        List<RegionRenderCacheBuilder> list = Lists.newArrayListWithExpectedSize(l);

        try
        {
            for (int i1 = 0; i1 < l; ++i1)
            {
                list.add(new RegionRenderCacheBuilder());
            }
        }
        catch (OutOfMemoryError outofmemoryerror1)
        {
            LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
            int j1 = Math.min(list.size() * 2 / 3, list.size() - 1);

            for (int k1 = 0; k1 < j1; ++k1)
            {
                list.remove(list.size() - 1);
            }

            System.gc();
        }

        this.freeBuilders = Queues.newConcurrentLinkedQueue(list);
        this.countFreeBuilders = this.freeBuilders.size();
        this.countRenderBuilders = this.countFreeBuilders;
        this.executor = p_i242112_3_;
        this.delegatedTaskExecutor = DelegatedTaskExecutor.create(p_i242112_3_, "Chunk Renderer");
        this.delegatedTaskExecutor.enqueue(this::runTask);
    }

    public void setWorld(World worldIn)
    {
        this.world = worldIn;
    }

    private void runTask()
    {
        if (!this.freeBuilders.isEmpty())
        {
            ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.renderTasks.poll();

            if (chunkrenderdispatcher$chunkrender$chunkrendertask != null)
            {
                RegionRenderCacheBuilder regionrendercachebuilder = this.freeBuilders.poll();

                if (regionrendercachebuilder == null)
                {
                    this.renderTasks.add(chunkrenderdispatcher$chunkrender$chunkrendertask);
                    return;
                }

                this.countRenderTasks = this.renderTasks.size();
                this.countFreeBuilders = this.freeBuilders.size();
                CompletableFuture.runAsync(() ->
                {
                }, this.executor).thenCompose((p_lambda$runTask$1_2_) ->
                {
                    return chunkrenderdispatcher$chunkrender$chunkrendertask.execute(regionrendercachebuilder);
                }).whenComplete((p_lambda$runTask$3_2_, p_lambda$runTask$3_3_) ->
                {
                    if (p_lambda$runTask$3_3_ != null)
                    {
                        CrashReport crashreport = CrashReport.makeCrashReport(p_lambda$runTask$3_3_, "Batching chunks");
                        Minecraft.getInstance().crashed(Minecraft.getInstance().addGraphicsAndWorldToCrashReport(crashreport));
                    }
                    else {
                        this.delegatedTaskExecutor.enqueue(() -> {
                            if (p_lambda$runTask$3_2_ == ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL)
                            {
                                regionrendercachebuilder.resetBuilders();
                            }
                            else {
                                regionrendercachebuilder.discardBuilders();
                            }

                            this.freeBuilders.add(regionrendercachebuilder);
                            this.countFreeBuilders = this.freeBuilders.size();
                            this.runTask();
                        });
                    }
                });
            }
        }
    }

    public String getDebugInfo()
    {
        return String.format("pC: %03d, pU: %02d, aB: %02d", this.countRenderTasks, this.uploadTasks.size(), this.countFreeBuilders);
    }

    public void setRenderPosition(Vector3d posIn)
    {
        this.renderPosition = posIn;
    }

    public Vector3d getRenderPosition()
    {
        return this.renderPosition;
    }

    public boolean runChunkUploads()
    {
        boolean flag;
        Runnable runnable;

        for (flag = false; (runnable = this.uploadTasks.poll()) != null; flag = true)
        {
            runnable.run();
        }

        return flag;
    }

    public void rebuildChunk(ChunkRenderDispatcher.ChunkRender chunkRenderIn)
    {
        chunkRenderIn.rebuildChunk();
    }

    public void stopChunkUpdates()
    {
        this.clearChunkUpdates();
    }

    public void schedule(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask renderTaskIn)
    {
        this.delegatedTaskExecutor.enqueue(() ->
        {
            this.renderTasks.offer(renderTaskIn);
            this.countRenderTasks = this.renderTasks.size();
            this.runTask();
        });
    }

    public CompletableFuture<Void> uploadChunkLayer(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn)
    {
        return CompletableFuture.runAsync(() ->
        {
        }, this.uploadTasks::add).thenCompose((p_lambda$uploadChunkLayer$6_3_) ->
        {
            return this.uploadChunkLayerRaw(bufferBuilderIn, vertexBufferIn);
        });
    }

    private CompletableFuture<Void> uploadChunkLayerRaw(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn)
    {
        return vertexBufferIn.uploadLater(bufferBuilderIn);
    }

    private void clearChunkUpdates()
    {
        while (!this.renderTasks.isEmpty())
        {
            ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.renderTasks.poll();

            if (chunkrenderdispatcher$chunkrender$chunkrendertask != null)
            {
                chunkrenderdispatcher$chunkrender$chunkrendertask.cancel();
            }
        }

        this.countRenderTasks = 0;
    }

    public boolean hasNoChunkUpdates()
    {
        return this.countRenderTasks == 0 && this.uploadTasks.isEmpty();
    }

    public void stopWorkerThreads()
    {
        this.clearChunkUpdates();
        this.delegatedTaskExecutor.close();
        this.freeBuilders.clear();
    }

    public void pauseChunkUpdates()
    {
        long i = System.currentTimeMillis();

        if (this.listPausedBuilders.size() <= 0)
        {
            while (this.listPausedBuilders.size() != this.countRenderBuilders)
            {
                this.runChunkUploads();
                RegionRenderCacheBuilder regionrendercachebuilder = this.freeBuilders.poll();

                if (regionrendercachebuilder != null)
                {
                    this.listPausedBuilders.add(regionrendercachebuilder);
                }

                if (System.currentTimeMillis() > i + 1000L)
                {
                    break;
                }
            }
        }
    }

    public void resumeChunkUpdates()
    {
        this.freeBuilders.addAll(this.listPausedBuilders);
        this.listPausedBuilders.clear();
    }

    public boolean updateChunkNow(ChunkRenderDispatcher.ChunkRender p_updateChunkNow_1_)
    {
        this.rebuildChunk(p_updateChunkNow_1_);
        return true;
    }

    public boolean updateChunkLater(ChunkRenderDispatcher.ChunkRender p_updateChunkLater_1_)
    {
        if (this.freeBuilders.isEmpty())
        {
            return false;
        }
        else
        {
            p_updateChunkLater_1_.rebuildChunkLater(this);
            return true;
        }
    }

    public boolean updateTransparencyLater(ChunkRenderDispatcher.ChunkRender p_updateTransparencyLater_1_)
    {
        return this.freeBuilders.isEmpty() ? false : p_updateTransparencyLater_1_.resortTransparency(RenderTypes.TRANSLUCENT, this);
    }

    public class ChunkRender implements IForgeRenderChunk
    {
        public final AtomicReference<ChunkRenderDispatcher.CompiledChunk> compiledChunk = new AtomicReference<>(ChunkRenderDispatcher.CompiledChunk.DUMMY);
        @Nullable
        private ChunkRenderDispatcher.ChunkRender.RebuildTask lastRebuildTask;
        @Nullable
        private ChunkRenderDispatcher.ChunkRender.SortTransparencyTask lastResortTransparencyTask;
        private final Set<TileEntity> globalTileEntities = Sets.newHashSet();
        private final ChunkLayerMap<VertexBuffer> vertexBuffers = new ChunkLayerMap<>((p_lambda$new$0_0_) ->
        {
            return new VertexBuffer(DefaultVertexFormats.BLOCK);
        });
        public AxisAlignedBB boundingBox;
        private int frameIndex = -1;
        private boolean needsUpdate = true;
        private final BlockPos.Mutable position = new BlockPos.Mutable(-1, -1, -1);
        private final BlockPos.Mutable[] mapEnumFacing = Util.make(new BlockPos.Mutable[6], (p_lambda$new$1_0_) ->
        {
            for (int i = 0; i < p_lambda$new$1_0_.length; ++i)
            {
                p_lambda$new$1_0_[i] = new BlockPos.Mutable();
            }
        });
        private boolean needsImmediateUpdate;
        private final boolean isMipmaps = Config.isMipmaps();
        private final boolean fixBlockLayer = !Reflector.BetterFoliageClient.exists();
        private boolean playerUpdate = false;
        private boolean renderRegions = Config.isRenderRegions();
        public int regionX;
        public int regionZ;
        private int regionDX;
        private int regionDY;
        private int regionDZ;
        private final ChunkRenderDispatcher.ChunkRender[] renderChunksOfset16 = new ChunkRenderDispatcher.ChunkRender[6];
        private boolean renderChunksOffset16Updated = false;
        private Chunk chunk;
        private ChunkRenderDispatcher.ChunkRender[] renderChunkNeighbours = new ChunkRenderDispatcher.ChunkRender[Direction.VALUES.length];
        private ChunkRenderDispatcher.ChunkRender[] renderChunkNeighboursValid = new ChunkRenderDispatcher.ChunkRender[Direction.VALUES.length];
        private boolean renderChunkNeighboursUpated = false;
        private WorldRenderer.LocalRenderInformationContainer renderInfo = new WorldRenderer.LocalRenderInformationContainer(this, (Direction)null, 0);
        public AabbFrame boundingBoxParent;

        private boolean isChunkLoaded(BlockPos blockPosIn)
        {
            return ChunkRenderDispatcher.this.world.getChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4, ChunkStatus.FULL, false) != null;
        }

        public boolean shouldStayLoaded()
        {
            int i = 24;

            if (!(this.getDistanceSq() > 576.0D))
            {
                return true;
            }
            else
            {
                return this.isChunkLoaded(this.mapEnumFacing[Direction.WEST.ordinal()]) && this.isChunkLoaded(this.mapEnumFacing[Direction.NORTH.ordinal()]) && this.isChunkLoaded(this.mapEnumFacing[Direction.EAST.ordinal()]) && this.isChunkLoaded(this.mapEnumFacing[Direction.SOUTH.ordinal()]);
            }
        }

        public boolean setFrameIndex(int frameIndexIn)
        {
            if (this.frameIndex == frameIndexIn)
            {
                return false;
            }
            else
            {
                this.frameIndex = frameIndexIn;
                return true;
            }
        }

        public VertexBuffer getVertexBuffer(RenderType renderTypeIn)
        {
            return this.vertexBuffers.get(renderTypeIn);
        }

        public void setPosition(int x, int y, int z)
        {
            if (x != this.position.getX() || y != this.position.getY() || z != this.position.getZ())
            {
                this.stopCompileTask();
                this.position.setPos(x, y, z);

                if (this.renderRegions)
                {
                    int i = 8;
                    this.regionX = x >> i << i;
                    this.regionZ = z >> i << i;
                    this.regionDX = x - this.regionX;
                    this.regionDY = y;
                    this.regionDZ = z - this.regionZ;
                }

                this.boundingBox = new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 16), (double)(y + 16), (double)(z + 16));

                for (Direction direction : Direction.VALUES)
                {
                    this.mapEnumFacing[direction.ordinal()].setPos(this.position).move(direction, 16);
                }

                this.renderChunksOffset16Updated = false;
                this.renderChunkNeighboursUpated = false;

                for (int j = 0; j < this.renderChunkNeighbours.length; ++j)
                {
                    ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.renderChunkNeighbours[j];

                    if (chunkrenderdispatcher$chunkrender != null)
                    {
                        chunkrenderdispatcher$chunkrender.renderChunkNeighboursUpated = false;
                    }
                }

                this.chunk = null;
                this.boundingBoxParent = null;
            }
        }

        protected double getDistanceSq()
        {
            ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
            double d0 = this.boundingBox.minX + 8.0D - activerenderinfo.getProjectedView().x;
            double d1 = this.boundingBox.minY + 8.0D - activerenderinfo.getProjectedView().y;
            double d2 = this.boundingBox.minZ + 8.0D - activerenderinfo.getProjectedView().z;
            return d0 * d0 + d1 * d1 + d2 * d2;
        }

        private void beginLayer(BufferBuilder bufferBuilderIn)
        {
            bufferBuilderIn.begin(7, DefaultVertexFormats.BLOCK);
        }

        public ChunkRenderDispatcher.CompiledChunk getCompiledChunk()
        {
            return this.compiledChunk.get();
        }

        private void stopCompileTask()
        {
            this.stopTasks();
            this.compiledChunk.set(ChunkRenderDispatcher.CompiledChunk.DUMMY);
            this.needsUpdate = true;
        }

        public void deleteGlResources()
        {
            this.stopCompileTask();
            this.vertexBuffers.values().forEach(VertexBuffer::close);
        }

        public BlockPos getPosition()
        {
            return this.position;
        }

        public void setNeedsUpdate(boolean immediate)
        {
            boolean flag = this.needsUpdate;
            this.needsUpdate = true;
            this.needsImmediateUpdate = immediate | (flag && this.needsImmediateUpdate);

            if (this.isWorldPlayerUpdate())
            {
                this.playerUpdate = true;
            }
        }

        public void clearNeedsUpdate()
        {
            this.needsUpdate = false;
            this.needsImmediateUpdate = false;
            this.playerUpdate = false;
        }

        public boolean needsUpdate()
        {
            return this.needsUpdate;
        }

        public boolean needsImmediateUpdate()
        {
            return this.needsUpdate && this.needsImmediateUpdate;
        }

        public BlockPos getBlockPosOffset16(Direction facing)
        {
            return this.mapEnumFacing[facing.ordinal()];
        }

        public boolean resortTransparency(RenderType renderTypeIn, ChunkRenderDispatcher renderDispatcherIn)
        {
            ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = this.getCompiledChunk();

            if (this.lastResortTransparencyTask != null)
            {
                this.lastResortTransparencyTask.cancel();
            }

            if (!chunkrenderdispatcher$compiledchunk.layersStarted.contains(renderTypeIn))
            {
                return false;
            }
            else
            {
                if (ChunkRenderDispatcher.FORGE)
                {
                    this.lastResortTransparencyTask = new ChunkRenderDispatcher.ChunkRender.SortTransparencyTask(new ChunkPos(this.getPosition()), this.getDistanceSq(), chunkrenderdispatcher$compiledchunk);
                }
                else
                {
                    this.lastResortTransparencyTask = new ChunkRenderDispatcher.ChunkRender.SortTransparencyTask(this.getDistanceSq(), chunkrenderdispatcher$compiledchunk);
                }

                renderDispatcherIn.schedule(this.lastResortTransparencyTask);
                return true;
            }
        }

        protected void stopTasks()
        {
            if (this.lastRebuildTask != null)
            {
                this.lastRebuildTask.cancel();
                this.lastRebuildTask = null;
            }

            if (this.lastResortTransparencyTask != null)
            {
                this.lastResortTransparencyTask.cancel();
                this.lastResortTransparencyTask = null;
            }
        }

        public ChunkRenderDispatcher.ChunkRender.ChunkRenderTask makeCompileTaskChunk()
        {
            this.stopTasks();
            BlockPos blockpos = this.position.toImmutable();
            int i = 1;
            ChunkRenderCache chunkrendercache = null;

            if (ChunkRenderDispatcher.FORGE)
            {
                this.lastRebuildTask = new ChunkRenderDispatcher.ChunkRender.RebuildTask(new ChunkPos(this.getPosition()), this.getDistanceSq(), chunkrendercache);
            }
            else
            {
                this.lastRebuildTask = new ChunkRenderDispatcher.ChunkRender.RebuildTask(this.getDistanceSq(), chunkrendercache);
            }

            return this.lastRebuildTask;
        }

        public void rebuildChunkLater(ChunkRenderDispatcher dispatcherIn)
        {
            ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.makeCompileTaskChunk();
            dispatcherIn.schedule(chunkrenderdispatcher$chunkrender$chunkrendertask);
        }

        private void updateGlobalTileEntities(Set<TileEntity> globalEntitiesIn)
        {
            Set<TileEntity> set = Sets.newHashSet(globalEntitiesIn);
            Set<TileEntity> set1 = Sets.newHashSet(this.globalTileEntities);
            set.removeAll(this.globalTileEntities);
            set1.removeAll(globalEntitiesIn);
            this.globalTileEntities.clear();
            this.globalTileEntities.addAll(globalEntitiesIn);
            ChunkRenderDispatcher.this.worldRenderer.updateTileEntities(set1, set);
        }

        public void rebuildChunk()
        {
            ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.makeCompileTaskChunk();
            chunkrenderdispatcher$chunkrender$chunkrendertask.execute(ChunkRenderDispatcher.this.fixedBuilder);
        }

        private boolean isWorldPlayerUpdate()
        {
            if (ChunkRenderDispatcher.this.world instanceof ClientWorld)
            {
                ClientWorld clientworld = (ClientWorld)ChunkRenderDispatcher.this.world;
                return clientworld.isPlayerUpdate();
            }
            else
            {
                return false;
            }
        }

        public boolean isPlayerUpdate()
        {
            return this.playerUpdate;
        }

        private RenderType[] getFluidRenderLayers(FluidState p_getFluidRenderLayers_1_, RenderType[] p_getFluidRenderLayers_2_)
        {
            if (ChunkRenderDispatcher.FORGE_CAN_RENDER_IN_LAYER_FS)
            {
                return ChunkRenderDispatcher.BLOCK_RENDER_LAYERS;
            }
            else
            {
                p_getFluidRenderLayers_2_[0] = RenderTypeLookup.getRenderType(p_getFluidRenderLayers_1_);
                return p_getFluidRenderLayers_2_;
            }
        }

        private RenderType[] getBlockRenderLayers(BlockState p_getBlockRenderLayers_1_, RenderType[] p_getBlockRenderLayers_2_)
        {
            if (ChunkRenderDispatcher.FORGE_CAN_RENDER_IN_LAYER_BS)
            {
                return ChunkRenderDispatcher.BLOCK_RENDER_LAYERS;
            }
            else
            {
                p_getBlockRenderLayers_2_[0] = RenderTypeLookup.getChunkRenderType(p_getBlockRenderLayers_1_);
                return p_getBlockRenderLayers_2_;
            }
        }

        private RenderType fixBlockLayer(IBlockReader p_fixBlockLayer_1_, BlockState p_fixBlockLayer_2_, BlockPos p_fixBlockLayer_3_, RenderType p_fixBlockLayer_4_)
        {
            if (CustomBlockLayers.isActive())
            {
                RenderType rendertype = CustomBlockLayers.getRenderLayer(p_fixBlockLayer_1_, p_fixBlockLayer_2_, p_fixBlockLayer_3_);

                if (rendertype != null)
                {
                    return rendertype;
                }
            }

            if (!this.fixBlockLayer)
            {
                return p_fixBlockLayer_4_;
            }
            else
            {
                if (this.isMipmaps)
                {
                    if (p_fixBlockLayer_4_ == RenderTypes.CUTOUT)
                    {
                        Block block = p_fixBlockLayer_2_.getBlock();

                        if (block instanceof RedstoneWireBlock)
                        {
                            return p_fixBlockLayer_4_;
                        }

                        if (block instanceof CactusBlock)
                        {
                            return p_fixBlockLayer_4_;
                        }

                        return RenderTypes.CUTOUT_MIPPED;
                    }
                }
                else if (p_fixBlockLayer_4_ == RenderTypes.CUTOUT_MIPPED)
                {
                    return RenderTypes.CUTOUT;
                }

                return p_fixBlockLayer_4_;
            }
        }

        private void postRenderOverlays(RegionRenderCacheBuilder p_postRenderOverlays_1_, ChunkRenderDispatcher.CompiledChunk p_postRenderOverlays_2_)
        {
            this.postRenderOverlay(RenderTypes.CUTOUT, p_postRenderOverlays_1_, p_postRenderOverlays_2_);
            this.postRenderOverlay(RenderTypes.CUTOUT_MIPPED, p_postRenderOverlays_1_, p_postRenderOverlays_2_);
            this.postRenderOverlay(RenderTypes.TRANSLUCENT, p_postRenderOverlays_1_, p_postRenderOverlays_2_);
        }

        private void postRenderOverlay(RenderType p_postRenderOverlay_1_, RegionRenderCacheBuilder p_postRenderOverlay_2_, ChunkRenderDispatcher.CompiledChunk p_postRenderOverlay_3_)
        {
            BufferBuilder bufferbuilder = p_postRenderOverlay_2_.getBuilder(p_postRenderOverlay_1_);

            if (bufferbuilder.isDrawing())
            {
                p_postRenderOverlay_3_.setLayerStarted(p_postRenderOverlay_1_);

                if (bufferbuilder.getVertexCount() > 0)
                {
                    p_postRenderOverlay_3_.setLayerUsed(p_postRenderOverlay_1_);
                }
            }
        }

        private ChunkCacheOF makeChunkCacheOF(BlockPos p_makeChunkCacheOF_1_)
        {
            BlockPos blockpos = p_makeChunkCacheOF_1_.add(-1, -1, -1);
            BlockPos blockpos1 = p_makeChunkCacheOF_1_.add(16, 16, 16);
            ChunkRenderCache chunkrendercache = this.createRegionRenderCache(ChunkRenderDispatcher.this.world, blockpos, blockpos1, 1);
            return new ChunkCacheOF(chunkrendercache, blockpos, blockpos1, 1);
        }

        public ChunkRenderCache createRegionRenderCache(World p_createRegionRenderCache_1_, BlockPos p_createRegionRenderCache_2_, BlockPos p_createRegionRenderCache_3_, int p_createRegionRenderCache_4_)
        {
            return ChunkRenderCache.generateCache(p_createRegionRenderCache_1_, p_createRegionRenderCache_2_, p_createRegionRenderCache_3_, p_createRegionRenderCache_4_, false);
        }

        public ChunkRenderDispatcher.ChunkRender getRenderChunkOffset16(ViewFrustum p_getRenderChunkOffset16_1_, Direction p_getRenderChunkOffset16_2_)
        {
            if (!this.renderChunksOffset16Updated)
            {
                for (int i = 0; i < Direction.VALUES.length; ++i)
                {
                    Direction direction = Direction.VALUES[i];
                    BlockPos blockpos = this.getBlockPosOffset16(direction);
                    this.renderChunksOfset16[i] = p_getRenderChunkOffset16_1_.getRenderChunk(blockpos);
                }

                this.renderChunksOffset16Updated = true;
            }

            return this.renderChunksOfset16[p_getRenderChunkOffset16_2_.ordinal()];
        }

        public Chunk getChunk()
        {
            return this.getChunk(this.position);
        }

        private Chunk getChunk(BlockPos p_getChunk_1_)
        {
            Chunk chunk = this.chunk;

            if (chunk != null && ChunkUtils.isLoaded(chunk))
            {
                return chunk;
            }
            else
            {
                chunk = ChunkRenderDispatcher.this.world.getChunkAt(p_getChunk_1_);
                this.chunk = chunk;
                return chunk;
            }
        }

        public boolean isChunkRegionEmpty()
        {
            return this.isChunkRegionEmpty(this.position);
        }

        private boolean isChunkRegionEmpty(BlockPos p_isChunkRegionEmpty_1_)
        {
            int i = p_isChunkRegionEmpty_1_.getY();
            int j = i + 15;
            return this.getChunk(p_isChunkRegionEmpty_1_).isEmptyBetween(i, j);
        }

        public void setRenderChunkNeighbour(Direction p_setRenderChunkNeighbour_1_, ChunkRenderDispatcher.ChunkRender p_setRenderChunkNeighbour_2_)
        {
            this.renderChunkNeighbours[p_setRenderChunkNeighbour_1_.ordinal()] = p_setRenderChunkNeighbour_2_;
            this.renderChunkNeighboursValid[p_setRenderChunkNeighbour_1_.ordinal()] = p_setRenderChunkNeighbour_2_;
        }

        public ChunkRenderDispatcher.ChunkRender getRenderChunkNeighbour(Direction p_getRenderChunkNeighbour_1_)
        {
            if (!this.renderChunkNeighboursUpated)
            {
                this.updateRenderChunkNeighboursValid();
            }

            return this.renderChunkNeighboursValid[p_getRenderChunkNeighbour_1_.ordinal()];
        }

        public WorldRenderer.LocalRenderInformationContainer getRenderInfo()
        {
            return this.renderInfo;
        }

        private void updateRenderChunkNeighboursValid()
        {
            int i = this.getPosition().getX();
            int j = this.getPosition().getZ();
            int k = Direction.NORTH.ordinal();
            int l = Direction.SOUTH.ordinal();
            int i1 = Direction.WEST.ordinal();
            int j1 = Direction.EAST.ordinal();
            this.renderChunkNeighboursValid[k] = this.renderChunkNeighbours[k].getPosition().getZ() == j - 16 ? this.renderChunkNeighbours[k] : null;
            this.renderChunkNeighboursValid[l] = this.renderChunkNeighbours[l].getPosition().getZ() == j + 16 ? this.renderChunkNeighbours[l] : null;
            this.renderChunkNeighboursValid[i1] = this.renderChunkNeighbours[i1].getPosition().getX() == i - 16 ? this.renderChunkNeighbours[i1] : null;
            this.renderChunkNeighboursValid[j1] = this.renderChunkNeighbours[j1].getPosition().getX() == i + 16 ? this.renderChunkNeighbours[j1] : null;
            this.renderChunkNeighboursUpated = true;
        }

        public boolean isBoundingBoxInFrustum(ICamera p_isBoundingBoxInFrustum_1_, int p_isBoundingBoxInFrustum_2_)
        {
            return this.getBoundingBoxParent().isBoundingBoxInFrustumFully(p_isBoundingBoxInFrustum_1_, p_isBoundingBoxInFrustum_2_) ? true : p_isBoundingBoxInFrustum_1_.isBoundingBoxInFrustum(this.boundingBox);
        }

        public AabbFrame getBoundingBoxParent()
        {
            if (this.boundingBoxParent == null)
            {
                BlockPos blockpos = this.getPosition();
                int i = blockpos.getX();
                int j = blockpos.getY();
                int k = blockpos.getZ();
                int l = 5;
                int i1 = i >> l << l;
                int j1 = j >> l << l;
                int k1 = k >> l << l;

                if (i1 != i || j1 != j || k1 != k)
                {
                    AabbFrame aabbframe = ChunkRenderDispatcher.this.worldRenderer.getRenderChunk(new BlockPos(i1, j1, k1)).getBoundingBoxParent();

                    if (aabbframe != null && aabbframe.minX == (double)i1 && aabbframe.minY == (double)j1 && aabbframe.minZ == (double)k1)
                    {
                        this.boundingBoxParent = aabbframe;
                    }
                }

                if (this.boundingBoxParent == null)
                {
                    int l1 = 1 << l;
                    this.boundingBoxParent = new AabbFrame((double)i1, (double)j1, (double)k1, (double)(i1 + l1), (double)(j1 + l1), (double)(k1 + l1));
                }
            }

            return this.boundingBoxParent;
        }

        public String toString()
        {
            return "pos: " + this.getPosition() + ", frameIndex: " + this.frameIndex;
        }

        abstract class ChunkRenderTask implements Comparable<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask>
        {
            protected final double distanceSq;
            protected final AtomicBoolean finished = new AtomicBoolean(false);
            protected Map<BlockPos, IModelData> modelData;

            public ChunkRenderTask(double distanceSqIn)
            {
                this((ChunkPos)null, distanceSqIn);
            }

            public ChunkRenderTask(ChunkPos p_i242119_2_, double p_i242119_3_)
            {
                this.distanceSq = p_i242119_3_;

                if (p_i242119_2_ == null)
                {
                    this.modelData = Collections.emptyMap();
                }
                else
                {
                    this.modelData = ModelDataManager.getModelData(Minecraft.getInstance().world, p_i242119_2_);
                }
            }

            public abstract CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> execute(RegionRenderCacheBuilder builderIn);

            public abstract void cancel();

            public int compareTo(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask p_compareTo_1_)
            {
                return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
            }

            public IModelData getModelData(BlockPos p_getModelData_1_)
            {
                return this.modelData.getOrDefault(p_getModelData_1_, EmptyModelData.INSTANCE);
            }
        }

        class RebuildTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask
        {
            @Nullable
            protected ChunkRenderCache chunkRenderCache;

            public RebuildTask(@Nullable double distanceSqIn, ChunkRenderCache renderCacheIn)
            {
                this((ChunkPos)null, distanceSqIn, renderCacheIn);
            }

            public RebuildTask(ChunkPos p_i242111_2_, @Nullable double p_i242111_3_, ChunkRenderCache p_i242111_5_)
            {
                super(p_i242111_2_, p_i242111_3_);
                this.chunkRenderCache = p_i242111_5_;
            }

            public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> execute(RegionRenderCacheBuilder builderIn)
            {
                if (this.finished.get())
                {
                    return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                }
                else if (!ChunkRender.this.shouldStayLoaded())
                {
                    this.chunkRenderCache = null;
                    ChunkRender.this.setNeedsUpdate(false);
                    this.finished.set(true);
                    return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                }
                else if (this.finished.get())
                {
                    return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                }
                else
                {
                    Vector3d vector3d = ChunkRenderDispatcher.this.getRenderPosition();
                    float f = (float)vector3d.x;
                    float f1 = (float)vector3d.y;
                    float f2 = (float)vector3d.z;
                    ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = new ChunkRenderDispatcher.CompiledChunk();
                    Set<TileEntity> set = this.compile(f, f1, f2, chunkrenderdispatcher$compiledchunk, builderIn);
                    ChunkRender.this.updateGlobalTileEntities(set);

                    if (this.finished.get())
                    {
                        return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                    }
                    else
                    {
                        List<CompletableFuture<Void>> list = Lists.newArrayList();
                        chunkrenderdispatcher$compiledchunk.layersStarted.forEach((p_lambda$execute$0_3_) ->
                        {
                            list.add(ChunkRenderDispatcher.this.uploadChunkLayer(builderIn.getBuilder(p_lambda$execute$0_3_), ChunkRender.this.getVertexBuffer(p_lambda$execute$0_3_)));
                        });
                        return Util.gather(list).handle((p_lambda$execute$1_2_, p_lambda$execute$1_3_) ->
                        {
                            if (p_lambda$execute$1_3_ != null && !(p_lambda$execute$1_3_ instanceof CancellationException) && !(p_lambda$execute$1_3_ instanceof InterruptedException))
                            {
                                Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_lambda$execute$1_3_, "Rendering chunk"));
                            }

                            if (this.finished.get())
                            {
                                return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                            }
                            else {
                                ChunkRender.this.compiledChunk.set(chunkrenderdispatcher$compiledchunk);
                                return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                            }
                        });
                    }
                }
            }

            private Set<TileEntity> compile(float xIn, float yIn, float zIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn, RegionRenderCacheBuilder builderIn)
            {
                int i = 1;
                BlockPos blockpos = ChunkRender.this.position.toImmutable();
                BlockPos blockpos1 = blockpos.add(15, 15, 15);
                VisGraph visgraph = new VisGraph();
                Set<TileEntity> set = Sets.newHashSet();
                this.chunkRenderCache = null;
                MatrixStack matrixstack = new MatrixStack();

                if (!ChunkRender.this.isChunkRegionEmpty(blockpos))
                {
                    ++ChunkRenderDispatcher.renderChunksUpdated;
                    ChunkCacheOF chunkcacheof = ChunkRender.this.makeChunkCacheOF(blockpos);
                    chunkcacheof.renderStart();
                    RenderType[] arendertype = new RenderType[1];
                    boolean flag = Config.isShaders();
                    boolean flag1 = flag && Shaders.useMidBlockAttrib;
                    BlockModelRenderer.enableCache();
                    Random random = new Random();
                    BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

                    for (BlockPosM blockposm : (Iterable<BlockPosM>)BlockPosM.getAllInBoxMutable(blockpos, blockpos1))
                    {
                        BlockState blockstate = chunkcacheof.getBlockState(blockposm);

                        if (!blockstate.isAir())
                        {
                            Block block = blockstate.getBlock();

                            if (blockstate.isOpaqueCube(chunkcacheof, blockposm))
                            {
                                visgraph.setOpaqueCube(blockposm);
                            }

                            if (ReflectorForge.blockHasTileEntity(blockstate))
                            {
                                TileEntity tileentity = chunkcacheof.getTileEntity(blockposm, Chunk.CreateEntityType.CHECK);

                                if (tileentity != null)
                                {
                                    this.handleTileEntity(compiledChunkIn, set, tileentity);
                                }
                            }

                            FluidState fluidstate = blockstate.getFluidState();
                            IModelData imodeldata = ChunkRenderDispatcher.FORGE ? this.getModelData(blockposm) : null;

                            if (!fluidstate.isEmpty())
                            {
                                RenderType[] arendertype1 = ChunkRender.this.getFluidRenderLayers(fluidstate, arendertype);

                                for (int j = 0; j < arendertype1.length; ++j)
                                {
                                    RenderType rendertype = arendertype1[j];

                                    if (!ChunkRenderDispatcher.FORGE_CAN_RENDER_IN_LAYER_FS || Reflector.callBoolean(Reflector.ForgeRenderTypeLookup_canRenderInLayerFs, fluidstate, rendertype))
                                    {
                                        if (ChunkRenderDispatcher.FORGE_SET_RENDER_LAYER)
                                        {
                                            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, rendertype);
                                        }

                                        BufferBuilder bufferbuilder = builderIn.getBuilder(rendertype);
                                        bufferbuilder.setBlockLayer(rendertype);
                                        RenderEnv renderenv = bufferbuilder.getRenderEnv(blockstate, blockposm);
                                        renderenv.setRegionRenderCacheBuilder(builderIn);
                                        chunkcacheof.setRenderEnv(renderenv);

                                        if (compiledChunkIn.layersStarted.add(rendertype))
                                        {
                                            ChunkRender.this.beginLayer(bufferbuilder);
                                        }

                                        if (blockrendererdispatcher.renderFluid(blockposm, chunkcacheof, bufferbuilder, fluidstate))
                                        {
                                            compiledChunkIn.empty = false;
                                            compiledChunkIn.layersUsed.add(rendertype);
                                        }
                                    }
                                }
                            }

                            if (blockstate.getRenderType() != BlockRenderType.INVISIBLE)
                            {
                                RenderType[] arendertype2 = ChunkRender.this.getBlockRenderLayers(blockstate, arendertype);

                                for (int k = 0; k < arendertype2.length; ++k)
                                {
                                    RenderType rendertype3 = arendertype2[k];

                                    if (!ChunkRenderDispatcher.FORGE_CAN_RENDER_IN_LAYER_BS || Reflector.callBoolean(Reflector.ForgeRenderTypeLookup_canRenderInLayerBs, blockstate, rendertype3))
                                    {
                                        if (ChunkRenderDispatcher.FORGE_SET_RENDER_LAYER)
                                        {
                                            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, rendertype3);
                                        }

                                        rendertype3 = ChunkRender.this.fixBlockLayer(chunkcacheof, blockstate, blockposm, rendertype3);
                                        BufferBuilder bufferbuilder3 = builderIn.getBuilder(rendertype3);
                                        bufferbuilder3.setBlockLayer(rendertype3);
                                        RenderEnv renderenv1 = bufferbuilder3.getRenderEnv(blockstate, blockposm);
                                        renderenv1.setRegionRenderCacheBuilder(builderIn);
                                        chunkcacheof.setRenderEnv(renderenv1);

                                        if (compiledChunkIn.layersStarted.add(rendertype3))
                                        {
                                            ChunkRender.this.beginLayer(bufferbuilder3);
                                        }

                                        matrixstack.push();
                                        matrixstack.translate((double)ChunkRender.this.regionDX + (double)(blockposm.getX() & 15), (double)ChunkRender.this.regionDY + (double)(blockposm.getY() & 15), (double)ChunkRender.this.regionDZ + (double)(blockposm.getZ() & 15));

                                        if (flag1)
                                        {
                                            bufferbuilder3.setMidBlock(0.5F + (float)ChunkRender.this.regionDX + (float)(blockposm.getX() & 15), 0.5F + (float)ChunkRender.this.regionDY + (float)(blockposm.getY() & 15), 0.5F + (float)ChunkRender.this.regionDZ + (float)(blockposm.getZ() & 15));
                                        }

                                        if (blockrendererdispatcher.renderModel(blockstate, blockposm, chunkcacheof, matrixstack, bufferbuilder3, true, random, imodeldata))
                                        {
                                            compiledChunkIn.empty = false;
                                            compiledChunkIn.layersUsed.add(rendertype3);

                                            if (renderenv1.isOverlaysRendered())
                                            {
                                                ChunkRender.this.postRenderOverlays(builderIn, compiledChunkIn);
                                                renderenv1.setOverlaysRendered(false);
                                            }
                                        }

                                        matrixstack.pop();
                                    }
                                }
                            }

                            if (ChunkRenderDispatcher.FORGE_SET_RENDER_LAYER)
                            {
                                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderLayer, (Object)null);
                            }
                        }
                    }

                    if (compiledChunkIn.layersUsed.contains(RenderType.getTranslucent()))
                    {
                        BufferBuilder bufferbuilder1 = builderIn.getBuilder(RenderType.getTranslucent());
                        bufferbuilder1.sortVertexData((float)ChunkRender.this.regionDX + xIn - (float)blockpos.getX(), (float)ChunkRender.this.regionDY + yIn - (float)blockpos.getY(), (float)ChunkRender.this.regionDZ + zIn - (float)blockpos.getZ());
                        compiledChunkIn.state = bufferbuilder1.getVertexState();
                    }

                    compiledChunkIn.layersStarted.stream().map(builderIn::getBuilder).forEach(BufferBuilder::finishDrawing);

                    for (RenderType rendertype2 : ChunkRenderDispatcher.BLOCK_RENDER_LAYERS)
                    {
                        compiledChunkIn.setAnimatedSprites(rendertype2, (BitSet)null);
                    }

                    for (RenderType rendertype1 : compiledChunkIn.layersStarted)
                    {
                        if (Config.isShaders())
                        {
                            SVertexBuilder.calcNormalChunkLayer(builderIn.getBuilder(rendertype1));
                        }

                        BufferBuilder bufferbuilder2 = builderIn.getBuilder(rendertype1);

                        if (bufferbuilder2.animatedSprites != null && !bufferbuilder2.animatedSprites.isEmpty())
                        {
                            compiledChunkIn.setAnimatedSprites(rendertype1, (BitSet)bufferbuilder2.animatedSprites.clone());
                        }
                    }

                    chunkcacheof.renderFinish();
                    BlockModelRenderer.disableCache();
                }

                compiledChunkIn.setVisibility = visgraph.computeVisibility();
                return set;
            }

            private <E extends TileEntity> void handleTileEntity(ChunkRenderDispatcher.CompiledChunk compiledChunkIn, Set<TileEntity> tileEntitiesIn, E tileEntityIn)
            {
                TileEntityRenderer<E> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileEntityIn);

                if (tileentityrenderer != null)
                {
                    if (tileentityrenderer.isGlobalRenderer(tileEntityIn))
                    {
                        tileEntitiesIn.add(tileEntityIn);
                    }
                    else
                    {
                        compiledChunkIn.tileEntities.add(tileEntityIn);
                    }
                }
            }

            public void cancel()
            {
                this.chunkRenderCache = null;

                if (this.finished.compareAndSet(false, true))
                {
                    ChunkRender.this.setNeedsUpdate(false);
                }
            }
        }

        class SortTransparencyTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask
        {
            private final ChunkRenderDispatcher.CompiledChunk sortCompiledChunk;

            public SortTransparencyTask(double distanceSqIn, ChunkRenderDispatcher.CompiledChunk compiledChunkIn)
            {
                this((ChunkPos)null, distanceSqIn, compiledChunkIn);
            }

            public SortTransparencyTask(ChunkPos p_i242104_2_, double p_i242104_3_, ChunkRenderDispatcher.CompiledChunk p_i242104_5_)
            {
                super(p_i242104_2_, p_i242104_3_);
                this.sortCompiledChunk = p_i242104_5_;
            }

            public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> execute(RegionRenderCacheBuilder builderIn)
            {
                if (this.finished.get())
                {
                    return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                }
                else if (!ChunkRender.this.shouldStayLoaded())
                {
                    this.finished.set(true);
                    return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                }
                else if (this.finished.get())
                {
                    return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                }
                else
                {
                    Vector3d vector3d = ChunkRenderDispatcher.this.getRenderPosition();
                    float f = (float)vector3d.x;
                    float f1 = (float)vector3d.y;
                    float f2 = (float)vector3d.z;
                    BufferBuilder.State bufferbuilder$state = this.sortCompiledChunk.state;

                    if (bufferbuilder$state != null && this.sortCompiledChunk.layersUsed.contains(RenderType.getTranslucent()))
                    {
                        BufferBuilder bufferbuilder = builderIn.getBuilder(RenderType.getTranslucent());
                        bufferbuilder.setBlockLayer(RenderType.getTranslucent());
                        ChunkRender.this.beginLayer(bufferbuilder);
                        bufferbuilder.setVertexState(bufferbuilder$state);
                        bufferbuilder.sortVertexData((float)ChunkRender.this.regionDX + f - (float)ChunkRender.this.position.getX(), (float)ChunkRender.this.regionDY + f1 - (float)ChunkRender.this.position.getY(), (float)ChunkRender.this.regionDZ + f2 - (float)ChunkRender.this.position.getZ());
                        this.sortCompiledChunk.state = bufferbuilder.getVertexState();
                        bufferbuilder.finishDrawing();

                        if (this.finished.get())
                        {
                            return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                        }
                        else
                        {
                            CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> completablefuture = ChunkRenderDispatcher.this.uploadChunkLayer(builderIn.getBuilder(RenderType.getTranslucent()), ChunkRender.this.getVertexBuffer(RenderType.getTranslucent())).thenApply((p_lambda$execute$0_0_) ->
                            {
                                return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                            });
                            return completablefuture.handle((p_lambda$execute$1_1_, p_lambda$execute$1_2_) ->
                            {
                                if (p_lambda$execute$1_2_ != null && !(p_lambda$execute$1_2_ instanceof CancellationException) && !(p_lambda$execute$1_2_ instanceof InterruptedException))
                                {
                                    Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_lambda$execute$1_2_, "Rendering chunk"));
                                }

                                return this.finished.get() ? ChunkRenderDispatcher.ChunkTaskResult.CANCELLED : ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                            });
                        }
                    }
                    else
                    {
                        return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                    }
                }
            }

            public void cancel()
            {
                this.finished.set(true);
            }
        }
    }

    static enum ChunkTaskResult
    {
        SUCCESSFUL,
        CANCELLED;
    }

    public static class CompiledChunk
    {
        public static final ChunkRenderDispatcher.CompiledChunk DUMMY = new ChunkRenderDispatcher.CompiledChunk()
        {
            public boolean isVisible(Direction facing, Direction facing2)
            {
                return false;
            }
            public void setAnimatedSprites(RenderType p_setAnimatedSprites_1_, BitSet p_setAnimatedSprites_2_)
            {
                throw new UnsupportedOperationException();
            }
        };
        private final ChunkLayerSet layersUsed = new ChunkLayerSet();
        private final Set<RenderType> layersStarted = new ObjectArraySet<>();
        private boolean empty = true;
        private final List<TileEntity> tileEntities = Lists.newArrayList();
        private SetVisibility setVisibility = new SetVisibility();
        @Nullable
        private BufferBuilder.State state;
        private BitSet[] animatedSprites = new BitSet[RenderType.CHUNK_RENDER_TYPES.length];

        public boolean isEmpty()
        {
            return this.empty;
        }

        public boolean isLayerEmpty(RenderType renderTypeIn)
        {
            return !this.layersUsed.contains(renderTypeIn);
        }

        public List<TileEntity> getTileEntities()
        {
            return this.tileEntities;
        }

        public boolean isVisible(Direction facing, Direction facing2)
        {
            return this.setVisibility.isVisible(facing, facing2);
        }

        public BitSet getAnimatedSprites(RenderType p_getAnimatedSprites_1_)
        {
            return this.animatedSprites[p_getAnimatedSprites_1_.ordinal()];
        }

        public void setAnimatedSprites(RenderType p_setAnimatedSprites_1_, BitSet p_setAnimatedSprites_2_)
        {
            this.animatedSprites[p_setAnimatedSprites_1_.ordinal()] = p_setAnimatedSprites_2_;
        }

        public boolean isLayerStarted(RenderType p_isLayerStarted_1_)
        {
            return this.layersStarted.contains(p_isLayerStarted_1_);
        }

        public void setLayerStarted(RenderType p_setLayerStarted_1_)
        {
            this.layersStarted.add(p_setLayerStarted_1_);
        }

        public void setLayerUsed(RenderType p_setLayerUsed_1_)
        {
            this.layersUsed.add(p_setLayerUsed_1_);
        }
    }
}
