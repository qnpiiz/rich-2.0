package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer implements IChunk
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkPos pos;
    private volatile boolean modified;
    @Nullable
    private BiomeContainer biomes;
    @Nullable
    private volatile WorldLightManager lightManager;
    private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private volatile ChunkStatus status = ChunkStatus.EMPTY;
    private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
    private final Map<BlockPos, CompoundNBT> deferredTileEntities = Maps.newHashMap();
    private final ChunkSection[] sections = new ChunkSection[16];
    private final List<CompoundNBT> entities = Lists.newArrayList();
    private final List<BlockPos> lightPositions = Lists.newArrayList();
    private final ShortList[] packedPositions = new ShortList[16];
    private final Map < Structure<?>, StructureStart<? >> structureStartMap = Maps.newHashMap();
    private final Map < Structure<?>, LongSet > structureReferenceMap = Maps.newHashMap();
    private final UpgradeData upgradeData;
    private final ChunkPrimerTickList<Block> pendingBlockTicks;
    private final ChunkPrimerTickList<Fluid> pendingFluidTicks;
    private long inhabitedTime;
    private final Map<GenerationStage.Carving, BitSet> carvingMasks = new Object2ObjectArrayMap<>();
    private volatile boolean hasLight;

    public ChunkPrimer(ChunkPos pos, UpgradeData data)
    {
        this(pos, data, (ChunkSection[])null, new ChunkPrimerTickList<>((block) ->
        {
            return block == null || block.getDefaultState().isAir();
        }, pos), new ChunkPrimerTickList<>((fluid) ->
        {
            return fluid == null || fluid == Fluids.EMPTY;
        }, pos));
    }

    public ChunkPrimer(ChunkPos pos, UpgradeData upgradeData, @Nullable ChunkSection[] sections, ChunkPrimerTickList<Block> pendingBlockTicks, ChunkPrimerTickList<Fluid> pendingFluidTicks)
    {
        this.pos = pos;
        this.upgradeData = upgradeData;
        this.pendingBlockTicks = pendingBlockTicks;
        this.pendingFluidTicks = pendingFluidTicks;

        if (sections != null)
        {
            if (this.sections.length == sections.length)
            {
                System.arraycopy(sections, 0, this.sections, 0, this.sections.length);
            }
            else
            {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", sections.length, this.sections.length);
            }
        }
    }

    public BlockState getBlockState(BlockPos pos)
    {
        int i = pos.getY();

        if (World.isYOutOfBounds(i))
        {
            return Blocks.VOID_AIR.getDefaultState();
        }
        else
        {
            ChunkSection chunksection = this.getSections()[i >> 4];
            return ChunkSection.isEmpty(chunksection) ? Blocks.AIR.getDefaultState() : chunksection.getBlockState(pos.getX() & 15, i & 15, pos.getZ() & 15);
        }
    }

    public FluidState getFluidState(BlockPos pos)
    {
        int i = pos.getY();

        if (World.isYOutOfBounds(i))
        {
            return Fluids.EMPTY.getDefaultState();
        }
        else
        {
            ChunkSection chunksection = this.getSections()[i >> 4];
            return ChunkSection.isEmpty(chunksection) ? Fluids.EMPTY.getDefaultState() : chunksection.getFluidState(pos.getX() & 15, i & 15, pos.getZ() & 15);
        }
    }

    public Stream<BlockPos> getLightSources()
    {
        return this.lightPositions.stream();
    }

    public ShortList[] getPackedLightPositions()
    {
        ShortList[] ashortlist = new ShortList[16];

        for (BlockPos blockpos : this.lightPositions)
        {
            IChunk.getList(ashortlist, blockpos.getY() >> 4).add(packToLocal(blockpos));
        }

        return ashortlist;
    }

    public void addLightValue(short packedPosition, int lightValue)
    {
        this.addLightPosition(unpackToWorld(packedPosition, lightValue, this.pos));
    }

    public void addLightPosition(BlockPos lightPos)
    {
        this.lightPositions.add(lightPos.toImmutable());
    }

    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving)
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        if (j >= 0 && j < 256)
        {
            if (this.sections[j >> 4] == Chunk.EMPTY_SECTION && state.isIn(Blocks.AIR))
            {
                return state;
            }
            else
            {
                if (state.getLightValue() > 0)
                {
                    this.lightPositions.add(new BlockPos((i & 15) + this.getPos().getXStart(), j, (k & 15) + this.getPos().getZStart()));
                }

                ChunkSection chunksection = this.getSection(j >> 4);
                BlockState blockstate = chunksection.setBlockState(i & 15, j & 15, k & 15, state);

                if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockstate && (state.getOpacity(this, pos) != blockstate.getOpacity(this, pos) || state.getLightValue() != blockstate.getLightValue() || state.isTransparent() || blockstate.isTransparent()))
                {
                    WorldLightManager worldlightmanager = this.getWorldLightManager();
                    worldlightmanager.checkBlock(pos);
                }

                EnumSet<Heightmap.Type> enumset1 = this.getStatus().getHeightMaps();
                EnumSet<Heightmap.Type> enumset = null;

                for (Heightmap.Type heightmap$type : enumset1)
                {
                    Heightmap heightmap = this.heightmaps.get(heightmap$type);

                    if (heightmap == null)
                    {
                        if (enumset == null)
                        {
                            enumset = EnumSet.noneOf(Heightmap.Type.class);
                        }

                        enumset.add(heightmap$type);
                    }
                }

                if (enumset != null)
                {
                    Heightmap.updateChunkHeightmaps(this, enumset);
                }

                for (Heightmap.Type heightmap$type1 : enumset1)
                {
                    this.heightmaps.get(heightmap$type1).update(i & 15, j, k & 15, state);
                }

                return blockstate;
            }
        }
        else
        {
            return Blocks.VOID_AIR.getDefaultState();
        }
    }

    public ChunkSection getSection(int sectionId)
    {
        if (this.sections[sectionId] == Chunk.EMPTY_SECTION)
        {
            this.sections[sectionId] = new ChunkSection(sectionId << 4);
        }

        return this.sections[sectionId];
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
        tileEntityIn.setPos(pos);
        this.tileEntities.put(pos, tileEntityIn);
    }

    public Set<BlockPos> getTileEntitiesPos()
    {
        Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
        set.addAll(this.tileEntities.keySet());
        return set;
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos)
    {
        return this.tileEntities.get(pos);
    }

    public Map<BlockPos, TileEntity> getTileEntities()
    {
        return this.tileEntities;
    }

    public void addEntity(CompoundNBT entityCompound)
    {
        this.entities.add(entityCompound);
    }

    /**
     * Adds an entity to the chunk.
     */
    public void addEntity(Entity entityIn)
    {
        if (!entityIn.isPassenger())
        {
            CompoundNBT compoundnbt = new CompoundNBT();
            entityIn.writeUnlessPassenger(compoundnbt);
            this.addEntity(compoundnbt);
        }
    }

    public List<CompoundNBT> getEntities()
    {
        return this.entities;
    }

    public void setBiomes(BiomeContainer biomes)
    {
        this.biomes = biomes;
    }

    @Nullable
    public BiomeContainer getBiomes()
    {
        return this.biomes;
    }

    public void setModified(boolean modified)
    {
        this.modified = modified;
    }

    public boolean isModified()
    {
        return this.modified;
    }

    public ChunkStatus getStatus()
    {
        return this.status;
    }

    public void setStatus(ChunkStatus status)
    {
        this.status = status;
        this.setModified(true);
    }

    public ChunkSection[] getSections()
    {
        return this.sections;
    }

    @Nullable
    public WorldLightManager getWorldLightManager()
    {
        return this.lightManager;
    }

    public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps()
    {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    public void setHeightmap(Heightmap.Type type, long[] data)
    {
        this.getHeightmap(type).setDataArray(data);
    }

    public Heightmap getHeightmap(Heightmap.Type typeIn)
    {
        return this.heightmaps.computeIfAbsent(typeIn, (type) ->
        {
            return new Heightmap(this, type);
        });
    }

    public int getTopBlockY(Heightmap.Type heightmapType, int x, int z)
    {
        Heightmap heightmap = this.heightmaps.get(heightmapType);

        if (heightmap == null)
        {
            Heightmap.updateChunkHeightmaps(this, EnumSet.of(heightmapType));
            heightmap = this.heightmaps.get(heightmapType);
        }

        return heightmap.getHeight(x & 15, z & 15) - 1;
    }

    /**
     * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
     */
    public ChunkPos getPos()
    {
        return this.pos;
    }

    public void setLastSaveTime(long saveTime)
    {
    }

    @Nullable
    public StructureStart<?> func_230342_a_(Structure<?> p_230342_1_)
    {
        return this.structureStartMap.get(p_230342_1_);
    }

    public void func_230344_a_(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_)
    {
        this.structureStartMap.put(p_230344_1_, p_230344_2_);
        this.modified = true;
    }

    public Map < Structure<?>, StructureStart<? >> getStructureStarts()
    {
        return Collections.unmodifiableMap(this.structureStartMap);
    }

    public void setStructureStarts(Map < Structure<?>, StructureStart<? >> structureStartsIn)
    {
        this.structureStartMap.clear();
        this.structureStartMap.putAll(structureStartsIn);
        this.modified = true;
    }

    public LongSet func_230346_b_(Structure<?> p_230346_1_)
    {
        return this.structureReferenceMap.computeIfAbsent(p_230346_1_, (structureIn) ->
        {
            return new LongOpenHashSet();
        });
    }

    public void func_230343_a_(Structure<?> p_230343_1_, long p_230343_2_)
    {
        this.structureReferenceMap.computeIfAbsent(p_230343_1_, (structureIn) ->
        {
            return new LongOpenHashSet();
        }).add(p_230343_2_);
        this.modified = true;
    }

    public Map < Structure<?>, LongSet > getStructureReferences()
    {
        return Collections.unmodifiableMap(this.structureReferenceMap);
    }

    public void setStructureReferences(Map < Structure<?>, LongSet > structureReferences)
    {
        this.structureReferenceMap.clear();
        this.structureReferenceMap.putAll(structureReferences);
        this.modified = true;
    }

    public static short packToLocal(BlockPos pos)
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        int l = i & 15;
        int i1 = j & 15;
        int j1 = k & 15;
        return (short)(l | i1 << 4 | j1 << 8);
    }

    public static BlockPos unpackToWorld(short packedPos, int yOffset, ChunkPos chunkPosIn)
    {
        int i = (packedPos & 15) + (chunkPosIn.x << 4);
        int j = (packedPos >>> 4 & 15) + (yOffset << 4);
        int k = (packedPos >>> 8 & 15) + (chunkPosIn.z << 4);
        return new BlockPos(i, j, k);
    }

    public void markBlockForPostprocessing(BlockPos pos)
    {
        if (!World.isOutsideBuildHeight(pos))
        {
            IChunk.getList(this.packedPositions, pos.getY() >> 4).add(packToLocal(pos));
        }
    }

    public ShortList[] getPackedPositions()
    {
        return this.packedPositions;
    }

    public void addPackedPosition(short packedPosition, int index)
    {
        IChunk.getList(this.packedPositions, index).add(packedPosition);
    }

    public ChunkPrimerTickList<Block> getBlocksToBeTicked()
    {
        return this.pendingBlockTicks;
    }

    public ChunkPrimerTickList<Fluid> getFluidsToBeTicked()
    {
        return this.pendingFluidTicks;
    }

    public UpgradeData getUpgradeData()
    {
        return this.upgradeData;
    }

    public void setInhabitedTime(long newInhabitedTime)
    {
        this.inhabitedTime = newInhabitedTime;
    }

    public long getInhabitedTime()
    {
        return this.inhabitedTime;
    }

    public void addTileEntity(CompoundNBT nbt)
    {
        this.deferredTileEntities.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
    }

    public Map<BlockPos, CompoundNBT> getDeferredTileEntities()
    {
        return Collections.unmodifiableMap(this.deferredTileEntities);
    }

    public CompoundNBT getDeferredTileEntity(BlockPos pos)
    {
        return this.deferredTileEntities.get(pos);
    }

    @Nullable
    public CompoundNBT getTileEntityNBT(BlockPos pos)
    {
        TileEntity tileentity = this.getTileEntity(pos);
        return tileentity != null ? tileentity.write(new CompoundNBT()) : this.deferredTileEntities.get(pos);
    }

    public void removeTileEntity(BlockPos pos)
    {
        this.tileEntities.remove(pos);
        this.deferredTileEntities.remove(pos);
    }

    @Nullable
    public BitSet getCarvingMask(GenerationStage.Carving type)
    {
        return this.carvingMasks.get(type);
    }

    public BitSet getOrAddCarvingMask(GenerationStage.Carving type)
    {
        return this.carvingMasks.computeIfAbsent(type, (typeIn) ->
        {
            return new BitSet(65536);
        });
    }

    public void setCarvingMask(GenerationStage.Carving type, BitSet mask)
    {
        this.carvingMasks.put(type, mask);
    }

    public void setLightManager(WorldLightManager lightManager)
    {
        this.lightManager = lightManager;
    }

    public boolean hasLight()
    {
        return this.hasLight;
    }

    public void setLight(boolean lightCorrectIn)
    {
        this.hasLight = lightCorrectIn;
        this.setModified(true);
    }
}
