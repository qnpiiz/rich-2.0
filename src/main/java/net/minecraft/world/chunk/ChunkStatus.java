package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.ServerWorldLightManager;

public class ChunkStatus
{
    private static final EnumSet<Heightmap.Type> PRE_FEATURES = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
    private static final EnumSet<Heightmap.Type> POST_FEATURES = EnumSet.of(Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
    private static final ChunkStatus.ILoadingWorker NOOP_LOADING_WORKER = (status, world, templateManager, worldLightManager, loadingFunction, loadingChunk) ->
    {
        if (loadingChunk instanceof ChunkPrimer && !loadingChunk.getStatus().isAtLeast(status))
        {
            ((ChunkPrimer)loadingChunk).setStatus(status);
        }

        return CompletableFuture.completedFuture(Either.left(loadingChunk));
    };
    public static final ChunkStatus EMPTY = registerSelective("empty", (ChunkStatus)null, -1, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
    });
    public static final ChunkStatus STRUCTURE_STARTS = register("structure_starts", EMPTY, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (status, world, generator, templateManager, worldLightManager, loadingFunction, chunks, loadingChunk) ->
    {
        if (!loadingChunk.getStatus().isAtLeast(status))
        {
            if (world.getServer().func_240793_aU_().getDimensionGeneratorSettings().doesGenerateFeatures())
            {
                generator.func_242707_a(world.func_241828_r(), world.func_241112_a_(), loadingChunk, templateManager, world.getSeed());
            }

            if (loadingChunk instanceof ChunkPrimer)
            {
                ((ChunkPrimer)loadingChunk).setStatus(status);
            }
        }

        return CompletableFuture.completedFuture(Either.left(loadingChunk));
    });
    public static final ChunkStatus STRUCTURE_REFERENCES = registerSelective("structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
        WorldGenRegion worldgenregion = new WorldGenRegion(world, chunks);
        generator.func_235953_a_(worldgenregion, world.func_241112_a_().func_241464_a_(worldgenregion), loadingChunk);
    });
    public static final ChunkStatus BIOMES = registerSelective("biomes", STRUCTURE_REFERENCES, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
        generator.func_242706_a(world.func_241828_r().getRegistry(Registry.BIOME_KEY), loadingChunk);
    });
    public static final ChunkStatus NOISE = registerSelective("noise", BIOMES, 8, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
        WorldGenRegion worldgenregion = new WorldGenRegion(world, chunks);
        generator.func_230352_b_(worldgenregion, world.func_241112_a_().func_241464_a_(worldgenregion), loadingChunk);
    });
    public static final ChunkStatus SURFACE = registerSelective("surface", NOISE, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
        generator.generateSurface(new WorldGenRegion(world, chunks), loadingChunk);
    });
    public static final ChunkStatus CARVERS = registerSelective("carvers", SURFACE, 0, PRE_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
        generator.func_230350_a_(world.getSeed(), world.getBiomeManager(), loadingChunk, GenerationStage.Carving.AIR);
    });
    public static final ChunkStatus LIQUID_CARVERS = registerSelective("liquid_carvers", CARVERS, 0, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
        generator.func_230350_a_(world.getSeed(), world.getBiomeManager(), loadingChunk, GenerationStage.Carving.LIQUID);
    });
    public static final ChunkStatus FEATURES = register("features", LIQUID_CARVERS, 8, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (status, world, generator, templateManager, worldLightManager, loadingFunction, chunks, loadingChunk) ->
    {
        ChunkPrimer chunkprimer = (ChunkPrimer)loadingChunk;
        chunkprimer.setLightManager(worldLightManager);

        if (!loadingChunk.getStatus().isAtLeast(status))
        {
            Heightmap.updateChunkHeightmaps(loadingChunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
            WorldGenRegion worldgenregion = new WorldGenRegion(world, chunks);
            generator.func_230351_a_(worldgenregion, world.func_241112_a_().func_241464_a_(worldgenregion));
            chunkprimer.setStatus(status);
        }

        return CompletableFuture.completedFuture(Either.left(loadingChunk));
    });
    public static final ChunkStatus LIGHT = register("light", FEATURES, 1, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (status, world, generator, templateManager, worldLightManager, loadingFunction, chunks, loadingChunk) ->
    {
        return lightChunk(status, worldLightManager, loadingChunk);
    }, (status, world, templateManager, worldLightManager, loadingFunction, loadingChunk) ->
    {
        return lightChunk(status, worldLightManager, loadingChunk);
    });
    public static final ChunkStatus SPAWN = registerSelective("spawn", LIGHT, 0, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
        generator.func_230354_a_(new WorldGenRegion(world, chunks));
    });
    public static final ChunkStatus HEIGHTMAPS = registerSelective("heightmaps", SPAWN, 0, POST_FEATURES, ChunkStatus.Type.PROTOCHUNK, (world, generator, chunks, loadingChunk) ->
    {
    });
    public static final ChunkStatus FULL = register("full", HEIGHTMAPS, 0, POST_FEATURES, ChunkStatus.Type.LEVELCHUNK, (status, world, generator, templateManager, worldLightManager, loadingFunction, chunks, loadingChunk) ->
    {
        return loadingFunction.apply(loadingChunk);
    }, (status, world, templateManager, worldLightManager, loadingFunction, loadingChunk) ->
    {
        return loadingFunction.apply(loadingChunk);
    });
    private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(FULL, FEATURES, LIQUID_CARVERS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS);
    private static final IntList RANGE_BY_STATUS = Util.make(new IntArrayList(getAll().size()), (statusRange) ->
    {
        int i = 0;

        for (int j = getAll().size() - 1; j >= 0; --j)
        {
            while (i + 1 < STATUS_BY_RANGE.size() && j <= STATUS_BY_RANGE.get(i + 1).ordinal())
            {
                ++i;
            }

            statusRange.add(0, i);
        }
    });
    private final String name;
    private final int ordinal;
    private final ChunkStatus parent;
    private final ChunkStatus.IGenerationWorker generationWorker;
    private final ChunkStatus.ILoadingWorker loadingWorker;
    private final int taskRange;
    private final ChunkStatus.Type type;
    private final EnumSet<Heightmap.Type> heightmaps;

    private static CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> lightChunk(ChunkStatus status, ServerWorldLightManager lightManager, IChunk chunk)
    {
        boolean flag = isLighted(status, chunk);

        if (!chunk.getStatus().isAtLeast(status))
        {
            ((ChunkPrimer)chunk).setStatus(status);
        }

        return lightManager.lightChunk(chunk, flag).thenApply(Either::left);
    }

    private static ChunkStatus registerSelective(String key, @Nullable ChunkStatus parent, int taskRange, EnumSet<Heightmap.Type> heightmaps, ChunkStatus.Type type, ChunkStatus.ISelectiveWorker generationWorker)
    {
        return register(key, parent, taskRange, heightmaps, type, generationWorker);
    }

    private static ChunkStatus register(String key, @Nullable ChunkStatus parent, int taskRange, EnumSet<Heightmap.Type> heightmaps, ChunkStatus.Type type, ChunkStatus.IGenerationWorker generationWorker)
    {
        return register(key, parent, taskRange, heightmaps, type, generationWorker, NOOP_LOADING_WORKER);
    }

    private static ChunkStatus register(String key, @Nullable ChunkStatus parent, int taskRange, EnumSet<Heightmap.Type> heightmaps, ChunkStatus.Type type, ChunkStatus.IGenerationWorker generationWorker, ChunkStatus.ILoadingWorker loadingWorker)
    {
        return Registry.register(Registry.CHUNK_STATUS, key, new ChunkStatus(key, parent, taskRange, heightmaps, type, generationWorker, loadingWorker));
    }

    public static List<ChunkStatus> getAll()
    {
        List<ChunkStatus> list = Lists.newArrayList();
        ChunkStatus chunkstatus;

        for (chunkstatus = FULL; chunkstatus.getParent() != chunkstatus; chunkstatus = chunkstatus.getParent())
        {
            list.add(chunkstatus);
        }

        list.add(chunkstatus);
        Collections.reverse(list);
        return list;
    }

    private static boolean isLighted(ChunkStatus status, IChunk chunk)
    {
        return chunk.getStatus().isAtLeast(status) && chunk.hasLight();
    }

    public static ChunkStatus getStatus(int id)
    {
        if (id >= STATUS_BY_RANGE.size())
        {
            return EMPTY;
        }
        else
        {
            return id < 0 ? FULL : STATUS_BY_RANGE.get(id);
        }
    }

    public static int maxDistance()
    {
        return STATUS_BY_RANGE.size();
    }

    public static int getDistance(ChunkStatus status)
    {
        return RANGE_BY_STATUS.getInt(status.ordinal());
    }

    ChunkStatus(String nameIn, @Nullable ChunkStatus parentIn, int taskRangeIn, EnumSet<Heightmap.Type> heightmapsIn, ChunkStatus.Type typeIn, ChunkStatus.IGenerationWorker generationWorkerIn, ChunkStatus.ILoadingWorker loadingWorkerIn)
    {
        this.name = nameIn;
        this.parent = parentIn == null ? this : parentIn;
        this.generationWorker = generationWorkerIn;
        this.loadingWorker = loadingWorkerIn;
        this.taskRange = taskRangeIn;
        this.type = typeIn;
        this.heightmaps = heightmapsIn;
        this.ordinal = parentIn == null ? 0 : parentIn.ordinal() + 1;
    }

    public int ordinal()
    {
        return this.ordinal;
    }

    public String getName()
    {
        return this.name;
    }

    public ChunkStatus getParent()
    {
        return this.parent;
    }

    public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doGenerationWork(ServerWorld worldIn, ChunkGenerator chunkGeneratorIn, TemplateManager templateManagerIn, ServerWorldLightManager lightManager, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> loadingFunction, List<IChunk> chunks)
    {
        return this.generationWorker.doWork(this, worldIn, chunkGeneratorIn, templateManagerIn, lightManager, loadingFunction, chunks, chunks.get(chunks.size() / 2));
    }

    public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doLoadingWork(ServerWorld worldIn, TemplateManager templateManagerIn, ServerWorldLightManager lightManager, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> loadingFunction, IChunk loadingChunk)
    {
        return this.loadingWorker.doWork(this, worldIn, templateManagerIn, lightManager, loadingFunction, loadingChunk);
    }

    /**
     * Distance in chunks between the edge of the center chunk and the edge of the chunk region needed for the task. The
     * task will only affect the center chunk, only reading from the chunks in the margin.
     */
    public int getTaskRange()
    {
        return this.taskRange;
    }

    public ChunkStatus.Type getType()
    {
        return this.type;
    }

    public static ChunkStatus byName(String location)
    {
        return Registry.CHUNK_STATUS.getOrDefault(ResourceLocation.tryCreate(location));
    }

    public EnumSet<Heightmap.Type> getHeightMaps()
    {
        return this.heightmaps;
    }

    public boolean isAtLeast(ChunkStatus status)
    {
        return this.ordinal() >= status.ordinal();
    }

    public String toString()
    {
        return Registry.CHUNK_STATUS.getKey(this).toString();
    }

    interface IGenerationWorker
    {
        CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus p_doWork_1_, ServerWorld p_doWork_2_, ChunkGenerator p_doWork_3_, TemplateManager p_doWork_4_, ServerWorldLightManager p_doWork_5_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_doWork_6_, List<IChunk> p_doWork_7_, IChunk p_doWork_8_);
    }

    interface ILoadingWorker
    {
        CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus p_doWork_1_, ServerWorld p_doWork_2_, TemplateManager p_doWork_3_, ServerWorldLightManager p_doWork_4_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_doWork_5_, IChunk p_doWork_6_);
    }

    interface ISelectiveWorker extends ChunkStatus.IGenerationWorker
    {
    default CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> doWork(ChunkStatus p_doWork_1_, ServerWorld p_doWork_2_, ChunkGenerator p_doWork_3_, TemplateManager p_doWork_4_, ServerWorldLightManager p_doWork_5_, Function<IChunk, CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> p_doWork_6_, List<IChunk> p_doWork_7_, IChunk p_doWork_8_)
        {
            if (!p_doWork_8_.getStatus().isAtLeast(p_doWork_1_))
            {
                this.doWork(p_doWork_2_, p_doWork_3_, p_doWork_7_, p_doWork_8_);

                if (p_doWork_8_ instanceof ChunkPrimer)
                {
                    ((ChunkPrimer)p_doWork_8_).setStatus(p_doWork_1_);
                }
            }

            return CompletableFuture.completedFuture(Either.left(p_doWork_8_));
        }

        void doWork(ServerWorld p_doWork_1_, ChunkGenerator p_doWork_2_, List<IChunk> p_doWork_3_, IChunk p_doWork_4_);
    }

    public static enum Type
    {
        PROTOCHUNK,
        LEVELCHUNK;
    }
}
