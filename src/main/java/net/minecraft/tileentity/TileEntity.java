package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TileEntity
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final TileEntityType<?> type;
    @Nullable

    /** the instance of the world the tile entity is in. */
    protected World world;
    protected BlockPos pos = BlockPos.ZERO;
    protected boolean removed;
    @Nullable
    private BlockState cachedBlockState;
    private boolean warnedInvalidBlock;

    public TileEntity(TileEntityType<?> tileEntityTypeIn)
    {
        this.type = tileEntityTypeIn;
    }

    @Nullable

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorld()
    {
        return this.world;
    }

    public void setWorldAndPos(World world, BlockPos pos)
    {
        this.world = world;
        this.pos = pos.toImmutable();
    }

    /**
     * Returns true if the worldObj isn't null.
     */
    public boolean hasWorld()
    {
        return this.world != null;
    }

    public void read(BlockState state, CompoundNBT nbt)
    {
        this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    public CompoundNBT write(CompoundNBT compound)
    {
        return this.writeInternal(compound);
    }

    private CompoundNBT writeInternal(CompoundNBT compound)
    {
        ResourceLocation resourcelocation = TileEntityType.getId(this.getType());

        if (resourcelocation == null)
        {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        else
        {
            compound.putString("id", resourcelocation.toString());
            compound.putInt("x", this.pos.getX());
            compound.putInt("y", this.pos.getY());
            compound.putInt("z", this.pos.getZ());
            return compound;
        }
    }

    @Nullable
    public static TileEntity readTileEntity(BlockState state, CompoundNBT nbt)
    {
        String s = nbt.getString("id");
        return Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(s)).map((type) ->
        {
            try {
                return type.create();
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Failed to create block entity {}", s, throwable);
                return null;
            }
        }).map((tileEntity) ->
        {
            try {
                tileEntity.read(state, nbt);
                return tileEntity;
            }
            catch (Throwable throwable)
            {
                LOGGER.error("Failed to load data for block entity {}", s, throwable);
                return null;
            }
        }).orElseGet(() ->
        {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
            return null;
        });
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        if (this.world != null)
        {
            this.cachedBlockState = this.world.getBlockState(this.pos);
            this.world.markChunkDirty(this.pos, this);

            if (!this.cachedBlockState.isAir())
            {
                this.world.updateComparatorOutputLevel(this.pos, this.cachedBlockState.getBlock());
            }
        }
    }

    public double getMaxRenderDistanceSquared()
    {
        return 64.0D;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public BlockState getBlockState()
    {
        if (this.cachedBlockState == null)
        {
            this.cachedBlockState = this.world.getBlockState(this.pos);
        }

        return this.cachedBlockState;
    }

    @Nullable

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return null;
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
     */
    public CompoundNBT getUpdateTag()
    {
        return this.writeInternal(new CompoundNBT());
    }

    public boolean isRemoved()
    {
        return this.removed;
    }

    /**
     * invalidates a tile entity
     */
    public void remove()
    {
        this.removed = true;
    }

    /**
     * validates a tile entity
     */
    public void validate()
    {
        this.removed = false;
    }

    /**
     * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
     * clientside.
     */
    public boolean receiveClientEvent(int id, int type)
    {
        return false;
    }

    public void updateContainingBlockInfo()
    {
        this.cachedBlockState = null;
    }

    public void addInfoToCrashReport(CrashReportCategory reportCategory)
    {
        reportCategory.addDetail("Name", () ->
        {
            return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
        });

        if (this.world != null)
        {
            CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.getBlockState());
            CrashReportCategory.addBlockInfo(reportCategory, this.pos, this.world.getBlockState(this.pos));
        }
    }

    public void setPos(BlockPos posIn)
    {
        this.pos = posIn.toImmutable();
    }

    /**
     * Checks if players can use this tile entity to access operator (permission level 2) commands either directly or
     * indirectly, such as give or setblock. A similar method exists for entities at {@link
     * net.minecraft.entity.Entity#ignoreItemEntityData()}.<p>For example, {@link
     * net.minecraft.tileentity.TileEntitySign#onlyOpsCanSetNbt() signs} (player right-clicking) and {@link
     * net.minecraft.tileentity.TileEntityCommandBlock#onlyOpsCanSetNbt() command blocks} are considered
     * accessible.</p>@return true if this block entity offers ways for unauthorized players to use restricted commands
     */
    public boolean onlyOpsCanSetNbt()
    {
        return false;
    }

    public void rotate(Rotation rotationIn)
    {
    }

    public void mirror(Mirror mirrorIn)
    {
    }

    public TileEntityType<?> getType()
    {
        return this.type;
    }

    public void warnInvalidBlock()
    {
        if (!this.warnedInvalidBlock)
        {
            this.warnedInvalidBlock = true;
            LOGGER.warn("Block entity invalid: {} @ {}", () ->
            {
                return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType());
            }, this::getPos);
        }
    }
}
