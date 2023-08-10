package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;

public class EmptyChunk extends Chunk
{
    private static final Biome[] BIOMES = Util.make(new Biome[BiomeContainer.BIOMES_SIZE], (biomes) ->
    {
        Arrays.fill(biomes, BiomeRegistry.PLAINS);
    });

    public EmptyChunk(World worldIn, ChunkPos chunkPos)
    {
        super(worldIn, chunkPos, new BiomeContainer(worldIn.func_241828_r().getRegistry(Registry.BIOME_KEY), BIOMES));
    }

    public BlockState getBlockState(BlockPos pos)
    {
        return Blocks.VOID_AIR.getDefaultState();
    }

    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving)
    {
        return null;
    }

    public FluidState getFluidState(BlockPos pos)
    {
        return Fluids.EMPTY.getDefaultState();
    }

    @Nullable
    public WorldLightManager getWorldLightManager()
    {
        return null;
    }

    public int getLightValue(BlockPos pos)
    {
        return 0;
    }

    /**
     * Adds an entity to the chunk.
     */
    public void addEntity(Entity entityIn)
    {
    }

    /**
     * removes entity using its y chunk coordinate as its index
     */
    public void removeEntity(Entity entityIn)
    {
    }

    /**
     * Removes entity at the specified index from the entity array.
     */
    public void removeEntityAtIndex(Entity entityIn, int index)
    {
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType creationMode)
    {
        return null;
    }

    public void addTileEntity(TileEntity tileEntityIn)
    {
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
    }

    public void removeTileEntity(BlockPos pos)
    {
    }

    /**
     * Sets the isModified flag for this Chunk
     */
    public void markDirty()
    {
    }

    /**
     * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity.
     */
    public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate <? super Entity > filter)
    {
    }

    public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class <? extends T > entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate <? super T > filter)
    {
    }

    public boolean isEmpty()
    {
        return true;
    }

    /**
     * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty
     * (true) or not (false).
     */
    public boolean isEmptyBetween(int startY, int endY)
    {
        return true;
    }

    public ChunkHolder.LocationType getLocationType()
    {
        return ChunkHolder.LocationType.BORDER;
    }
}
