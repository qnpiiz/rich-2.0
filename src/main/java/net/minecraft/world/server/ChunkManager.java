package net.minecraft.world.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.TrackedEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkTaskPriorityQueue;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.PlayerGenerationTracker;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.ChunkSerializer;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.optifine.reflect.Reflector;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkManager extends ChunkLoader implements ChunkHolder.IPlayerProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_LOADED_LEVEL = 33 + ChunkStatus.maxDistance();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> loadedChunks = new Long2ObjectLinkedOpenHashMap<>();
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> immutableLoadedChunks = this.loadedChunks.clone();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> chunksToUnload = new Long2ObjectLinkedOpenHashMap<>();

    /**
     * Chunk positions in this set have fully loaded and their TE's and entities are accessible from the world
     */
    private final LongSet loadedPositions = new LongOpenHashSet();
    private final ServerWorld world;
    private final ServerWorldLightManager lightManager;
    private final ThreadTaskExecutor<Runnable> mainThread;
    private final ChunkGenerator generator;
    private final Supplier<DimensionSavedDataManager> field_219259_m;
    private final PointOfInterestManager pointOfInterestManager;

    /**
     * Chunks that have been requested to be unloaded, but haven't been unloaded yet.
     */
    private final LongSet unloadableChunks = new LongOpenHashSet();

    /**
     * True if changes have been made to {@link #loadedChunks} and thus a new copy of the collection has to be made into
     * {@link #immutableLoadedChunks}.
     */
    private boolean immutableLoadedChunksDirty;
    private final ChunkTaskPriorityQueueSorter field_219263_q;
    private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219264_r;
    private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_219265_s;
    private final IChunkStatusListener field_219266_t;
    private final ChunkManager.ProxyTicketManager ticketManager;
    private final AtomicInteger field_219268_v = new AtomicInteger();
    private final TemplateManager templateManager;
    private final File dimensionDirectory;
    private final PlayerGenerationTracker playerGenerationTracker = new PlayerGenerationTracker();
    private final Int2ObjectMap<ChunkManager.EntityTracker> entities = new Int2ObjectOpenHashMap<>();
    private final Long2ByteMap field_241087_z_ = new Long2ByteOpenHashMap();
    private final Queue<Runnable> saveTasks = Queues.newConcurrentLinkedQueue();
    private int viewDistance;

    public ChunkManager(ServerWorld p_i232602_1_, SaveFormat.LevelSave p_i232602_2_, DataFixer p_i232602_3_, TemplateManager p_i232602_4_, Executor p_i232602_5_, ThreadTaskExecutor<Runnable> p_i232602_6_, IChunkLightProvider p_i232602_7_, ChunkGenerator p_i232602_8_, IChunkStatusListener p_i232602_9_, Supplier<DimensionSavedDataManager> p_i232602_10_, int p_i232602_11_, boolean p_i232602_12_)
    {
        super(new File(p_i232602_2_.getDimensionFolder(p_i232602_1_.getDimensionKey()), "region"), p_i232602_3_, p_i232602_12_);
        this.templateManager = p_i232602_4_;
        this.dimensionDirectory = p_i232602_2_.getDimensionFolder(p_i232602_1_.getDimensionKey());
        this.world = p_i232602_1_;
        this.generator = p_i232602_8_;
        this.mainThread = p_i232602_6_;
        DelegatedTaskExecutor<Runnable> delegatedtaskexecutor = DelegatedTaskExecutor.create(p_i232602_5_, "worldgen");
        ITaskExecutor<Runnable> itaskexecutor = ITaskExecutor.inline("main", p_i232602_6_::enqueue);
        this.field_219266_t = p_i232602_9_;
        DelegatedTaskExecutor<Runnable> delegatedtaskexecutor1 = DelegatedTaskExecutor.create(p_i232602_5_, "light");
        this.field_219263_q = new ChunkTaskPriorityQueueSorter(ImmutableList.of(delegatedtaskexecutor, itaskexecutor, delegatedtaskexecutor1), p_i232602_5_, Integer.MAX_VALUE);
        this.field_219264_r = this.field_219263_q.func_219087_a(delegatedtaskexecutor, false);
        this.field_219265_s = this.field_219263_q.func_219087_a(itaskexecutor, false);
        this.lightManager = new ServerWorldLightManager(p_i232602_7_, this, this.world.getDimensionType().hasSkyLight(), delegatedtaskexecutor1, this.field_219263_q.func_219087_a(delegatedtaskexecutor1, false));
        this.ticketManager = new ChunkManager.ProxyTicketManager(p_i232602_5_, p_i232602_6_);
        this.field_219259_m = p_i232602_10_;
        this.pointOfInterestManager = new PointOfInterestManager(new File(this.dimensionDirectory, "poi"), p_i232602_3_, p_i232602_12_);
        this.setViewDistance(p_i232602_11_);
    }

    /**
     * Returns the squared distance to the center of the chunk.
     */
    private static double getDistanceSquaredToChunk(ChunkPos chunkPosIn, Entity entityIn)
    {
        double d0 = (double)(chunkPosIn.x * 16 + 8);
        double d1 = (double)(chunkPosIn.z * 16 + 8);
        double d2 = d0 - entityIn.getPosX();
        double d3 = d1 - entityIn.getPosZ();
        return d2 * d2 + d3 * d3;
    }

    private static int func_219215_b(ChunkPos pos, ServerPlayerEntity player, boolean p_219215_2_)
    {
        int i;
        int j;

        if (p_219215_2_)
        {
            SectionPos sectionpos = player.getManagedSectionPos();
            i = sectionpos.getSectionX();
            j = sectionpos.getSectionZ();
        }
        else
        {
            i = MathHelper.floor(player.getPosX() / 16.0D);
            j = MathHelper.floor(player.getPosZ() / 16.0D);
        }

        return getChunkDistance(pos, i, j);
    }

    private static int getChunkDistance(ChunkPos chunkPosIn, int x, int y)
    {
        int i = chunkPosIn.x - x;
        int j = chunkPosIn.z - y;
        return Math.max(Math.abs(i), Math.abs(j));
    }

    protected ServerWorldLightManager getLightManager()
    {
        return this.lightManager;
    }

    @Nullable
    protected ChunkHolder func_219220_a(long chunkPosIn)
    {
        return this.loadedChunks.get(chunkPosIn);
    }

    @Nullable
    protected ChunkHolder func_219219_b(long chunkPosIn)
    {
        return this.immutableLoadedChunks.get(chunkPosIn);
    }

    protected IntSupplier func_219191_c(long chunkPosIn)
    {
        return () ->
        {
            ChunkHolder chunkholder = this.func_219219_b(chunkPosIn);
            return chunkholder == null ? ChunkTaskPriorityQueue.MAX_LOADED_LEVELS - 1 : Math.min(chunkholder.func_219281_j(), ChunkTaskPriorityQueue.MAX_LOADED_LEVELS - 1);
        };
    }

    public String getDebugInfo(ChunkPos pos)
    {
        ChunkHolder chunkholder = this.func_219219_b(pos.asLong());

        if (chunkholder == null)
        {
            return "null";
        }
        else
        {
            String s = chunkholder.getChunkLevel() + "\n";
            ChunkStatus chunkstatus = chunkholder.func_219285_d();
            IChunk ichunk = chunkholder.func_219287_e();

            if (chunkstatus != null)
            {
                s = s + "St: \u00a7" + chunkstatus.ordinal() + chunkstatus + '\u00a7' + "r\n";
            }

            if (ichunk != null)
            {
                s = s + "Ch: \u00a7" + ichunk.getStatus().ordinal() + ichunk.getStatus() + '\u00a7' + "r\n";
            }

            ChunkHolder.LocationType chunkholder$locationtype = chunkholder.func_219300_g();
            s = s + "\u00a7" + chunkholder$locationtype.ordinal() + chunkholder$locationtype;
            return s + '\u00a7' + "r";
        }
    }

    private CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> func_219236_a(ChunkPos pos, int p_219236_2_, IntFunction<ChunkStatus> p_219236_3_)
    {
        List<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> list = Lists.newArrayList();
        int i = pos.x;
        int j = pos.z;

        for (int k = -p_219236_2_; k <= p_219236_2_; ++k)
        {
            for (int l = -p_219236_2_; l <= p_219236_2_; ++l)
            {
                int i1 = Math.max(Math.abs(l), Math.abs(k));
                final ChunkPos chunkpos = new ChunkPos(i + l, j + k);
                long j1 = chunkpos.asLong();
                ChunkHolder chunkholder = this.func_219220_a(j1);

                if (chunkholder == null)
                {
                    return CompletableFuture.completedFuture(Either.right(new ChunkHolder.IChunkLoadingError()
                    {
                        public String toString()
                        {
                            return "Unloaded " + chunkpos.toString();
                        }
                    }));
                }

                ChunkStatus chunkstatus = p_219236_3_.apply(i1);
                CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = chunkholder.func_219276_a(chunkstatus, this);
                list.add(completablefuture);
            }
        }

        CompletableFuture<List<Either<IChunk, ChunkHolder.IChunkLoadingError>>> completablefuture1 = Util.gather(list);
        return completablefuture1.thenApply((p_lambda$func_219236_a$1_4_) ->
        {
            List<IChunk> list1 = Lists.newArrayList();
            int k1 = 0;

            for (final Either<IChunk, ChunkHolder.IChunkLoadingError> either : p_lambda$func_219236_a$1_4_)
            {
                Optional<IChunk> optional = either.left();

                if (!optional.isPresent())
                {
                    final int l1 = k1;
                    return Either.right(new ChunkHolder.IChunkLoadingError()
                    {
                        public String toString()
                        {
                            return "Unloaded " + new ChunkPos(i + l1 % (p_219236_2_ * 2 + 1), j + l1 / (p_219236_2_ * 2 + 1)) + " " + either.right().get().toString();
                        }
                    });
                }

                list1.add(optional.get());
                ++k1;
            }

            return Either.left(list1);
        });
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219188_b(ChunkPos p_219188_1_)
    {
        return this.func_219236_a(p_219188_1_, 2, (p_lambda$func_219188_b$2_0_) ->
        {
            return ChunkStatus.FULL;
        }).thenApplyAsync((p_lambda$func_219188_b$4_0_) ->
        {
            return p_lambda$func_219188_b$4_0_.mapLeft((p_lambda$null$3_0_) -> {
                return (Chunk)p_lambda$null$3_0_.get(p_lambda$null$3_0_.size() / 2);
            });
        }, this.mainThread);
    }

    @Nullable

    /**
     * Sets level and loads/unloads chunk. Used by {@link net.minecraft.world.server.ChunkManager.ProxyTicketManager} to
     * set chunk level.
     *  
     * @param holder The {@link net.minecraft.world.server.ChunkHolder} of the chunk if it is loaded, and null
     * otherwise.
     */
    private ChunkHolder setChunkLevel(long chunkPosIn, int newLevel, @Nullable ChunkHolder holder, int oldLevel)
    {
        if (oldLevel > MAX_LOADED_LEVEL && newLevel > MAX_LOADED_LEVEL)
        {
            return holder;
        }
        else
        {
            if (holder != null)
            {
                holder.setChunkLevel(newLevel);
            }

            if (holder != null)
            {
                if (newLevel > MAX_LOADED_LEVEL)
                {
                    this.unloadableChunks.add(chunkPosIn);
                }
                else
                {
                    this.unloadableChunks.remove(chunkPosIn);
                }
            }

            if (newLevel <= MAX_LOADED_LEVEL && holder == null)
            {
                holder = this.chunksToUnload.remove(chunkPosIn);

                if (holder != null)
                {
                    holder.setChunkLevel(newLevel);
                }
                else
                {
                    holder = new ChunkHolder(new ChunkPos(chunkPosIn), newLevel, this.lightManager, this.field_219263_q, this);
                }

                this.loadedChunks.put(chunkPosIn, holder);
                this.immutableLoadedChunksDirty = true;
            }

            return holder;
        }
    }

    public void close() throws IOException
    {
        try
        {
            this.field_219263_q.close();
            this.pointOfInterestManager.close();
        }
        finally
        {
            super.close();
        }
    }

    protected void save(boolean flush)
    {
        if (flush)
        {
            List<ChunkHolder> list = this.immutableLoadedChunks.values().stream().filter(ChunkHolder::isAccessible).peek(ChunkHolder::updateAccessible).collect(Collectors.toList());
            MutableBoolean mutableboolean = new MutableBoolean();

            do
            {
                mutableboolean.setFalse();
                list.stream().map((p_lambda$save$5_1_) ->
                {
                    CompletableFuture<IChunk> completablefuture;

                    do {
                        completablefuture = p_lambda$save$5_1_.func_219302_f();
                        this.mainThread.driveUntil(completablefuture::isDone);
                    }
                    while (completablefuture != p_lambda$save$5_1_.func_219302_f());

                    return completablefuture.join();
                }).filter((p_lambda$save$6_0_) ->
                {
                    return p_lambda$save$6_0_ instanceof ChunkPrimerWrapper || p_lambda$save$6_0_ instanceof Chunk;
                }).filter(this::chunkSave).forEach((p_lambda$save$7_1_) ->
                {
                    mutableboolean.setTrue();
                });
            }
            while (mutableboolean.isTrue());

            this.scheduleUnloads(() ->
            {
                return true;
            });
            this.func_227079_i_();
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.dimensionDirectory.getName());
        }
        else
        {
            this.immutableLoadedChunks.values().stream().filter(ChunkHolder::isAccessible).forEach((p_lambda$save$9_1_) ->
            {
                IChunk ichunk = p_lambda$save$9_1_.func_219302_f().getNow((IChunk)null);

                if (ichunk instanceof ChunkPrimerWrapper || ichunk instanceof Chunk)
                {
                    this.chunkSave(ichunk);
                    p_lambda$save$9_1_.updateAccessible();
                }
            });
        }
    }

    protected void tick(BooleanSupplier hasMoreTime)
    {
        IProfiler iprofiler = this.world.getProfiler();
        iprofiler.startSection("poi");
        this.pointOfInterestManager.tick(hasMoreTime);
        iprofiler.endStartSection("chunk_unload");

        if (!this.world.isSaveDisabled())
        {
            this.scheduleUnloads(hasMoreTime);
        }

        iprofiler.endSection();
    }

    private void scheduleUnloads(BooleanSupplier hasMoreTime)
    {
        LongIterator longiterator = this.unloadableChunks.iterator();

        for (int i = 0; longiterator.hasNext() && (hasMoreTime.getAsBoolean() || i < 200 || this.unloadableChunks.size() > 2000); longiterator.remove())
        {
            long j = longiterator.nextLong();
            ChunkHolder chunkholder = this.loadedChunks.remove(j);

            if (chunkholder != null)
            {
                this.chunksToUnload.put(j, chunkholder);
                this.immutableLoadedChunksDirty = true;
                ++i;
                this.scheduleSave(j, chunkholder);
            }
        }

        Runnable runnable;

        while ((hasMoreTime.getAsBoolean() || this.saveTasks.size() > 2000) && (runnable = this.saveTasks.poll()) != null)
        {
            runnable.run();
        }
    }

    private void scheduleSave(long chunkPosIn, ChunkHolder chunkHolderIn)
    {
        CompletableFuture<IChunk> completablefuture = chunkHolderIn.func_219302_f();
        completablefuture.thenAcceptAsync((p_lambda$scheduleSave$10_5_) ->
        {
            CompletableFuture<IChunk> completablefuture1 = chunkHolderIn.func_219302_f();

            if (completablefuture1 != completablefuture)
            {
                this.scheduleSave(chunkPosIn, chunkHolderIn);
            }
            else if (this.chunksToUnload.remove(chunkPosIn, chunkHolderIn) && p_lambda$scheduleSave$10_5_ != null)
            {
                if (p_lambda$scheduleSave$10_5_ instanceof Chunk)
                {
                    ((Chunk)p_lambda$scheduleSave$10_5_).setLoaded(false);

                    if (Reflector.ChunkEvent_Unload_Constructor.exists())
                    {
                        Reflector.postForgeBusEvent(Reflector.ChunkEvent_Unload_Constructor, p_lambda$scheduleSave$10_5_);
                    }
                }

                this.chunkSave(p_lambda$scheduleSave$10_5_);

                if (this.loadedPositions.remove(chunkPosIn) && p_lambda$scheduleSave$10_5_ instanceof Chunk)
                {
                    Chunk chunk = (Chunk)p_lambda$scheduleSave$10_5_;
                    this.world.onChunkUnloading(chunk);
                }

                this.lightManager.updateChunkStatus(p_lambda$scheduleSave$10_5_.getPos());
                this.lightManager.func_215588_z_();
                this.field_219266_t.statusChanged(p_lambda$scheduleSave$10_5_.getPos(), (ChunkStatus)null);
            }
        }, this.saveTasks::add).whenComplete((p_lambda$scheduleSave$11_1_, p_lambda$scheduleSave$11_2_) ->
        {
            if (p_lambda$scheduleSave$11_2_ != null)
            {
                LOGGER.error("Failed to save chunk " + chunkHolderIn.getPosition(), p_lambda$scheduleSave$11_2_);
            }
        });
    }

    protected boolean refreshOffThreadCache()
    {
        if (!this.immutableLoadedChunksDirty)
        {
            return false;
        }
        else
        {
            this.immutableLoadedChunks = this.loadedChunks.clone();
            this.immutableLoadedChunksDirty = false;
            return true;
        }
    }

    public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219244_a(ChunkHolder chunkHolder, ChunkStatus chunkStatus)
    {
        ChunkPos chunkpos = chunkHolder.getPosition();

        if (chunkStatus == ChunkStatus.EMPTY)
        {
            return this.chunkLoad(chunkpos);
        }
        else
        {
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = chunkHolder.func_219276_a(chunkStatus.getParent(), this);
            return completablefuture.thenComposeAsync((p_lambda$func_219244_a$13_4_) ->
            {
                Optional<IChunk> optional = p_lambda$func_219244_a$13_4_.left();

                if (!optional.isPresent())
                {
                    return CompletableFuture.completedFuture(p_lambda$func_219244_a$13_4_);
                }
                else {
                    if (chunkStatus == ChunkStatus.LIGHT)
                    {
                        this.ticketManager.registerWithLevel(TicketType.LIGHT, chunkpos, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), chunkpos);
                    }

                    IChunk ichunk = optional.get();

                    if (ichunk.getStatus().isAtLeast(chunkStatus))
                    {
                        CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1;

                        if (chunkStatus == ChunkStatus.LIGHT)
                        {
                            completablefuture1 = this.chunkGenerate(chunkHolder, chunkStatus);
                        }
                        else
                        {
                            completablefuture1 = chunkStatus.doLoadingWork(this.world, this.templateManager, this.lightManager, (p_lambda$null$12_2_) ->
                            {
                                return this.func_219200_b(chunkHolder);
                            }, ichunk);
                        }

                        this.field_219266_t.statusChanged(chunkpos, chunkStatus);
                        return completablefuture1;
                    }
                    else {
                        return this.chunkGenerate(chunkHolder, chunkStatus);
                    }
                }
            }, this.mainThread);
        }
    }

    private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> chunkLoad(ChunkPos chunkPos)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try {
                this.world.getProfiler().func_230035_c_("chunkLoad");
                CompoundNBT compoundnbt = this.loadChunkData(chunkPos);

                if (compoundnbt != null)
                {
                    boolean flag = compoundnbt.contains("Level", 10) && compoundnbt.getCompound("Level").contains("Status", 8);

                    if (flag)
                    {
                        IChunk ichunk = ChunkSerializer.read(this.world, this.templateManager, this.pointOfInterestManager, chunkPos, compoundnbt);
                        ichunk.setLastSaveTime(this.world.getGameTime());
                        this.func_241088_a_(chunkPos, ichunk.getStatus().getType());
                        return Either.left(ichunk);
                    }

                    LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)chunkPos);
                }
            }
            catch (ReportedException reportedexception)
            {
                Throwable throwable = reportedexception.getCause();

                if (!(throwable instanceof IOException))
                {
                    this.func_241089_g_(chunkPos);
                    throw reportedexception;
                }

                LOGGER.error("Couldn't load chunk {}", chunkPos, throwable);
            }
            catch (Exception exception1)
            {
                LOGGER.error("Couldn't load chunk {}", chunkPos, exception1);
            }

            this.func_241089_g_(chunkPos);
            return Either.left(new ChunkPrimer(chunkPos, UpgradeData.EMPTY));
        }, this.mainThread);
    }

    private void func_241089_g_(ChunkPos p_241089_1_)
    {
        this.field_241087_z_.put(p_241089_1_.asLong(), (byte) - 1);
    }

    private byte func_241088_a_(ChunkPos p_241088_1_, ChunkStatus.Type p_241088_2_)
    {
        return this.field_241087_z_.put(p_241088_1_.asLong(), (byte)(p_241088_2_ == ChunkStatus.Type.PROTOCHUNK ? -1 : 1));
    }

    private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> chunkGenerate(ChunkHolder chunkHolder, ChunkStatus chunkStatus)
    {
        ChunkPos chunkpos = chunkHolder.getPosition();
        CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219236_a(chunkpos, chunkStatus.getTaskRange(), (p_lambda$chunkGenerate$15_2_) ->
        {
            return this.func_219205_a(chunkStatus, p_lambda$chunkGenerate$15_2_);
        });
        this.world.getProfiler().func_230036_c_(() ->
        {
            return "chunkGenerate " + chunkStatus.getName();
        });
        return completablefuture.thenComposeAsync((p_lambda$chunkGenerate$20_4_) ->
        {
            return p_lambda$chunkGenerate$20_4_.map((p_lambda$null$18_4_) -> {
                try {
                    CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = chunkStatus.doGenerationWork(this.world, this.generator, this.templateManager, this.lightManager, (p_lambda$null$17_2_) -> {
                        return this.func_219200_b(chunkHolder);
                    }, p_lambda$null$18_4_);
                    this.field_219266_t.statusChanged(chunkpos, chunkStatus);
                    return completablefuture1;
                }
                catch (Exception exception1)
                {
                    CrashReport crashreport = CrashReport.makeCrashReport(exception1, "Exception generating new chunk");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                    crashreportcategory.addDetail("Location", String.format("%d,%d", chunkpos.x, chunkpos.z));
                    crashreportcategory.addDetail("Position hash", ChunkPos.asLong(chunkpos.x, chunkpos.z));
                    crashreportcategory.addDetail("Generator", this.generator);
                    throw new ReportedException(crashreport);
                }
            }, (p_lambda$null$19_2_) -> {
                this.func_219209_c(chunkpos);
                return CompletableFuture.completedFuture(Either.right(p_lambda$null$19_2_));
            });
        }, (p_lambda$chunkGenerate$21_2_) ->
        {
            this.field_219264_r.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(chunkHolder, p_lambda$chunkGenerate$21_2_));
        });
    }

    protected void func_219209_c(ChunkPos p_219209_1_)
    {
        this.mainThread.enqueue(Util.namedRunnable(() ->
        {
            this.ticketManager.releaseWithLevel(TicketType.LIGHT, p_219209_1_, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), p_219209_1_);
        }, () ->
        {
            return "release light ticket " + p_219209_1_;
        }));
    }

    private ChunkStatus func_219205_a(ChunkStatus p_219205_1_, int p_219205_2_)
    {
        ChunkStatus chunkstatus;

        if (p_219205_2_ == 0)
        {
            chunkstatus = p_219205_1_.getParent();
        }
        else
        {
            chunkstatus = ChunkStatus.getStatus(ChunkStatus.getDistance(p_219205_1_) + p_219205_2_);
        }

        return chunkstatus;
    }

    private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219200_b(ChunkHolder p_219200_1_)
    {
        CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = p_219200_1_.func_219301_a(ChunkStatus.FULL.getParent());
        return completablefuture.thenApplyAsync((p_lambda$func_219200_b$26_2_) ->
        {
            ChunkStatus chunkstatus = ChunkHolder.getChunkStatusFromLevel(p_219200_1_.getChunkLevel());
            return !chunkstatus.isAtLeast(ChunkStatus.FULL) ? ChunkHolder.MISSING_CHUNK : p_lambda$func_219200_b$26_2_.mapLeft((p_lambda$null$25_2_) -> {
                ChunkPos chunkpos = p_219200_1_.getPosition();
                Chunk chunk;

                if (p_lambda$null$25_2_ instanceof ChunkPrimerWrapper)
                {
                    chunk = ((ChunkPrimerWrapper)p_lambda$null$25_2_).getChunk();
                }
                else {
                    chunk = new Chunk(this.world, (ChunkPrimer)p_lambda$null$25_2_);
                    p_219200_1_.func_219294_a(new ChunkPrimerWrapper(chunk));
                }

                chunk.setLocationType(() -> {
                    return ChunkHolder.getLocationTypeFromLevel(p_219200_1_.getChunkLevel());
                });
                chunk.postLoad();

                if (this.loadedPositions.add(chunkpos.asLong()))
                {
                    chunk.setLoaded(true);
                    this.world.addTileEntities(chunk.getTileEntityMap().values());
                    List<Entity> list = null;
                    ClassInheritanceMultiMap<Entity>[] classinheritancemultimap = chunk.getEntityLists();
                    int i = classinheritancemultimap.length;

                    for (int j = 0; j < i; ++j)
                    {
                        for (Entity entity : classinheritancemultimap[j])
                        {
                            if (!(entity instanceof PlayerEntity) && !this.world.addEntityIfNotDuplicate(entity))
                            {
                                if (list == null)
                                {
                                    list = Lists.newArrayList(entity);
                                }
                                else
                                {
                                    list.add(entity);
                                }
                            }
                        }
                    }

                    if (list != null)
                    {
                        list.forEach(chunk::removeEntity);
                    }

                    if (Reflector.ChunkEvent_Load_Constructor.exists())
                    {
                        Reflector.postForgeBusEvent(Reflector.ChunkEvent_Load_Constructor, chunk);
                    }
                }

                return chunk;
            });
        }, (p_lambda$func_219200_b$27_2_) ->
        {
            this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219069_a(p_lambda$func_219200_b$27_2_, p_219200_1_.getPosition().asLong(), p_219200_1_::getChunkLevel));
        });
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219179_a(ChunkHolder p_219179_1_)
    {
        ChunkPos chunkpos = p_219179_1_.getPosition();
        CompletableFuture<Either<List<IChunk>, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219236_a(chunkpos, 1, (p_lambda$func_219179_a$28_0_) ->
        {
            return ChunkStatus.FULL;
        });
        CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = completablefuture.thenApplyAsync((p_lambda$func_219179_a$30_0_) ->
        {
            return p_lambda$func_219179_a$30_0_.flatMap((p_lambda$null$29_0_) -> {
                Chunk chunk = (Chunk)p_lambda$null$29_0_.get(p_lambda$null$29_0_.size() / 2);
                chunk.postProcess();
                return Either.left(chunk);
            });
        }, (p_lambda$func_219179_a$31_2_) ->
        {
            this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_219179_1_, p_lambda$func_219179_a$31_2_));
        });
        completablefuture1.thenAcceptAsync((p_lambda$func_219179_a$34_2_) ->
        {
            p_lambda$func_219179_a$34_2_.mapLeft((p_lambda$null$33_2_) -> {
                this.field_219268_v.getAndIncrement();
                IPacket<?>[] ipacket = new IPacket[2];
                this.getTrackingPlayers(chunkpos, false).forEach((p_lambda$null$32_3_) -> {
                    this.sendChunkData(p_lambda$null$32_3_, ipacket, p_lambda$null$33_2_);
                });
                return Either.left(p_lambda$null$33_2_);
            });
        }, (p_lambda$func_219179_a$35_2_) ->
        {
            this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_219179_1_, p_lambda$func_219179_a$35_2_));
        });
        return completablefuture1;
    }

    public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_222961_b(ChunkHolder p_222961_1_)
    {
        return p_222961_1_.func_219276_a(ChunkStatus.FULL, this).thenApplyAsync((p_lambda$func_222961_b$37_0_) ->
        {
            return p_lambda$func_222961_b$37_0_.mapLeft((p_lambda$null$36_0_) -> {
                Chunk chunk = (Chunk)p_lambda$null$36_0_;
                chunk.rescheduleTicks();
                return chunk;
            });
        }, (p_lambda$func_222961_b$38_2_) ->
        {
            this.field_219265_s.enqueue(ChunkTaskPriorityQueueSorter.func_219081_a(p_222961_1_, p_lambda$func_222961_b$38_2_));
        });
    }

    public int func_219174_c()
    {
        return this.field_219268_v.get();
    }

    private boolean chunkSave(IChunk chunkIn)
    {
        this.pointOfInterestManager.saveIfDirty(chunkIn.getPos());

        if (!chunkIn.isModified())
        {
            return false;
        }
        else
        {
            chunkIn.setLastSaveTime(this.world.getGameTime());
            chunkIn.setModified(false);
            ChunkPos chunkpos = chunkIn.getPos();

            try
            {
                ChunkStatus chunkstatus = chunkIn.getStatus();

                if (chunkstatus.getType() != ChunkStatus.Type.LEVELCHUNK)
                {
                    if (this.func_241090_h_(chunkpos))
                    {
                        return false;
                    }

                    if (chunkstatus == ChunkStatus.EMPTY && chunkIn.getStructureStarts().values().stream().noneMatch(StructureStart::isValid))
                    {
                        return false;
                    }
                }

                this.world.getProfiler().func_230035_c_("chunkSave");
                CompoundNBT compoundnbt = ChunkSerializer.write(this.world, chunkIn);

                if (Reflector.ChunkDataEvent_Save_Constructor.exists())
                {
                    World world = (World)Reflector.call(chunkIn, Reflector.ForgeIChunk_getWorldForge);
                    Reflector.postForgeBusEvent(Reflector.ChunkDataEvent_Save_Constructor, chunkIn, world != null ? world : this.world, compoundnbt);
                }

                this.writeChunk(chunkpos, compoundnbt);
                this.func_241088_a_(chunkpos, chunkstatus.getType());
                return true;
            }
            catch (Exception exception1)
            {
                LOGGER.error("Failed to save chunk {},{}", chunkpos.x, chunkpos.z, exception1);
                return false;
            }
        }
    }

    private boolean func_241090_h_(ChunkPos p_241090_1_)
    {
        byte b0 = this.field_241087_z_.get(p_241090_1_.asLong());

        if (b0 != 0)
        {
            return b0 == 1;
        }
        else
        {
            CompoundNBT compoundnbt;

            try
            {
                compoundnbt = this.loadChunkData(p_241090_1_);

                if (compoundnbt == null)
                {
                    this.func_241089_g_(p_241090_1_);
                    return false;
                }
            }
            catch (Exception exception)
            {
                LOGGER.error("Failed to read chunk {}", p_241090_1_, exception);
                this.func_241089_g_(p_241090_1_);
                return false;
            }

            ChunkStatus.Type chunkstatus$type = ChunkSerializer.getChunkStatus(compoundnbt);
            return this.func_241088_a_(p_241090_1_, chunkstatus$type) == 1;
        }
    }

    protected void setViewDistance(int viewDistance)
    {
        int i = MathHelper.clamp(viewDistance + 1, 3, 64);

        if (i != this.viewDistance)
        {
            int j = this.viewDistance;
            this.viewDistance = i;
            this.ticketManager.setViewDistance(this.viewDistance);

            for (ChunkHolder chunkholder : this.loadedChunks.values())
            {
                ChunkPos chunkpos = chunkholder.getPosition();
                IPacket<?>[] ipacket = new IPacket[2];
                this.getTrackingPlayers(chunkpos, false).forEach((p_lambda$setViewDistance$39_4_) ->
                {
                    int k = func_219215_b(chunkpos, p_lambda$setViewDistance$39_4_, true);
                    boolean flag = k <= j;
                    boolean flag1 = k <= this.viewDistance;
                    this.setChunkLoadedAtClient(p_lambda$setViewDistance$39_4_, chunkpos, ipacket, flag, flag1);
                });
            }
        }
    }

    /**
     * Sends the chunk to the client, or tells it to unload it.
     */
    protected void setChunkLoadedAtClient(ServerPlayerEntity player, ChunkPos chunkPosIn, IPacket<?>[] packetCache, boolean wasLoaded, boolean load)
    {
        if (player.world == this.world)
        {
            if (Reflector.ForgeEventFactory_fireChunkWatch.exists())
            {
                Reflector.ForgeEventFactory_fireChunkWatch.call(wasLoaded, load, player, chunkPosIn, this.world);
            }

            if (load && !wasLoaded)
            {
                ChunkHolder chunkholder = this.func_219219_b(chunkPosIn.asLong());

                if (chunkholder != null)
                {
                    Chunk chunk = chunkholder.getChunkIfComplete();

                    if (chunk != null)
                    {
                        this.sendChunkData(player, packetCache, chunk);
                    }

                    DebugPacketSender.sendChuckPos(this.world, chunkPosIn);
                }
            }

            if (!load && wasLoaded)
            {
                player.sendChunkUnload(chunkPosIn);
            }
        }
    }

    public int getLoadedChunkCount()
    {
        return this.immutableLoadedChunks.size();
    }

    protected ChunkManager.ProxyTicketManager getTicketManager()
    {
        return this.ticketManager;
    }

    protected Iterable<ChunkHolder> getLoadedChunksIterable()
    {
        return Iterables.unmodifiableIterable(this.immutableLoadedChunks.values());
    }

    void func_225406_a(Writer p_225406_1_) throws IOException
    {
        CSVWriter csvwriter = CSVWriter.func_225428_a().func_225423_a("x").func_225423_a("z").func_225423_a("level").func_225423_a("in_memory").func_225423_a("status").func_225423_a("full_status").func_225423_a("accessible_ready").func_225423_a("ticking_ready").func_225423_a("entity_ticking_ready").func_225423_a("ticket").func_225423_a("spawning").func_225423_a("entity_count").func_225423_a("block_entity_count").func_225422_a(p_225406_1_);

        for (Entry<ChunkHolder> entry : this.immutableLoadedChunks.long2ObjectEntrySet())
        {
            ChunkPos chunkpos = new ChunkPos(entry.getLongKey());
            ChunkHolder chunkholder = entry.getValue();
            Optional<IChunk> optional = Optional.ofNullable(chunkholder.func_219287_e());
            Optional<Chunk> optional1 = optional.flatMap((p_lambda$func_225406_a$40_0_) ->
            {
                return p_lambda$func_225406_a$40_0_ instanceof Chunk ? Optional.of((Chunk)p_lambda$func_225406_a$40_0_) : Optional.empty();
            });
            csvwriter.func_225426_a(chunkpos.x, chunkpos.z, chunkholder.getChunkLevel(), optional.isPresent(), optional.map(IChunk::getStatus).orElse((ChunkStatus)null), optional1.map(Chunk::getLocationType).orElse((ChunkHolder.LocationType)null), func_225402_a(chunkholder.getBorderFuture()), func_225402_a(chunkholder.getTickingFuture()), func_225402_a(chunkholder.getEntityTickingFuture()), this.ticketManager.func_225413_c(entry.getLongKey()), !this.isOutsideSpawningRadius(chunkpos), optional1.map((p_lambda$func_225406_a$41_0_) ->
            {
                return Stream.of(p_lambda$func_225406_a$41_0_.getEntityLists()).mapToInt(ClassInheritanceMultiMap::size).sum();
            }).orElse(0), optional1.map((p_lambda$func_225406_a$42_0_) ->
            {
                return p_lambda$func_225406_a$42_0_.getTileEntityMap().size();
            }).orElse(0));
        }
    }

    private static String func_225402_a(CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> p_225402_0_)
    {
        try
        {
            Either<Chunk, ChunkHolder.IChunkLoadingError> either = p_225402_0_.getNow((Either)null);
            return either != null ? either.map((p_lambda$func_225402_a$43_0_) ->
            {
                return "done";
            }, (p_lambda$func_225402_a$44_0_) ->
            {
                return "unloaded";
            }) : "not completed";
        }
        catch (CompletionException completionexception)
        {
            return "failed " + completionexception.getCause().getMessage();
        }
        catch (CancellationException cancellationexception1)
        {
            return "cancelled";
        }
    }

    @Nullable
    private CompoundNBT loadChunkData(ChunkPos pos) throws IOException
    {
        CompoundNBT compoundnbt = this.readChunk(pos);
        return compoundnbt == null ? null : this.func_235968_a_(this.world.getDimensionKey(), this.field_219259_m, compoundnbt);
    }

    boolean isOutsideSpawningRadius(ChunkPos chunkPosIn)
    {
        long i = chunkPosIn.asLong();
        return !this.ticketManager.isOutsideSpawningRadius(i) ? true : this.playerGenerationTracker.getGeneratingPlayers(i).noneMatch((p_lambda$isOutsideSpawningRadius$45_1_) ->
        {
            return !p_lambda$isOutsideSpawningRadius$45_1_.isSpectator() && getDistanceSquaredToChunk(chunkPosIn, p_lambda$isOutsideSpawningRadius$45_1_) < 16384.0D;
        });
    }

    private boolean cannotGenerateChunks(ServerPlayerEntity player)
    {
        return player.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
    }

    void setPlayerTracking(ServerPlayerEntity player, boolean track)
    {
        boolean flag = this.cannotGenerateChunks(player);
        boolean flag1 = this.playerGenerationTracker.cannotGenerateChunks(player);
        int i = MathHelper.floor(player.getPosX()) >> 4;
        int j = MathHelper.floor(player.getPosZ()) >> 4;

        if (track)
        {
            this.playerGenerationTracker.addPlayer(ChunkPos.asLong(i, j), player, flag);
            this.func_223489_c(player);

            if (!flag)
            {
                this.ticketManager.updatePlayerPosition(SectionPos.from(player), player);
            }
        }
        else
        {
            SectionPos sectionpos = player.getManagedSectionPos();
            this.playerGenerationTracker.removePlayer(sectionpos.asChunkPos().asLong(), player);

            if (!flag1)
            {
                this.ticketManager.removePlayer(sectionpos, player);
            }
        }

        for (int l = i - this.viewDistance; l <= i + this.viewDistance; ++l)
        {
            for (int k = j - this.viewDistance; k <= j + this.viewDistance; ++k)
            {
                ChunkPos chunkpos = new ChunkPos(l, k);
                this.setChunkLoadedAtClient(player, chunkpos, new IPacket[2], !track, track);
            }
        }
    }

    private SectionPos func_223489_c(ServerPlayerEntity serverPlayerEntity)
    {
        SectionPos sectionpos = SectionPos.from(serverPlayerEntity);
        serverPlayerEntity.setManagedSectionPos(sectionpos);
        serverPlayerEntity.connection.sendPacket(new SUpdateChunkPositionPacket(sectionpos.getSectionX(), sectionpos.getSectionZ()));
        return sectionpos;
    }

    public void updatePlayerPosition(ServerPlayerEntity player)
    {
        for (ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values())
        {
            if (chunkmanager$entitytracker.entity == player)
            {
                chunkmanager$entitytracker.updateTrackingState(this.world.getPlayers());
            }
            else
            {
                chunkmanager$entitytracker.updateTrackingState(player);
            }
        }

        int l1 = MathHelper.floor(player.getPosX()) >> 4;
        int i2 = MathHelper.floor(player.getPosZ()) >> 4;
        SectionPos sectionpos = player.getManagedSectionPos();
        SectionPos sectionpos1 = SectionPos.from(player);
        long i = sectionpos.asChunkPos().asLong();
        long j = sectionpos1.asChunkPos().asLong();
        boolean flag = this.playerGenerationTracker.canGeneratePlayer(player);
        boolean flag1 = this.cannotGenerateChunks(player);
        boolean flag2 = sectionpos.asLong() != sectionpos1.asLong();

        if (flag2 || flag != flag1)
        {
            this.func_223489_c(player);

            if (!flag)
            {
                this.ticketManager.removePlayer(sectionpos, player);
            }

            if (!flag1)
            {
                this.ticketManager.updatePlayerPosition(sectionpos1, player);
            }

            if (!flag && flag1)
            {
                this.playerGenerationTracker.disableGeneration(player);
            }

            if (flag && !flag1)
            {
                this.playerGenerationTracker.enableGeneration(player);
            }

            if (i != j)
            {
                this.playerGenerationTracker.updatePlayerPosition(i, j, player);
            }
        }

        int k = sectionpos.getSectionX();
        int l = sectionpos.getSectionZ();

        if (Math.abs(k - l1) <= this.viewDistance * 2 && Math.abs(l - i2) <= this.viewDistance * 2)
        {
            int k2 = Math.min(l1, k) - this.viewDistance;
            int i3 = Math.min(i2, l) - this.viewDistance;
            int j3 = Math.max(l1, k) + this.viewDistance;
            int k3 = Math.max(i2, l) + this.viewDistance;

            for (int l3 = k2; l3 <= j3; ++l3)
            {
                for (int k1 = i3; k1 <= k3; ++k1)
                {
                    ChunkPos chunkpos1 = new ChunkPos(l3, k1);
                    boolean flag5 = getChunkDistance(chunkpos1, k, l) <= this.viewDistance;
                    boolean flag6 = getChunkDistance(chunkpos1, l1, i2) <= this.viewDistance;
                    this.setChunkLoadedAtClient(player, chunkpos1, new IPacket[2], flag5, flag6);
                }
            }
        }
        else
        {
            for (int i1 = k - this.viewDistance; i1 <= k + this.viewDistance; ++i1)
            {
                for (int j1 = l - this.viewDistance; j1 <= l + this.viewDistance; ++j1)
                {
                    ChunkPos chunkpos = new ChunkPos(i1, j1);
                    boolean flag3 = true;
                    boolean flag4 = false;
                    this.setChunkLoadedAtClient(player, chunkpos, new IPacket[2], true, false);
                }
            }

            for (int j2 = l1 - this.viewDistance; j2 <= l1 + this.viewDistance; ++j2)
            {
                for (int l2 = i2 - this.viewDistance; l2 <= i2 + this.viewDistance; ++l2)
                {
                    ChunkPos chunkpos2 = new ChunkPos(j2, l2);
                    boolean flag7 = false;
                    boolean flag8 = true;
                    this.setChunkLoadedAtClient(player, chunkpos2, new IPacket[2], false, true);
                }
            }
        }
    }

    public Stream<ServerPlayerEntity> getTrackingPlayers(ChunkPos pos, boolean boundaryOnly)
    {
        return this.playerGenerationTracker.getGeneratingPlayers(pos.asLong()).filter((p_lambda$getTrackingPlayers$46_3_) ->
        {
            int i = func_219215_b(pos, p_lambda$getTrackingPlayers$46_3_, true);

            if (i > this.viewDistance)
            {
                return false;
            }
            else {
                return !boundaryOnly || i == this.viewDistance;
            }
        });
    }

    protected void track(Entity entityIn)
    {
        boolean flag = entityIn instanceof EnderDragonPartEntity;

        if (Reflector.PartEntity.exists())
        {
            flag = Reflector.PartEntity.isInstance(entityIn);
        }

        if (!flag)
        {
            EntityType<?> entitytype = entityIn.getType();
            int i = entitytype.func_233602_m_() * 16;
            int j = entitytype.getUpdateFrequency();

            if (this.entities.containsKey(entityIn.getEntityId()))
            {
                throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException("Entity is already tracked!"));
            }

            ChunkManager.EntityTracker chunkmanager$entitytracker = new ChunkManager.EntityTracker(entityIn, i, j, entitytype.shouldSendVelocityUpdates());
            this.entities.put(entityIn.getEntityId(), chunkmanager$entitytracker);
            chunkmanager$entitytracker.updateTrackingState(this.world.getPlayers());

            if (entityIn instanceof ServerPlayerEntity)
            {
                ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityIn;
                this.setPlayerTracking(serverplayerentity, true);

                for (ChunkManager.EntityTracker chunkmanager$entitytracker1 : this.entities.values())
                {
                    if (chunkmanager$entitytracker1.entity != serverplayerentity)
                    {
                        chunkmanager$entitytracker1.updateTrackingState(serverplayerentity);
                    }
                }
            }
        }
    }

    protected void untrack(Entity entity)
    {
        if (entity instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity;
            this.setPlayerTracking(serverplayerentity, false);

            for (ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values())
            {
                chunkmanager$entitytracker.removeTracker(serverplayerentity);
            }
        }

        ChunkManager.EntityTracker chunkmanager$entitytracker1 = this.entities.remove(entity.getEntityId());

        if (chunkmanager$entitytracker1 != null)
        {
            chunkmanager$entitytracker1.removeAllTrackers();
        }
    }

    protected void tickEntityTracker()
    {
        List<ServerPlayerEntity> list = Lists.newArrayList();
        List<ServerPlayerEntity> list1 = this.world.getPlayers();

        for (ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values())
        {
            SectionPos sectionpos = chunkmanager$entitytracker.pos;
            SectionPos sectionpos1 = SectionPos.from(chunkmanager$entitytracker.entity);

            if (!Objects.equals(sectionpos, sectionpos1))
            {
                chunkmanager$entitytracker.updateTrackingState(list1);
                Entity entity = chunkmanager$entitytracker.entity;

                if (entity instanceof ServerPlayerEntity)
                {
                    list.add((ServerPlayerEntity)entity);
                }

                chunkmanager$entitytracker.pos = sectionpos1;
            }

            chunkmanager$entitytracker.entry.tick();
        }

        if (!list.isEmpty())
        {
            for (ChunkManager.EntityTracker chunkmanager$entitytracker1 : this.entities.values())
            {
                chunkmanager$entitytracker1.updateTrackingState(list);
            }
        }
    }

    protected void sendToAllTracking(Entity entity, IPacket<?> p_219222_2_)
    {
        ChunkManager.EntityTracker chunkmanager$entitytracker = this.entities.get(entity.getEntityId());

        if (chunkmanager$entitytracker != null)
        {
            chunkmanager$entitytracker.sendToAllTracking(p_219222_2_);
        }
    }

    protected void sendToTrackingAndSelf(Entity entity, IPacket<?> p_219225_2_)
    {
        ChunkManager.EntityTracker chunkmanager$entitytracker = this.entities.get(entity.getEntityId());

        if (chunkmanager$entitytracker != null)
        {
            chunkmanager$entitytracker.sendToTrackingAndSelf(p_219225_2_);
        }
    }

    private void sendChunkData(ServerPlayerEntity player, IPacket<?>[] packetCache, Chunk chunkIn)
    {
        if (packetCache[0] == null)
        {
            packetCache[0] = new SChunkDataPacket(chunkIn, 65535);
            packetCache[1] = new SUpdateLightPacket(chunkIn.getPos(), this.lightManager, true);
        }

        player.sendChunkLoad(chunkIn.getPos(), packetCache[0], packetCache[1]);
        DebugPacketSender.sendChuckPos(this.world, chunkIn.getPos());
        List<Entity> list = Lists.newArrayList();
        List<Entity> list1 = Lists.newArrayList();

        for (ChunkManager.EntityTracker chunkmanager$entitytracker : this.entities.values())
        {
            Entity entity = chunkmanager$entitytracker.entity;

            if (entity != player && entity.chunkCoordX == chunkIn.getPos().x && entity.chunkCoordZ == chunkIn.getPos().z)
            {
                chunkmanager$entitytracker.updateTrackingState(player);

                if (entity instanceof MobEntity && ((MobEntity)entity).getLeashHolder() != null)
                {
                    list.add(entity);
                }

                if (!entity.getPassengers().isEmpty())
                {
                    list1.add(entity);
                }
            }
        }

        if (!list.isEmpty())
        {
            for (Entity entity1 : list)
            {
                player.connection.sendPacket(new SMountEntityPacket(entity1, ((MobEntity)entity1).getLeashHolder()));
            }
        }

        if (!list1.isEmpty())
        {
            for (Entity entity2 : list1)
            {
                player.connection.sendPacket(new SSetPassengersPacket(entity2));
            }
        }
    }

    protected PointOfInterestManager getPointOfInterestManager()
    {
        return this.pointOfInterestManager;
    }

    public CompletableFuture<Void> func_222973_a(Chunk chunk)
    {
        return this.mainThread.runAsync(() ->
        {
            chunk.saveScheduledTicks(this.world);
        });
    }

    class EntityTracker
    {
        private final TrackedEntity entry;
        private final Entity entity;
        private final int range;
        private SectionPos pos;
        private final Set<ServerPlayerEntity> trackingPlayers = Sets.newHashSet();

        public EntityTracker(Entity entity, int p_i50468_3_, int updateFrequency, boolean sendVelocityUpdates)
        {
            this.entry = new TrackedEntity(ChunkManager.this.world, entity, updateFrequency, sendVelocityUpdates, this::sendToAllTracking);
            this.entity = entity;
            this.range = p_i50468_3_;
            this.pos = SectionPos.from(entity);
        }

        public boolean equals(Object p_equals_1_)
        {
            if (p_equals_1_ instanceof ChunkManager.EntityTracker)
            {
                return ((ChunkManager.EntityTracker)p_equals_1_).entity.getEntityId() == this.entity.getEntityId();
            }
            else
            {
                return false;
            }
        }

        public int hashCode()
        {
            return this.entity.getEntityId();
        }

        public void sendToAllTracking(IPacket<?> p_219391_1_)
        {
            for (ServerPlayerEntity serverplayerentity : this.trackingPlayers)
            {
                serverplayerentity.connection.sendPacket(p_219391_1_);
            }
        }

        public void sendToTrackingAndSelf(IPacket<?> p_219392_1_)
        {
            this.sendToAllTracking(p_219392_1_);

            if (this.entity instanceof ServerPlayerEntity)
            {
                ((ServerPlayerEntity)this.entity).connection.sendPacket(p_219392_1_);
            }
        }

        public void removeAllTrackers()
        {
            for (ServerPlayerEntity serverplayerentity : this.trackingPlayers)
            {
                this.entry.untrack(serverplayerentity);
            }
        }

        public void removeTracker(ServerPlayerEntity player)
        {
            if (this.trackingPlayers.remove(player))
            {
                this.entry.untrack(player);
            }
        }

        public void updateTrackingState(ServerPlayerEntity player)
        {
            if (player != this.entity)
            {
                Vector3d vector3d = player.getPositionVec().subtract(this.entry.getDecodedPosition());
                int i = Math.min(this.func_229843_b_(), (ChunkManager.this.viewDistance - 1) * 16);
                boolean flag = vector3d.x >= (double)(-i) && vector3d.x <= (double)i && vector3d.z >= (double)(-i) && vector3d.z <= (double)i && this.entity.isSpectatedByPlayer(player);

                if (flag)
                {
                    boolean flag1 = this.entity.forceSpawn;

                    if (!flag1)
                    {
                        ChunkPos chunkpos = new ChunkPos(this.entity.chunkCoordX, this.entity.chunkCoordZ);
                        ChunkHolder chunkholder = ChunkManager.this.func_219219_b(chunkpos.asLong());

                        if (chunkholder != null && chunkholder.getChunkIfComplete() != null)
                        {
                            flag1 = ChunkManager.func_219215_b(chunkpos, player, false) <= ChunkManager.this.viewDistance;
                        }
                    }

                    if (flag1 && this.trackingPlayers.add(player))
                    {
                        this.entry.track(player);
                    }
                }
                else if (this.trackingPlayers.remove(player))
                {
                    this.entry.untrack(player);
                }
            }
        }

        private int func_241091_a_(int p_241091_1_)
        {
            return ChunkManager.this.world.getServer().func_230512_b_(p_241091_1_);
        }

        private int func_229843_b_()
        {
            Collection<Entity> collection = this.entity.getRecursivePassengers();
            int i = this.range;

            for (Entity entity : collection)
            {
                int j = entity.getType().func_233602_m_() * 16;

                if (j > i)
                {
                    i = j;
                }
            }

            return this.func_241091_a_(i);
        }

        public void updateTrackingState(List<ServerPlayerEntity> playersList)
        {
            for (ServerPlayerEntity serverplayerentity : playersList)
            {
                this.updateTrackingState(serverplayerentity);
            }
        }
    }

    class ProxyTicketManager extends TicketManager
    {
        protected ProxyTicketManager(Executor p_i50469_2_, Executor p_i50469_3_)
        {
            super(p_i50469_2_, p_i50469_3_);
        }

        protected boolean contains(long p_219371_1_)
        {
            return ChunkManager.this.unloadableChunks.contains(p_219371_1_);
        }

        @Nullable
        protected ChunkHolder getChunkHolder(long chunkPosIn)
        {
            return ChunkManager.this.func_219220_a(chunkPosIn);
        }

        @Nullable
        protected ChunkHolder setChunkLevel(long chunkPosIn, int newLevel, @Nullable ChunkHolder holder, int oldLevel)
        {
            return ChunkManager.this.setChunkLevel(chunkPosIn, newLevel, holder, oldLevel);
        }
    }
}
