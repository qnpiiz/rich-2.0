package net.minecraft.village;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.util.SectionDistanceGraph;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.storage.RegionSectionCache;

public class PointOfInterestManager extends RegionSectionCache<PointOfInterestData>
{
    private final PointOfInterestManager.DistanceGraph distanceTracker;
    private final LongSet loadedChunks = new LongOpenHashSet();

    public PointOfInterestManager(File folder, DataFixer fixer, boolean sync)
    {
        super(folder, PointOfInterestData::func_234158_a_, PointOfInterestData::new, fixer, DefaultTypeReferences.POI_CHUNK, sync);
        this.distanceTracker = new PointOfInterestManager.DistanceGraph();
    }

    public void add(BlockPos pos, PointOfInterestType poiType)
    {
        this.func_235995_e_(SectionPos.from(pos).asLong()).add(pos, poiType);
    }

    public void remove(BlockPos pos)
    {
        this.func_235995_e_(SectionPos.from(pos).asLong()).remove(pos);
    }

    public long getCountInRange(Predicate<PointOfInterestType> p_219145_1_, BlockPos pos, int distance, PointOfInterestManager.Status status)
    {
        return this.func_219146_b(p_219145_1_, pos, distance, status).count();
    }

    public boolean hasTypeAtPosition(PointOfInterestType type, BlockPos pos)
    {
        Optional<PointOfInterestType> optional = this.func_235995_e_(SectionPos.from(pos).asLong()).getType(pos);
        return optional.isPresent() && optional.get().equals(type);
    }

    public Stream<PointOfInterest> getInSquare(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int distance, PointOfInterestManager.Status status)
    {
        int i = Math.floorDiv(distance, 16) + 1;
        return ChunkPos.getAllInBox(new ChunkPos(pos), i).flatMap((chunkPos) ->
        {
            return this.getInChunk(typePredicate, chunkPos, status);
        }).filter((poi) ->
        {
            BlockPos blockpos = poi.getPos();
            return Math.abs(blockpos.getX() - pos.getX()) <= distance && Math.abs(blockpos.getZ() - pos.getZ()) <= distance;
        });
    }

    public Stream<PointOfInterest> func_219146_b(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int distance, PointOfInterestManager.Status status)
    {
        int i = distance * distance;
        return this.getInSquare(typePredicate, pos, distance, status).filter((p_226349_2_) ->
        {
            return p_226349_2_.getPos().distanceSq(pos) <= (double)i;
        });
    }

    public Stream<PointOfInterest> getInChunk(Predicate<PointOfInterestType> p_219137_1_, ChunkPos posChunk, PointOfInterestManager.Status status)
    {
        return IntStream.range(0, 16).boxed().map((y) ->
        {
            return this.func_219113_d(SectionPos.from(posChunk, y).asLong());
        }).filter(Optional::isPresent).flatMap((data) ->
        {
            return data.get().getRecords(p_219137_1_, status);
        });
    }

    public Stream<BlockPos> findAll(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, BlockPos pos, int distance, PointOfInterestManager.Status status)
    {
        return this.func_219146_b(typePredicate, pos, distance, status).map(PointOfInterest::getPos).filter(posPredicate);
    }

    public Stream<BlockPos> func_242324_b(Predicate<PointOfInterestType> p_242324_1_, Predicate<BlockPos> posPredicate, BlockPos p_242324_3_, int distance, PointOfInterestManager.Status status)
    {
        return this.findAll(p_242324_1_, posPredicate, p_242324_3_, distance, status).sorted(Comparator.comparingDouble((pos) ->
        {
            return pos.distanceSq(p_242324_3_);
        }));
    }

    public Optional<BlockPos> find(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, BlockPos pos, int distance, PointOfInterestManager.Status status)
    {
        return this.findAll(typePredicate, posPredicate, pos, distance, status).findFirst();
    }

    public Optional<BlockPos> func_234148_d_(Predicate<PointOfInterestType> typePredicate, BlockPos pos, int distance, PointOfInterestManager.Status status)
    {
        return this.func_219146_b(typePredicate, pos, distance, status).map(PointOfInterest::getPos).min(Comparator.comparingDouble((pos2) ->
        {
            return pos2.distanceSq(pos);
        }));
    }

    public Optional<BlockPos> take(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, BlockPos pos, int distance)
    {
        return this.func_219146_b(typePredicate, pos, distance, PointOfInterestManager.Status.HAS_SPACE).filter((p_219129_1_) ->
        {
            return posPredicate.test(p_219129_1_.getPos());
        }).findFirst().map((p_219152_0_) ->
        {
            p_219152_0_.claim();
            return p_219152_0_.getPos();
        });
    }

    public Optional<BlockPos> getRandom(Predicate<PointOfInterestType> typePredicate, Predicate<BlockPos> posPredicate, PointOfInterestManager.Status status, BlockPos pos, int distance, Random rand)
    {
        List<PointOfInterest> list = this.func_219146_b(typePredicate, pos, distance, status).collect(Collectors.toList());
        Collections.shuffle(list, rand);
        return list.stream().filter((p_234143_1_) ->
        {
            return posPredicate.test(p_234143_1_.getPos());
        }).findFirst().map(PointOfInterest::getPos);
    }

    public boolean release(BlockPos pos)
    {
        return this.func_235995_e_(SectionPos.from(pos).asLong()).release(pos);
    }

