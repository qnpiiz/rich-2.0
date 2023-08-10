package net.minecraft.tileentity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndGatewayTileEntity extends EndPortalTileEntity implements ITickableTileEntity
{
    private static final Logger LOGGER = LogManager.getLogger();
    private long age;
    private int teleportCooldown;
    @Nullable
    private BlockPos exitPortal;
    private boolean exactTeleport;

    public EndGatewayTileEntity()
    {
        super(TileEntityType.END_GATEWAY);
    }

    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        compound.putLong("Age", this.age);

        if (this.exitPortal != null)
        {
            compound.put("ExitPortal", NBTUtil.writeBlockPos(this.exitPortal));
        }

        if (this.exactTeleport)
        {
            compound.putBoolean("ExactTeleport", this.exactTeleport);
        }

        return compound;
    }

    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.age = nbt.getLong("Age");

        if (nbt.contains("ExitPortal", 10))
        {
            this.exitPortal = NBTUtil.readBlockPos(nbt.getCompound("ExitPortal"));
        }

        this.exactTeleport = nbt.getBoolean("ExactTeleport");
    }

    public double getMaxRenderDistanceSquared()
    {
        return 256.0D;
    }

    public void tick()
    {
        boolean flag = this.isSpawning();
        boolean flag1 = this.isCoolingDown();
        ++this.age;

        if (flag1)
        {
            --this.teleportCooldown;
        }
        else if (!this.world.isRemote)
        {
            List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos()), EndGatewayTileEntity::func_242690_a);

            if (!list.isEmpty())
            {
                this.teleportEntity(list.get(this.world.rand.nextInt(list.size())));
            }

            if (this.age % 2400L == 0L)
            {
                this.triggerCooldown();
            }
        }

        if (flag != this.isSpawning() || flag1 != this.isCoolingDown())
        {
            this.markDirty();
        }
    }

    public static boolean func_242690_a(Entity p_242690_0_)
    {
        return EntityPredicates.NOT_SPECTATING.test(p_242690_0_) && !p_242690_0_.getLowestRidingEntity().func_242280_ah();
    }

    public boolean isSpawning()
    {
        return this.age < 200L;
    }

    public boolean isCoolingDown()
    {
        return this.teleportCooldown > 0;
    }

    public float getSpawnPercent(float partialTicks)
    {
        return MathHelper.clamp(((float)this.age + partialTicks) / 200.0F, 0.0F, 1.0F);
    }

    public float getCooldownPercent(float partialTicks)
    {
        return 1.0F - MathHelper.clamp(((float)this.teleportCooldown - partialTicks) / 40.0F, 0.0F, 1.0F);
    }

    @Nullable

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 8, this.getUpdateTag());
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
     */
    public CompoundNBT getUpdateTag()
    {
        return this.write(new CompoundNBT());
    }

    public void triggerCooldown()
    {
        if (!this.world.isRemote)
        {
            this.teleportCooldown = 40;
            this.world.addBlockEvent(this.getPos(), this.getBlockState().getBlock(), 1, 0);
            this.markDirty();
        }
    }

    /**
     * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
     * clientside.
     */
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.teleportCooldown = 40;
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    public void teleportEntity(Entity entityIn)
    {
        if (this.world instanceof ServerWorld && !this.isCoolingDown())
        {
            this.teleportCooldown = 100;

            if (this.exitPortal == null && this.world.getDimensionKey() == World.THE_END)
            {
                this.func_227015_a_((ServerWorld)this.world);
            }

            if (this.exitPortal != null)
            {
                BlockPos blockpos = this.exactTeleport ? this.exitPortal : this.findExitPosition();
                Entity entity;

                if (entityIn instanceof EnderPearlEntity)
                {
                    Entity entity1 = ((EnderPearlEntity)entityIn).func_234616_v_();

                    if (entity1 instanceof ServerPlayerEntity)
                    {
                        CriteriaTriggers.ENTER_BLOCK.trigger((ServerPlayerEntity)entity1, this.world.getBlockState(this.getPos()));
                    }

                    if (entity1 != null)
                    {
                        entity = entity1;
                        entityIn.remove();
                    }
                    else
                    {
                        entity = entityIn;
                    }
                }
                else
                {
                    entity = entityIn.getLowestRidingEntity();
                }

                entity.func_242279_ag();
                entity.teleportKeepLoaded((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
            }

            this.triggerCooldown();
        }
    }

    private BlockPos findExitPosition()
    {
        BlockPos blockpos = findHighestBlock(this.world, this.exitPortal.add(0, 2, 0), 5, false);
        LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortal, blockpos);
        return blockpos.up();
    }

    private void func_227015_a_(ServerWorld p_227015_1_)
    {
        Vector3d vector3d = (new Vector3d((double)this.getPos().getX(), 0.0D, (double)this.getPos().getZ())).normalize();
        Vector3d vector3d1 = vector3d.scale(1024.0D);

        for (int i = 16; getChunk(p_227015_1_, vector3d1).getTopFilledSegment() > 0 && i-- > 0; vector3d1 = vector3d1.add(vector3d.scale(-16.0D)))
        {
            LOGGER.debug("Skipping backwards past nonempty chunk at {}", (Object)vector3d1);
        }

        for (int j = 16; getChunk(p_227015_1_, vector3d1).getTopFilledSegment() == 0 && j-- > 0; vector3d1 = vector3d1.add(vector3d.scale(16.0D)))
        {
            LOGGER.debug("Skipping forward past empty chunk at {}", (Object)vector3d1);
        }

        LOGGER.debug("Found chunk at {}", (Object)vector3d1);
        Chunk chunk = getChunk(p_227015_1_, vector3d1);
        this.exitPortal = findSpawnpointInChunk(chunk);

        if (this.exitPortal == null)
        {
            this.exitPortal = new BlockPos(vector3d1.x + 0.5D, 75.0D, vector3d1.z + 0.5D);
            LOGGER.debug("Failed to find suitable block, settling on {}", (Object)this.exitPortal);
            Features.END_ISLAND.func_242765_a(p_227015_1_, p_227015_1_.getChunkProvider().getChunkGenerator(), new Random(this.exitPortal.toLong()), this.exitPortal);
        }
        else
        {
            LOGGER.debug("Found block at {}", (Object)this.exitPortal);
        }

        this.exitPortal = findHighestBlock(p_227015_1_, this.exitPortal, 16, true);
        LOGGER.debug("Creating portal at {}", (Object)this.exitPortal);
        this.exitPortal = this.exitPortal.up(10);
        this.func_227016_a_(p_227015_1_, this.exitPortal);
        this.markDirty();
    }

    private static BlockPos findHighestBlock(IBlockReader worldIn, BlockPos posIn, int radius, boolean allowBedrock)
    {
        BlockPos blockpos = null;

        for (int i = -radius; i <= radius; ++i)
        {
            for (int j = -radius; j <= radius; ++j)
            {
                if (i != 0 || j != 0 || allowBedrock)
                {
                    for (int k = 255; k > (blockpos == null ? 0 : blockpos.getY()); --k)
                    {
                        BlockPos blockpos1 = new BlockPos(posIn.getX() + i, k, posIn.getZ() + j);
                        BlockState blockstate = worldIn.getBlockState(blockpos1);

                        if (blockstate.hasOpaqueCollisionShape(worldIn, blockpos1) && (allowBedrock || !blockstate.isIn(Blocks.BEDROCK)))
                        {
                            blockpos = blockpos1;
                            break;
                        }
                    }
                }
            }
        }

        return blockpos == null ? posIn : blockpos;
    }

    private static Chunk getChunk(World worldIn, Vector3d vec3)
    {
        return worldIn.getChunk(MathHelper.floor(vec3.x / 16.0D), MathHelper.floor(vec3.z / 16.0D));
    }

    @Nullable
    private static BlockPos findSpawnpointInChunk(Chunk chunkIn)
    {
        ChunkPos chunkpos = chunkIn.getPos();
        BlockPos blockpos = new BlockPos(chunkpos.getXStart(), 30, chunkpos.getZStart());
        int i = chunkIn.getTopFilledSegment() + 16 - 1;
        BlockPos blockpos1 = new BlockPos(chunkpos.getXEnd(), i, chunkpos.getZEnd());
        BlockPos blockpos2 = null;
        double d0 = 0.0D;

        for (BlockPos blockpos3 : BlockPos.getAllInBoxMutable(blockpos, blockpos1))
        {
            BlockState blockstate = chunkIn.getBlockState(blockpos3);
            BlockPos blockpos4 = blockpos3.up();
            BlockPos blockpos5 = blockpos3.up(2);

            if (blockstate.isIn(Blocks.END_STONE) && !chunkIn.getBlockState(blockpos4).hasOpaqueCollisionShape(chunkIn, blockpos4) && !chunkIn.getBlockState(blockpos5).hasOpaqueCollisionShape(chunkIn, blockpos5))
            {
                double d1 = blockpos3.distanceSq(0.0D, 0.0D, 0.0D, true);

                if (blockpos2 == null || d1 < d0)
                {
                    blockpos2 = blockpos3;
                    d0 = d1;
                }
            }
        }

        return blockpos2;
    }

    private void func_227016_a_(ServerWorld p_227016_1_, BlockPos p_227016_2_)
    {
        Feature.END_GATEWAY.withConfiguration(EndGatewayConfig.func_214702_a(this.getPos(), false)).func_242765_a(p_227016_1_, p_227016_1_.getChunkProvider().getChunkGenerator(), new Random(), p_227016_2_);
    }

    public boolean shouldRenderFace(Direction face)
    {
        return Block.shouldSideBeRendered(this.getBlockState(), this.world, this.getPos(), face);
    }

    public int getParticleAmount()
    {
        int i = 0;

        for (Direction direction : Direction.values())
        {
            i += this.shouldRenderFace(direction) ? 1 : 0;
        }

        return i;
    }

    public void setExitPortal(BlockPos exitPortalIn, boolean p_195489_2_)
    {
        this.exactTeleport = p_195489_2_;
        this.exitPortal = exitPortalIn;
    }
}
