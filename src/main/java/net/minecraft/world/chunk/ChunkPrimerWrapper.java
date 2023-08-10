package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;

public class ChunkPrimerWrapper extends ChunkPrimer
{
    private final Chunk chunk;

    public ChunkPrimerWrapper(Chunk chunk)
    {
        super(chunk.getPos(), UpgradeData.EMPTY);
        this.chunk = chunk;
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos)
    {
        return this.chunk.getTileEntity(pos);
    }

    @Nullable
    public BlockState getBlockState(BlockPos pos)
    {
        return this.chunk.getBlockState(pos);
    }

    public FluidState getFluidState(BlockPos pos)
    {
        return this.chunk.getFluidState(pos);
    }

    public int getMaxLightLevel()
    {
        return this.chunk.getMaxLightLevel();
    }

    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving)
    {
        return null;
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
    }

    /**
     * Adds an entity to the chunk.
     */
    public void addEntity(Entity entityIn)
    {
    }

    public void setStatus(ChunkStatus status)
    {
    }

    public ChunkSection[] getSections()
    {
        return this.chunk.getSections();
    }

    @Nullable
    public WorldLightManager getWorldLightManager()
    {
        return this.chunk.getWorldLightManager();
    }

    public void setHeightmap(Heightmap.Type type, long[] data)
    {
    }

    private Heightmap.Type func_209532_c(Heightmap.Type type)
    {
        if (type == Heightmap.Type.WORLD_SURFACE_WG)
        {
            return Heightmap.Type.WORLD_SURFACE;
        }
        else
        {
            return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
        }
    }

    public int getTopBlockY(Heightmap.Type heightmapType, int x, int z)
    {
        return this.chunk.getTopBlockY(this.func_209532_c(heightmapType), x, z);
    }

    /**
     * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
     */
    public ChunkPos getPos()
    {
        return this.chunk.getPos();
    }

    public void setLastSaveTime(long saveTime)
    {
    }

    @Nullable
    public StructureStart<?> func_230342_a_(Structure<?> p_230342_1_)
    {
        return this.chunk.func_230342_a_(p_230342_1_);
    }

    public void func_230344_a_(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_)
    {
    }

    public Map < Structure<?>, StructureStart<? >> getStructureStarts()
    {
        return this.chunk.getStructureStarts();
    }

    public void setStructureStarts(Map < Structure<?>, StructureStart<? >> structureStartsIn)
    {
    }

    public LongSet func_230346_b_(Structure<?> p_230346_1_)
    {
        return this.chunk.func_230346_b_(p_230346_1_);
    }

    public void func_230343_a_(Structure<?> p_230343_1_, long p_230343_2_)
    {
    }

    public Map < Structure<?>, LongSet > getStructureReferences()
    {
        return this.chunk.getStructureReferences();
    }

    public void setStructureReferences(Map < Structure<?>, LongSet > structureReferences)
    {
    }

    public BiomeContainer getBiomes()
    {
        return this.chunk.getBiomes();
    }

    public void setModified(boolean modified)
    {
    }

    public boolean isModified()
    {
        return false;
    }

    public ChunkStatus getStatus()
    {
        return this.chunk.getStatus();
    }

    public void removeTileEntity(BlockPos pos)
    {
    }

    public void markBlockForPostprocessing(BlockPos pos)
    {
    }

    public void addTileEntity(CompoundNBT nbt)
    {
    }

    @Nullable
    public CompoundNBT getDeferredTileEntity(BlockPos pos)
    {
        return this.chunk.getDeferredTileEntity(pos);
    }

    @Nullable
    public CompoundNBT getTileEntityNBT(BlockPos pos)
    {
        return this.chunk.getTileEntityNBT(pos);
    }

    public void setBiomes(BiomeContainer biomes)
    {
    }

    public Stream<BlockPos> getLightSources()
    {
        return this.chunk.getLightSources();
    }

    public ChunkPrimerTickList<Block> getBlocksToBeTicked()
    {
        return new ChunkPrimerTickList<>((block) ->
        {
            return block.getDefaultState().isAir();
        }, this.getPos());
    }

    public ChunkPrimerTickList<Fluid> getFluidsToBeTicked()
    {
        return new ChunkPrimerTickList<>((fluid) ->
        {
            return fluid == Fluids.EMPTY;
        }, this.getPos());
    }

    public BitSet getCarvingMask(GenerationStage.Carving type)
    {
        throw(UnsupportedOperationException)Util.pauseDevMode(new UnsupportedOperationException("Meaningless in this context"));
    }

    public BitSet getOrAddCarvingMask(GenerationStage.Carving type)
    {
        throw(UnsupportedOperationException)Util.pauseDevMode(new UnsupportedOperationException("Meaningless in this context"));
    }

    public Chunk getChunk()
    {
        return this.chunk;
    }

    public boolean hasLight()
    {
        return this.chunk.hasLight();
    }

    public void setLight(boolean lightCorrectIn)
    {
        this.chunk.setLight(lightCorrectIn);
    }
}