    public boolean exists(BlockPos pos, Predicate<PointOfInterestType> p_219138_2_)
    {
        return this.func_219113_d(SectionPos.from(pos).asLong()).map((data) ->
        {
            return data.exists(pos, p_219138_2_);
        }).orElse(false);
    }

    public Optional<PointOfInterestType> getType(BlockPos pos)
    {
        PointOfInterestData pointofinterestdata = this.func_235995_e_(SectionPos.from(pos).asLong());
        return pointofinterestdata.getType(pos);
    }

    public int sectionsToVillage(SectionPos sectionPos)
    {
        this.distanceTracker.runAllUpdates();
        return this.distanceTracker.getLevel(sectionPos.asLong());
    }

    private boolean isVillageCenter(long p_219154_1_)
    {
        Optional<PointOfInterestData> optional = this.func_219106_c(p_219154_1_);
        return optional == null ? false : optional.map((data) ->
        {
            return data.getRecords(PointOfInterestType.MATCH_ANY, PointOfInterestManager.Status.IS_OCCUPIED).count() > 0L;
        }).orElse(false);
    }

    public void tick(BooleanSupplier p_219115_1_)
    {
        super.tick(p_219115_1_);
        this.distanceTracker.runAllUpdates();
    }

    protected void markDirty(long sectionPosIn)
    {
        super.markDirty(sectionPosIn);
        this.distanceTracker.updateSourceLevel(sectionPosIn, this.distanceTracker.getSourceLevel(sectionPosIn), false);
    }

    protected void onSectionLoad(long p_219111_1_)
    {
        this.distanceTracker.updateSourceLevel(p_219111_1_, this.distanceTracker.getSourceLevel(p_219111_1_), false);
    }

    public void checkConsistencyWithBlocks(ChunkPos pos, ChunkSection section)
    {
        SectionPos sectionpos = SectionPos.from(pos, section.getYLocation() >> 4);
        Util.acceptOrElse(this.func_219113_d(sectionpos.asLong()), (data) ->
        {
            data.refresh((p_234145_3_) -> {
                if (hasAnyPOI(section))
                {
                    this.updateFromSelection(section, sectionpos, p_234145_3_);
                }
            });
        }, () ->
        {
            if (hasAnyPOI(section))
            {
                PointOfInterestData pointofinterestdata = this.func_235995_e_(sectionpos.asLong());
                this.updateFromSelection(section, sectionpos, pointofinterestdata::add);
            }
        });
    }

    private static boolean hasAnyPOI(ChunkSection section)
    {
        return section.isValidPOIState(PointOfInterestType.BLOCKS_OF_INTEREST::contains);
    }

    private void updateFromSelection(ChunkSection section, SectionPos sectionPos, BiConsumer<BlockPos, PointOfInterestType> posToTypeConsumer)
    {
        sectionPos.allBlocksWithin().forEach((pos) ->
        {
            BlockState blockstate = section.getBlockState(SectionPos.mask(pos.getX()), SectionPos.mask(pos.getY()), SectionPos.mask(pos.getZ()));
            PointOfInterestType.forState(blockstate).ifPresent((type) -> {
                posToTypeConsumer.accept(pos, type);
            });
        });
    }

    public void ensureLoadedAndValid(IWorldReader worldReader, BlockPos pos, int coordinateOffset)
    {
        SectionPos.func_229421_b_(new ChunkPos(pos), Math.floorDiv(coordinateOffset, 16)).map((sectionPos) ->
        {
            return Pair.of(sectionPos, this.func_219113_d(sectionPos.asLong()));
        }).filter((p_234146_0_) ->
        {
            return !p_234146_0_.getSecond().map(PointOfInterestData::isValid).orElse(false);
        }).map((p_234140_0_) ->
        {
            return p_234140_0_.getFirst().asChunkPos();
        }).filter((chunkPos) ->
        {
            return this.loadedChunks.add(chunkPos.asLong());
        }).forEach((chunkPos) ->
        {
            worldReader.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY);
        });
    }

    final class DistanceGraph extends SectionDistanceGraph
    {
        private final Long2ByteMap levels = new Long2ByteOpenHashMap();

        protected DistanceGraph()
        {
            super(7, 16, 256);
            this.levels.defaultReturnValue((byte)7);
        }

        protected int getSourceLevel(long pos)
        {
            return PointOfInterestManager.this.isVillageCenter(pos) ? 0 : 7;
        }

        protected int getLevel(long sectionPosIn)
        {
            return this.levels.get(sectionPosIn);
        }

        protected void setLevel(long sectionPosIn, int level)
        {
            if (level > 6)
            {
                this.levels.remove(sectionPosIn);
            }
            else
            {
                this.levels.put(sectionPosIn, (byte)level);
            }
        }

        public void runAllUpdates()
        {
            super.processUpdates(Integer.MAX_VALUE);
        }
    }

    public static enum Status
    {
        HAS_SPACE(PointOfInterest::hasSpace),
        IS_OCCUPIED(PointOfInterest::isOccupied),
        ANY((poi) -> {
            return true;
        });

        private final Predicate <? super PointOfInterest > test;

        private Status(Predicate <? super PointOfInterest > test)
        {
            this.test = test;
        }

        public Predicate <? super PointOfInterest > getTest()
        {
            return this.test;
        }
    }
}
