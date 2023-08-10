package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.SectionDistanceGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public abstract class SectionLightStorage<M extends LightDataMap<M>> extends SectionDistanceGraph
{
    protected static final NibbleArray EMPTY_ARRAY = new NibbleArray();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightType type;
    private final IChunkLightProvider chunkProvider;

    /**
     * Section positions with blocks in them that can be affected by lighting. All neighbor sections can spread light
     * into them.
     */
    protected final LongSet activeLightSections = new LongOpenHashSet();
    protected final LongSet addedEmptySections = new LongOpenHashSet();
    protected final LongSet addedActiveLightSections = new LongOpenHashSet();
    protected volatile M uncachedLightData;
    protected final M cachedLightData;
    protected final LongSet dirtyCachedSections = new LongOpenHashSet();
    protected final LongSet changedLightPositions = new LongOpenHashSet();
    protected final Long2ObjectMap<NibbleArray> newArrays = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<>());
    private final LongSet field_241536_n_ = new LongOpenHashSet();

    /**
     * Section column positions (section positions with Y=0) that need to be kept even if some of their sections could
     * otherwise be removed.
     */
    private final LongSet chunksToRetain = new LongOpenHashSet();

    /**
     * Set of section positions that can be removed, because their light won't affect any blocks.
     */
    private final LongSet noLightSections = new LongOpenHashSet();
    protected volatile boolean hasSectionsToUpdate;

    protected SectionLightStorage(LightType lightTypeIn, IChunkLightProvider chunkLightProvider, M dataMap)
    {
        super(3, 16, 256);
        this.type = lightTypeIn;
        this.chunkProvider = chunkLightProvider;
        this.cachedLightData = dataMap;
        this.uncachedLightData = dataMap.copy();
        this.uncachedLightData.disableCaching();
    }

    protected boolean hasSection(long sectionPosIn)
    {
        return this.getArray(sectionPosIn, true) != null;
    }

    @Nullable
    protected NibbleArray getArray(long sectionPosIn, boolean cached)
    {
        return this.getArray((M)(cached ? this.cachedLightData : this.uncachedLightData), sectionPosIn);
    }

    @Nullable
    protected NibbleArray getArray(M map, long sectionPosIn)
    {
        return map.getArray(sectionPosIn);
    }

    @Nullable
    public NibbleArray getArray(long sectionPosIn)
    {
        NibbleArray nibblearray = this.newArrays.get(sectionPosIn);
        return nibblearray != null ? nibblearray : this.getArray(sectionPosIn, false);
    }

    protected abstract int getLightOrDefault(long worldPos);

    protected int getLight(long worldPos)
    {
        long i = SectionPos.worldToSection(worldPos);
        NibbleArray nibblearray = this.getArray(i, true);
        return nibblearray.get(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)));
    }

    protected void setLight(long worldPos, int lightLevel)
    {
        long i = SectionPos.worldToSection(worldPos);

        if (this.dirtyCachedSections.add(i))
        {
            this.cachedLightData.copyArray(i);
        }

        NibbleArray nibblearray = this.getArray(i, true);
        nibblearray.set(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)), lightLevel);

        for (int j = -1; j <= 1; ++j)
        {
            for (int k = -1; k <= 1; ++k)
            {
                for (int l = -1; l <= 1; ++l)
                {
                    this.changedLightPositions.add(SectionPos.worldToSection(BlockPos.offset(worldPos, k, l, j)));
                }
            }
        }
    }

    protected int getLevel(long sectionPosIn)
    {
        if (sectionPosIn == Long.MAX_VALUE)
        {
            return 2;
        }
        else if (this.activeLightSections.contains(sectionPosIn))
        {
            return 0;
        }
        else
        {
            return !this.noLightSections.contains(sectionPosIn) && this.cachedLightData.hasArray(sectionPosIn) ? 1 : 2;
        }
    }

    protected int getSourceLevel(long pos)
    {
        if (this.addedEmptySections.contains(pos))
        {
            return 2;
        }
        else
        {
            return !this.activeLightSections.contains(pos) && !this.addedActiveLightSections.contains(pos) ? 2 : 0;
        }
    }

    protected void setLevel(long sectionPosIn, int level)
    {
        int i = this.getLevel(sectionPosIn);

        if (i != 0 && level == 0)
        {
            this.activeLightSections.add(sectionPosIn);
            this.addedActiveLightSections.remove(sectionPosIn);
        }

        if (i == 0 && level != 0)
        {
            this.activeLightSections.remove(sectionPosIn);
            this.addedEmptySections.remove(sectionPosIn);
        }

        if (i >= 2 && level != 2)
        {
            if (this.noLightSections.contains(sectionPosIn))
            {
                this.noLightSections.remove(sectionPosIn);
            }
            else
            {
                this.cachedLightData.setArray(sectionPosIn, this.getOrCreateArray(sectionPosIn));
                this.dirtyCachedSections.add(sectionPosIn);
                this.addSection(sectionPosIn);

                for (int j = -1; j <= 1; ++j)
                {
                    for (int k = -1; k <= 1; ++k)
                    {
                        for (int l = -1; l <= 1; ++l)
                        {
                            this.changedLightPositions.add(SectionPos.worldToSection(BlockPos.offset(sectionPosIn, k, l, j)));
                        }
                    }
                }
            }
        }

        if (i != 2 && level >= 2)
        {
            this.noLightSections.add(sectionPosIn);
        }

        this.hasSectionsToUpdate = !this.noLightSections.isEmpty();
    }

    protected NibbleArray getOrCreateArray(long sectionPosIn)
    {
        NibbleArray nibblearray = this.newArrays.get(sectionPosIn);
        return nibblearray != null ? nibblearray : new NibbleArray();
    }

    protected void cancelSectionUpdates(LightEngine <? , ? > engine, long sectionPosIn)
    {
        if (engine.func_227467_c_() < 8192)
        {
            engine.func_227465_a_((p_227469_2_) ->
            {
                return SectionPos.worldToSection(p_227469_2_) == sectionPosIn;
            });
        }
        else
        {
            int i = SectionPos.toWorld(SectionPos.extractX(sectionPosIn));
            int j = SectionPos.toWorld(SectionPos.extractY(sectionPosIn));
            int k = SectionPos.toWorld(SectionPos.extractZ(sectionPosIn));

            for (int l = 0; l < 16; ++l)
            {
                for (int i1 = 0; i1 < 16; ++i1)
                {
                    for (int j1 = 0; j1 < 16; ++j1)
                    {
                        long k1 = BlockPos.pack(i + l, j + i1, k + j1);
                        engine.cancelUpdate(k1);
                    }
                }
            }
        }
    }

    protected boolean hasSectionsToUpdate()
    {
        return this.hasSectionsToUpdate;
    }

    protected void updateSections(LightEngine < M, ? > engine, boolean updateSkyLight, boolean updateBlockLight)
    {
        if (this.hasSectionsToUpdate() || !this.newArrays.isEmpty())
        {
            for (long i : this.noLightSections)
            {
                this.cancelSectionUpdates(engine, i);
                NibbleArray nibblearray = this.newArrays.remove(i);
                NibbleArray nibblearray1 = this.cachedLightData.removeArray(i);

                if (this.chunksToRetain.contains(SectionPos.toSectionColumnPos(i)))
                {
                    if (nibblearray != null)
                    {
                        this.newArrays.put(i, nibblearray);
                    }
                    else if (nibblearray1 != null)
                    {
                        this.newArrays.put(i, nibblearray1);
                    }
                }
            }

            this.cachedLightData.invalidateCaches();

            for (long k : this.noLightSections)
            {
                this.removeSection(k);
            }

            this.noLightSections.clear();
            this.hasSectionsToUpdate = false;

            for (Entry<NibbleArray> entry : this.newArrays.long2ObjectEntrySet())
            {
                long j = entry.getLongKey();

                if (this.hasSection(j))
                {
                    NibbleArray nibblearray2 = entry.getValue();

                    if (this.cachedLightData.getArray(j) != nibblearray2)
                    {
                        this.cancelSectionUpdates(engine, j);
                        this.cachedLightData.setArray(j, nibblearray2);
                        this.dirtyCachedSections.add(j);
                    }
                }
            }

            this.cachedLightData.invalidateCaches();

            if (!updateBlockLight)
            {
                for (long l : this.newArrays.keySet())
                {
                    this.func_241538_b_(engine, l);
                }
            }
            else
            {
                for (long i1 : this.field_241536_n_)
                {
                    this.func_241538_b_(engine, i1);
                }
            }

            this.field_241536_n_.clear();
            ObjectIterator<Entry<NibbleArray>> objectiterator = this.newArrays.long2ObjectEntrySet().iterator();

            while (objectiterator.hasNext())
            {
                Entry<NibbleArray> entry1 = objectiterator.next();
                long j1 = entry1.getLongKey();

                if (this.hasSection(j1))
                {
                    objectiterator.remove();
                }
            }
        }
    }

    private void func_241538_b_(LightEngine < M, ? > p_241538_1_, long p_241538_2_)
    {
        if (this.hasSection(p_241538_2_))
        {
            int i = SectionPos.toWorld(SectionPos.extractX(p_241538_2_));
            int j = SectionPos.toWorld(SectionPos.extractY(p_241538_2_));
            int k = SectionPos.toWorld(SectionPos.extractZ(p_241538_2_));

            for (Direction direction : DIRECTIONS)
            {
                long l = SectionPos.withOffset(p_241538_2_, direction);

                if (!this.newArrays.containsKey(l) && this.hasSection(l))
                {
                    for (int i1 = 0; i1 < 16; ++i1)
                    {
                        for (int j1 = 0; j1 < 16; ++j1)
                        {
                            long k1;
                            long l1;

                            switch (direction)
                            {
                                case DOWN:
                                    k1 = BlockPos.pack(i + j1, j, k + i1);
                                    l1 = BlockPos.pack(i + j1, j - 1, k + i1);
                                    break;

                                case UP:
                                    k1 = BlockPos.pack(i + j1, j + 16 - 1, k + i1);
                                    l1 = BlockPos.pack(i + j1, j + 16, k + i1);
                                    break;

                                case NORTH:
                                    k1 = BlockPos.pack(i + i1, j + j1, k);
                                    l1 = BlockPos.pack(i + i1, j + j1, k - 1);
                                    break;

                                case SOUTH:
                                    k1 = BlockPos.pack(i + i1, j + j1, k + 16 - 1);
                                    l1 = BlockPos.pack(i + i1, j + j1, k + 16);
                                    break;

                                case WEST:
                                    k1 = BlockPos.pack(i, j + i1, k + j1);
                                    l1 = BlockPos.pack(i - 1, j + i1, k + j1);
                                    break;

                                default:
                                    k1 = BlockPos.pack(i + 16 - 1, j + i1, k + j1);
                                    l1 = BlockPos.pack(i + 16, j + i1, k + j1);
                            }

                            p_241538_1_.scheduleUpdate(k1, l1, p_241538_1_.getEdgeLevel(k1, l1, p_241538_1_.getLevel(k1)), false);
                            p_241538_1_.scheduleUpdate(l1, k1, p_241538_1_.getEdgeLevel(l1, k1, p_241538_1_.getLevel(l1)), false);
                        }
                    }
                }
            }
        }
    }

    protected void addSection(long sectionPos)
    {
    }

    protected void removeSection(long p_215523_1_)
    {
    }

    protected void setColumnEnabled(long p_215526_1_, boolean p_215526_3_)
    {
    }

    public void retainChunkData(long sectionColumnPos, boolean retain)
    {
        if (retain)
        {
            this.chunksToRetain.add(sectionColumnPos);
        }
        else
        {
            this.chunksToRetain.remove(sectionColumnPos);
        }
    }

    protected void setData(long sectionPosIn, @Nullable NibbleArray array, boolean p_215529_4_)
    {
        if (array != null)
        {
            this.newArrays.put(sectionPosIn, array);

            if (!p_215529_4_)
            {
                this.field_241536_n_.add(sectionPosIn);
            }
        }
        else
        {
            this.newArrays.remove(sectionPosIn);
        }
    }

    protected void updateSectionStatus(long sectionPosIn, boolean isEmpty)
    {
        boolean flag = this.activeLightSections.contains(sectionPosIn);

        if (!flag && !isEmpty)
        {
            this.addedActiveLightSections.add(sectionPosIn);
            this.scheduleUpdate(Long.MAX_VALUE, sectionPosIn, 0, true);
        }

        if (flag && isEmpty)
        {
            this.addedEmptySections.add(sectionPosIn);
            this.scheduleUpdate(Long.MAX_VALUE, sectionPosIn, 2, false);
        }
    }

    protected void processAllLevelUpdates()
    {
        if (this.needsUpdate())
        {
            this.processUpdates(Integer.MAX_VALUE);
        }
    }

    protected void updateAndNotify()
    {
        if (!this.dirtyCachedSections.isEmpty())
        {
            M m = this.cachedLightData.copy();
            m.disableCaching();
            this.uncachedLightData = m;
            this.dirtyCachedSections.clear();
        }

        if (!this.changedLightPositions.isEmpty())
        {
            LongIterator longiterator = this.changedLightPositions.iterator();

            while (longiterator.hasNext())
            {
                long i = longiterator.nextLong();
                this.chunkProvider.markLightChanged(this.type, SectionPos.from(i));
            }

            this.changedLightPositions.clear();
        }
    }
}
