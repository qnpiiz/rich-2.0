package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public class SkyLightStorage extends SectionLightStorage<SkyLightStorage.StorageMap>
{
    private static final Direction[] field_215554_k = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet sectionsWithLight = new LongOpenHashSet();
    private final LongSet pendingAdditions = new LongOpenHashSet();
    private final LongSet pendingRemovals = new LongOpenHashSet();
    private final LongSet enabledColumns = new LongOpenHashSet();
    private volatile boolean hasPendingUpdates;

    protected SkyLightStorage(IChunkLightProvider lightProvider)
    {
        super(LightType.SKY, lightProvider, new SkyLightStorage.StorageMap(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    protected int getLightOrDefault(long worldPos)
    {
        long i = SectionPos.worldToSection(worldPos);
        int j = SectionPos.extractY(i);
        SkyLightStorage.StorageMap skylightstorage$storagemap = this.uncachedLightData;
        int k = skylightstorage$storagemap.surfaceSections.get(SectionPos.toSectionColumnPos(i));

        if (k != skylightstorage$storagemap.minY && j < k)
        {
            NibbleArray nibblearray = this.getArray(skylightstorage$storagemap, i);

            if (nibblearray == null)
            {
                for (worldPos = BlockPos.atSectionBottomY(worldPos); nibblearray == null; nibblearray = this.getArray(skylightstorage$storagemap, i))
                {
                    i = SectionPos.withOffset(i, Direction.UP);
                    ++j;

                    if (j >= k)
                    {
                        return 15;
                    }

                    worldPos = BlockPos.offset(worldPos, 0, 16, 0);
                }
            }

            return nibblearray.get(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)));
        }
        else
        {
            return 15;
        }
    }

    protected void addSection(long sectionPos)
    {
        int i = SectionPos.extractY(sectionPos);

        if ((this.cachedLightData).minY > i)
        {
            (this.cachedLightData).minY = i;
            (this.cachedLightData).surfaceSections.defaultReturnValue((this.cachedLightData).minY);
        }

        long j = SectionPos.toSectionColumnPos(sectionPos);
        int k = (this.cachedLightData).surfaceSections.get(j);

        if (k < i + 1)
        {
            (this.cachedLightData).surfaceSections.put(j, i + 1);

            if (this.enabledColumns.contains(j))
            {
                this.scheduleFullUpdate(sectionPos);

                if (k > (this.cachedLightData).minY)
                {
                    long l = SectionPos.asLong(SectionPos.extractX(sectionPos), k - 1, SectionPos.extractZ(sectionPos));
                    this.scheduleSurfaceUpdate(l);
                }

                this.updateHasPendingUpdates();
            }
        }
    }

    private void scheduleSurfaceUpdate(long p_223403_1_)
    {
        this.pendingRemovals.add(p_223403_1_);
        this.pendingAdditions.remove(p_223403_1_);
    }

    private void scheduleFullUpdate(long p_223404_1_)
    {
        this.pendingAdditions.add(p_223404_1_);
        this.pendingRemovals.remove(p_223404_1_);
    }

    private void updateHasPendingUpdates()
    {
        this.hasPendingUpdates = !this.pendingAdditions.isEmpty() || !this.pendingRemovals.isEmpty();
    }

    protected void removeSection(long p_215523_1_)
    {
        long i = SectionPos.toSectionColumnPos(p_215523_1_);
        boolean flag = this.enabledColumns.contains(i);

        if (flag)
        {
            this.scheduleSurfaceUpdate(p_215523_1_);
        }

        int j = SectionPos.extractY(p_215523_1_);

        if ((this.cachedLightData).surfaceSections.get(i) == j + 1)
        {
            long k;

            for (k = p_215523_1_; !this.hasSection(k) && this.isAboveBottom(j); k = SectionPos.withOffset(k, Direction.DOWN))
            {
                --j;
            }

            if (this.hasSection(k))
            {
                (this.cachedLightData).surfaceSections.put(i, j + 1);

                if (flag)
                {
                    this.scheduleFullUpdate(k);
                }
            }
            else
            {
                (this.cachedLightData).surfaceSections.remove(i);
            }
        }

        if (flag)
        {
            this.updateHasPendingUpdates();
        }
    }

    protected void setColumnEnabled(long p_215526_1_, boolean p_215526_3_)
    {
        this.processAllLevelUpdates();

        if (p_215526_3_ && this.enabledColumns.add(p_215526_1_))
        {
            int i = (this.cachedLightData).surfaceSections.get(p_215526_1_);

            if (i != (this.cachedLightData).minY)
            {
                long j = SectionPos.asLong(SectionPos.extractX(p_215526_1_), i - 1, SectionPos.extractZ(p_215526_1_));
                this.scheduleFullUpdate(j);
                this.updateHasPendingUpdates();
            }
        }
        else if (!p_215526_3_)
        {
            this.enabledColumns.remove(p_215526_1_);
        }
    }

    protected boolean hasSectionsToUpdate()
    {
        return super.hasSectionsToUpdate() || this.hasPendingUpdates;
    }

    protected NibbleArray getOrCreateArray(long sectionPosIn)
    {
        NibbleArray nibblearray = this.newArrays.get(sectionPosIn);

        if (nibblearray != null)
        {
            return nibblearray;
        }
        else
        {
            long i = SectionPos.withOffset(sectionPosIn, Direction.UP);
            int j = (this.cachedLightData).surfaceSections.get(SectionPos.toSectionColumnPos(sectionPosIn));

            if (j != (this.cachedLightData).minY && SectionPos.extractY(i) < j)
            {
                NibbleArray nibblearray1;

                while ((nibblearray1 = this.getArray(i, true)) == null)
                {
                    i = SectionPos.withOffset(i, Direction.UP);
                }

                return new NibbleArray((new NibbleArrayRepeater(nibblearray1, 0)).getData());
            }
            else
            {
                return new NibbleArray();
            }
        }
    }

    protected void updateSections(LightEngine < SkyLightStorage.StorageMap, ? > engine, boolean updateSkyLight, boolean updateBlockLight)
    {
        super.updateSections(engine, updateSkyLight, updateBlockLight);

        if (updateSkyLight)
        {
            if (!this.pendingAdditions.isEmpty())
            {
                for (long i : this.pendingAdditions)
                {
                    int j = this.getLevel(i);

                    if (j != 2 && !this.pendingRemovals.contains(i) && this.sectionsWithLight.add(i))
                    {
                        if (j == 1)
                        {
                            this.cancelSectionUpdates(engine, i);

                            if (this.dirtyCachedSections.add(i))
                            {
                                this.cachedLightData.copyArray(i);
                            }

                            Arrays.fill(this.getArray(i, true).getData(), (byte) - 1);
                            int i3 = SectionPos.toWorld(SectionPos.extractX(i));
                            int k3 = SectionPos.toWorld(SectionPos.extractY(i));
                            int i4 = SectionPos.toWorld(SectionPos.extractZ(i));

                            for (Direction direction : field_215554_k)
                            {
                                long j1 = SectionPos.withOffset(i, direction);

                                if ((this.pendingRemovals.contains(j1) || !this.sectionsWithLight.contains(j1) && !this.pendingAdditions.contains(j1)) && this.hasSection(j1))
                                {
                                    for (int k1 = 0; k1 < 16; ++k1)
                                    {
                                        for (int l1 = 0; l1 < 16; ++l1)
                                        {
                                            long i2;
                                            long j2;

                                            switch (direction)
                                            {
                                                case NORTH:
                                                    i2 = BlockPos.pack(i3 + k1, k3 + l1, i4);
                                                    j2 = BlockPos.pack(i3 + k1, k3 + l1, i4 - 1);
                                                    break;

                                                case SOUTH:
                                                    i2 = BlockPos.pack(i3 + k1, k3 + l1, i4 + 16 - 1);
                                                    j2 = BlockPos.pack(i3 + k1, k3 + l1, i4 + 16);
                                                    break;

                                                case WEST:
                                                    i2 = BlockPos.pack(i3, k3 + k1, i4 + l1);
                                                    j2 = BlockPos.pack(i3 - 1, k3 + k1, i4 + l1);
                                                    break;

                                                default:
                                                    i2 = BlockPos.pack(i3 + 16 - 1, k3 + k1, i4 + l1);
                                                    j2 = BlockPos.pack(i3 + 16, k3 + k1, i4 + l1);
                                            }

                                            engine.scheduleUpdate(i2, j2, engine.getEdgeLevel(i2, j2, 0), true);
                                        }
                                    }
                                }
                            }

                            for (int j4 = 0; j4 < 16; ++j4)
                            {
                                for (int k4 = 0; k4 < 16; ++k4)
                                {
                                    long l4 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + j4, SectionPos.toWorld(SectionPos.extractY(i)), SectionPos.toWorld(SectionPos.extractZ(i)) + k4);
                                    long i5 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + j4, SectionPos.toWorld(SectionPos.extractY(i)) - 1, SectionPos.toWorld(SectionPos.extractZ(i)) + k4);
                                    engine.scheduleUpdate(l4, i5, engine.getEdgeLevel(l4, i5, 0), true);
                                }
                            }
                        }
                        else
                        {
                            for (int k = 0; k < 16; ++k)
                            {
                                for (int l = 0; l < 16; ++l)
                                {
                                    long i1 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(i)) + k, SectionPos.toWorld(SectionPos.extractY(i)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(i)) + l);
                                    engine.scheduleUpdate(Long.MAX_VALUE, i1, 0, true);
                                }
                            }
                        }
                    }
                }
            }

            this.pendingAdditions.clear();

            if (!this.pendingRemovals.isEmpty())
            {
                for (long k2 : this.pendingRemovals)
                {
                    if (this.sectionsWithLight.remove(k2) && this.hasSection(k2))
                    {
                        for (int l2 = 0; l2 < 16; ++l2)
                        {
                            for (int j3 = 0; j3 < 16; ++j3)
                            {
                                long l3 = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(k2)) + l2, SectionPos.toWorld(SectionPos.extractY(k2)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(k2)) + j3);
                                engine.scheduleUpdate(Long.MAX_VALUE, l3, 15, false);
                            }
                        }
                    }
                }
            }

            this.pendingRemovals.clear();
            this.hasPendingUpdates = false;
        }
    }

    protected boolean isAboveBottom(int p_215550_1_)
    {
        return p_215550_1_ >= (this.cachedLightData).minY;
    }

    protected boolean func_215551_l(long p_215551_1_)
    {
        int i = BlockPos.unpackY(p_215551_1_);

        if ((i & 15) != 15)
        {
            return false;
        }
        else
        {
            long j = SectionPos.worldToSection(p_215551_1_);
            long k = SectionPos.toSectionColumnPos(j);

            if (!this.enabledColumns.contains(k))
            {
                return false;
            }
            else
            {
                int l = (this.cachedLightData).surfaceSections.get(k);
                return SectionPos.toWorld(l) == i + 16;
            }
        }
    }

    protected boolean isAboveWorld(long p_215549_1_)
    {
        long i = SectionPos.toSectionColumnPos(p_215549_1_);
        int j = (this.cachedLightData).surfaceSections.get(i);
        return j == (this.cachedLightData).minY || SectionPos.extractY(p_215549_1_) >= j;
    }

    protected boolean isSectionEnabled(long p_215548_1_)
    {
        long i = SectionPos.toSectionColumnPos(p_215548_1_);
        return this.enabledColumns.contains(i);
    }

    public static final class StorageMap extends LightDataMap<SkyLightStorage.StorageMap>
    {
        private int minY;
        private final Long2IntOpenHashMap surfaceSections;

        public StorageMap(Long2ObjectOpenHashMap<NibbleArray> p_i50496_1_, Long2IntOpenHashMap p_i50496_2_, int p_i50496_3_)
        {
            super(p_i50496_1_);
            this.surfaceSections = p_i50496_2_;
            p_i50496_2_.defaultReturnValue(p_i50496_3_);
            this.minY = p_i50496_3_;
        }

        public SkyLightStorage.StorageMap copy()
        {
            return new SkyLightStorage.StorageMap(this.arrays.clone(), this.surfaceSections.clone(), this.minY);
        }
    }
}
