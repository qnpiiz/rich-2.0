package net.minecraft.pathfinding;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

public class WalkNodeProcessor extends NodeProcessor
{
    protected float avoidsWater;
    private final Long2ObjectMap<PathNodeType> field_237226_k_ = new Long2ObjectOpenHashMap<>();
    private final Object2BooleanMap<AxisAlignedBB> field_237227_l_ = new Object2BooleanOpenHashMap<>();

    public void func_225578_a_(Region p_225578_1_, MobEntity p_225578_2_)
    {
        super.func_225578_a_(p_225578_1_, p_225578_2_);
        this.avoidsWater = p_225578_2_.getPathPriority(PathNodeType.WATER);
    }

    /**
     * This method is called when all nodes have been processed and PathEntity is created.
     *  {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
     * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
     */
    public void postProcess()
    {
        this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
        this.field_237226_k_.clear();
        this.field_237227_l_.clear();
        super.postProcess();
    }

    public PathPoint getStart()
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i = MathHelper.floor(this.entity.getPosY());
        BlockState blockstate = this.blockaccess.getBlockState(blockpos$mutable.setPos(this.entity.getPosX(), (double)i, this.entity.getPosZ()));

        if (!this.entity.func_230285_a_(blockstate.getFluidState().getFluid()))
        {
            if (this.getCanSwim() && this.entity.isInWater())
            {
                while (true)
                {
                    if (blockstate.getBlock() != Blocks.WATER && blockstate.getFluidState() != Fluids.WATER.getStillFluidState(false))
                    {
                        --i;
                        break;
                    }

                    ++i;
                    blockstate = this.blockaccess.getBlockState(blockpos$mutable.setPos(this.entity.getPosX(), (double)i, this.entity.getPosZ()));
                }
            }
            else if (this.entity.isOnGround())
            {
                i = MathHelper.floor(this.entity.getPosY() + 0.5D);
            }
            else
            {
                BlockPos blockpos;

                for (blockpos = this.entity.getPosition(); (this.blockaccess.getBlockState(blockpos).isAir() || this.blockaccess.getBlockState(blockpos).allowsMovement(this.blockaccess, blockpos, PathType.LAND)) && blockpos.getY() > 0; blockpos = blockpos.down())
                {
                }

                i = blockpos.up().getY();
            }
        }
        else
        {
            while (this.entity.func_230285_a_(blockstate.getFluidState().getFluid()))
            {
                ++i;
                blockstate = this.blockaccess.getBlockState(blockpos$mutable.setPos(this.entity.getPosX(), (double)i, this.entity.getPosZ()));
            }

            --i;
        }

        BlockPos blockpos1 = this.entity.getPosition();
        PathNodeType pathnodetype = this.func_237230_a_(this.entity, blockpos1.getX(), i, blockpos1.getZ());

        if (this.entity.getPathPriority(pathnodetype) < 0.0F)
        {
            AxisAlignedBB axisalignedbb = this.entity.getBoundingBox();

            if (this.func_237239_b_(blockpos$mutable.setPos(axisalignedbb.minX, (double)i, axisalignedbb.minZ)) || this.func_237239_b_(blockpos$mutable.setPos(axisalignedbb.minX, (double)i, axisalignedbb.maxZ)) || this.func_237239_b_(blockpos$mutable.setPos(axisalignedbb.maxX, (double)i, axisalignedbb.minZ)) || this.func_237239_b_(blockpos$mutable.setPos(axisalignedbb.maxX, (double)i, axisalignedbb.maxZ)))
            {
                PathPoint pathpoint = this.func_237223_a_(blockpos$mutable);
                pathpoint.nodeType = this.getPathNodeType(this.entity, pathpoint.func_224759_a());
                pathpoint.costMalus = this.entity.getPathPriority(pathpoint.nodeType);
                return pathpoint;
            }
        }

        PathPoint pathpoint1 = this.openPoint(blockpos1.getX(), i, blockpos1.getZ());
        pathpoint1.nodeType = this.getPathNodeType(this.entity, pathpoint1.func_224759_a());
        pathpoint1.costMalus = this.entity.getPathPriority(pathpoint1.nodeType);
        return pathpoint1;
    }

    private boolean func_237239_b_(BlockPos p_237239_1_)
    {
        PathNodeType pathnodetype = this.getPathNodeType(this.entity, p_237239_1_);
        return this.entity.getPathPriority(pathnodetype) >= 0.0F;
    }

    public FlaggedPathPoint func_224768_a(double p_224768_1_, double p_224768_3_, double p_224768_5_)
    {
        return new FlaggedPathPoint(this.openPoint(MathHelper.floor(p_224768_1_), MathHelper.floor(p_224768_3_), MathHelper.floor(p_224768_5_)));
    }

    public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_)
    {
        int i = 0;
        int j = 0;
        PathNodeType pathnodetype = this.func_237230_a_(this.entity, p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z);
        PathNodeType pathnodetype1 = this.func_237230_a_(this.entity, p_222859_2_.x, p_222859_2_.y, p_222859_2_.z);

        if (this.entity.getPathPriority(pathnodetype) >= 0.0F && pathnodetype1 != PathNodeType.STICKY_HONEY)
        {
            j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
        }

        double d0 = getGroundY(this.blockaccess, new BlockPos(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z));
        PathPoint pathpoint = this.getSafePoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1, j, d0, Direction.SOUTH, pathnodetype1);

        if (this.func_237235_a_(pathpoint, p_222859_2_))
        {
            p_222859_1_[i++] = pathpoint;
        }

        PathPoint pathpoint1 = this.getSafePoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z, j, d0, Direction.WEST, pathnodetype1);

        if (this.func_237235_a_(pathpoint1, p_222859_2_))
        {
            p_222859_1_[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.getSafePoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z, j, d0, Direction.EAST, pathnodetype1);

        if (this.func_237235_a_(pathpoint2, p_222859_2_))
        {
            p_222859_1_[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.getSafePoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1, j, d0, Direction.NORTH, pathnodetype1);

        if (this.func_237235_a_(pathpoint3, p_222859_2_))
        {
            p_222859_1_[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.getSafePoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1, j, d0, Direction.NORTH, pathnodetype1);

        if (this.func_222860_a(p_222859_2_, pathpoint1, pathpoint3, pathpoint4))
        {
            p_222859_1_[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.getSafePoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1, j, d0, Direction.NORTH, pathnodetype1);

        if (this.func_222860_a(p_222859_2_, pathpoint2, pathpoint3, pathpoint5))
        {
            p_222859_1_[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.getSafePoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1, j, d0, Direction.SOUTH, pathnodetype1);

        if (this.func_222860_a(p_222859_2_, pathpoint1, pathpoint, pathpoint6))
        {
            p_222859_1_[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.getSafePoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1, j, d0, Direction.SOUTH, pathnodetype1);

        if (this.func_222860_a(p_222859_2_, pathpoint2, pathpoint, pathpoint7))
        {
            p_222859_1_[i++] = pathpoint7;
        }

        return i;
    }

    private boolean func_237235_a_(PathPoint p_237235_1_, PathPoint p_237235_2_)
    {
        return p_237235_1_ != null && !p_237235_1_.visited && (p_237235_1_.costMalus >= 0.0F || p_237235_2_.costMalus < 0.0F);
    }

    private boolean func_222860_a(PathPoint p_222860_1_, @Nullable PathPoint p_222860_2_, @Nullable PathPoint p_222860_3_, @Nullable PathPoint p_222860_4_)
    {
        if (p_222860_4_ != null && p_222860_3_ != null && p_222860_2_ != null)
        {
            if (p_222860_4_.visited)
            {
                return false;
            }
            else if (p_222860_3_.y <= p_222860_1_.y && p_222860_2_.y <= p_222860_1_.y)
            {
                if (p_222860_2_.nodeType != PathNodeType.WALKABLE_DOOR && p_222860_3_.nodeType != PathNodeType.WALKABLE_DOOR && p_222860_4_.nodeType != PathNodeType.WALKABLE_DOOR)
                {
                    boolean flag = p_222860_3_.nodeType == PathNodeType.FENCE && p_222860_2_.nodeType == PathNodeType.FENCE && (double)this.entity.getWidth() < 0.5D;
                    return p_222860_4_.costMalus >= 0.0F && (p_222860_3_.y < p_222860_1_.y || p_222860_3_.costMalus >= 0.0F || flag) && (p_222860_2_.y < p_222860_1_.y || p_222860_2_.costMalus >= 0.0F || flag);
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean func_237234_a_(PathPoint p_237234_1_)
    {
        Vector3d vector3d = new Vector3d((double)p_237234_1_.x - this.entity.getPosX(), (double)p_237234_1_.y - this.entity.getPosY(), (double)p_237234_1_.z - this.entity.getPosZ());
        AxisAlignedBB axisalignedbb = this.entity.getBoundingBox();
        int i = MathHelper.ceil(vector3d.length() / axisalignedbb.getAverageEdgeLength());
        vector3d = vector3d.scale((double)(1.0F / (float)i));

        for (int j = 1; j <= i; ++j)
        {
            axisalignedbb = axisalignedbb.offset(vector3d);

            if (this.func_237236_a_(axisalignedbb))
            {
                return false;
            }
        }

        return true;
    }

    public static double getGroundY(IBlockReader p_197682_0_, BlockPos pos)
    {
        BlockPos blockpos = pos.down();
        VoxelShape voxelshape = p_197682_0_.getBlockState(blockpos).getCollisionShape(p_197682_0_, blockpos);
        return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.getEnd(Direction.Axis.Y));
    }

    @Nullable

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(int x, int y, int z, int stepHeight, double groundYIn, Direction facing, PathNodeType p_186332_8_)
    {
        PathPoint pathpoint = null;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        double d0 = getGroundY(this.blockaccess, blockpos$mutable.setPos(x, y, z));

        if (d0 - groundYIn > 1.125D)
        {
            return null;
        }
        else
        {
            PathNodeType pathnodetype = this.func_237230_a_(this.entity, x, y, z);
            float f = this.entity.getPathPriority(pathnodetype);
            double d1 = (double)this.entity.getWidth() / 2.0D;

            if (f >= 0.0F)
            {
                pathpoint = this.openPoint(x, y, z);
                pathpoint.nodeType = pathnodetype;
                pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            }

            if (p_186332_8_ == PathNodeType.FENCE && pathpoint != null && pathpoint.costMalus >= 0.0F && !this.func_237234_a_(pathpoint))
            {
                pathpoint = null;
            }

            if (pathnodetype == PathNodeType.WALKABLE)
            {
                return pathpoint;
            }
            else
            {
                if ((pathpoint == null || pathpoint.costMalus < 0.0F) && stepHeight > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.UNPASSABLE_RAIL && pathnodetype != PathNodeType.TRAPDOOR)
                {
                    pathpoint = this.getSafePoint(x, y + 1, z, stepHeight - 1, groundYIn, facing, p_186332_8_);

                    if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.getWidth() < 1.0F)
                    {
                        double d2 = (double)(x - facing.getXOffset()) + 0.5D;
                        double d3 = (double)(z - facing.getZOffset()) + 0.5D;
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, getGroundY(this.blockaccess, blockpos$mutable.setPos(d2, (double)(y + 1), d3)) + 0.001D, d3 - d1, d2 + d1, (double)this.entity.getHeight() + getGroundY(this.blockaccess, blockpos$mutable.setPos((double)pathpoint.x, (double)pathpoint.y, (double)pathpoint.z)) - 0.002D, d3 + d1);

                        if (this.func_237236_a_(axisalignedbb))
                        {
                            pathpoint = null;
                        }
                    }
                }

                if (pathnodetype == PathNodeType.WATER && !this.getCanSwim())
                {
                    if (this.func_237230_a_(this.entity, x, y - 1, z) != PathNodeType.WATER)
                    {
                        return pathpoint;
                    }

                    while (y > 0)
                    {
                        --y;
                        pathnodetype = this.func_237230_a_(this.entity, x, y, z);

                        if (pathnodetype != PathNodeType.WATER)
                        {
                            return pathpoint;
                        }

                        pathpoint = this.openPoint(x, y, z);
                        pathpoint.nodeType = pathnodetype;
                        pathpoint.costMalus = Math.max(pathpoint.costMalus, this.entity.getPathPriority(pathnodetype));
                    }
                }

                if (pathnodetype == PathNodeType.OPEN)
                {
                    int j = 0;
                    int i = y;

                    while (pathnodetype == PathNodeType.OPEN)
                    {
                        --y;

                        if (y < 0)
                        {
                            PathPoint pathpoint3 = this.openPoint(x, i, z);
                            pathpoint3.nodeType = PathNodeType.BLOCKED;
                            pathpoint3.costMalus = -1.0F;
                            return pathpoint3;
                        }

                        if (j++ >= this.entity.getMaxFallHeight())
                        {
                            PathPoint pathpoint2 = this.openPoint(x, y, z);
                            pathpoint2.nodeType = PathNodeType.BLOCKED;
                            pathpoint2.costMalus = -1.0F;
                            return pathpoint2;
                        }

                        pathnodetype = this.func_237230_a_(this.entity, x, y, z);
                        f = this.entity.getPathPriority(pathnodetype);

                        if (pathnodetype != PathNodeType.OPEN && f >= 0.0F)
                        {
                            pathpoint = this.openPoint(x, y, z);
                            pathpoint.nodeType = pathnodetype;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            break;
                        }

                        if (f < 0.0F)
                        {
                            PathPoint pathpoint1 = this.openPoint(x, y, z);
                            pathpoint1.nodeType = PathNodeType.BLOCKED;
                            pathpoint1.costMalus = -1.0F;
                            return pathpoint1;
                        }
                    }
                }

                if (pathnodetype == PathNodeType.FENCE)
                {
                    pathpoint = this.openPoint(x, y, z);
                    pathpoint.visited = true;
                    pathpoint.nodeType = pathnodetype;
                    pathpoint.costMalus = pathnodetype.getPriority();
                }

                return pathpoint;
            }
        }
    }

    private boolean func_237236_a_(AxisAlignedBB p_237236_1_)
    {
        return this.field_237227_l_.computeIfAbsent(p_237236_1_, (p_237237_2_) ->
        {
            return !this.blockaccess.hasNoCollisions(this.entity, p_237236_1_);
        });
    }

    public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z, MobEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn)
    {
        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        BlockPos blockpos = entitylivingIn.getPosition();
        pathnodetype = this.getPathNodeType(blockaccessIn, x, y, z, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn, enumset, pathnodetype, blockpos);

        if (enumset.contains(PathNodeType.FENCE))
        {
            return PathNodeType.FENCE;
        }
        else if (enumset.contains(PathNodeType.UNPASSABLE_RAIL))
        {
            return PathNodeType.UNPASSABLE_RAIL;
        }
        else
        {
            PathNodeType pathnodetype1 = PathNodeType.BLOCKED;

            for (PathNodeType pathnodetype2 : enumset)
            {
                if (entitylivingIn.getPathPriority(pathnodetype2) < 0.0F)
                {
                    return pathnodetype2;
                }

                if (entitylivingIn.getPathPriority(pathnodetype2) >= entitylivingIn.getPathPriority(pathnodetype1))
                {
                    pathnodetype1 = pathnodetype2;
                }
            }

            return pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype1) == 0.0F && xSize <= 1 ? PathNodeType.OPEN : pathnodetype1;
        }
    }

    public PathNodeType getPathNodeType(IBlockReader p_193577_1_, int x, int y, int z, int xSize, int ySize, int zSize, boolean canOpenDoorsIn, boolean canEnterDoorsIn, EnumSet<PathNodeType> nodeTypeEnum, PathNodeType nodeType, BlockPos pos)
    {
        for (int i = 0; i < xSize; ++i)
        {
            for (int j = 0; j < ySize; ++j)
            {
                for (int k = 0; k < zSize; ++k)
                {
                    int l = i + x;
                    int i1 = j + y;
                    int j1 = k + z;
                    PathNodeType pathnodetype = this.getPathNodeType(p_193577_1_, l, i1, j1);
                    pathnodetype = this.func_215744_a(p_193577_1_, canOpenDoorsIn, canEnterDoorsIn, pos, pathnodetype);

                    if (i == 0 && j == 0 && k == 0)
                    {
                        nodeType = pathnodetype;
                    }

                    nodeTypeEnum.add(pathnodetype);
                }
            }
        }

        return nodeType;
    }

    protected PathNodeType func_215744_a(IBlockReader p_215744_1_, boolean p_215744_2_, boolean p_215744_3_, BlockPos p_215744_4_, PathNodeType p_215744_5_)
    {
        if (p_215744_5_ == PathNodeType.DOOR_WOOD_CLOSED && p_215744_2_ && p_215744_3_)
        {
            p_215744_5_ = PathNodeType.WALKABLE_DOOR;
        }

        if (p_215744_5_ == PathNodeType.DOOR_OPEN && !p_215744_3_)
        {
            p_215744_5_ = PathNodeType.BLOCKED;
        }

        if (p_215744_5_ == PathNodeType.RAIL && !(p_215744_1_.getBlockState(p_215744_4_).getBlock() instanceof AbstractRailBlock) && !(p_215744_1_.getBlockState(p_215744_4_.down()).getBlock() instanceof AbstractRailBlock))
        {
            p_215744_5_ = PathNodeType.UNPASSABLE_RAIL;
        }

        if (p_215744_5_ == PathNodeType.LEAVES)
        {
            p_215744_5_ = PathNodeType.BLOCKED;
        }

        return p_215744_5_;
    }

    private PathNodeType getPathNodeType(MobEntity entitylivingIn, BlockPos pos)
    {
        return this.func_237230_a_(entitylivingIn, pos.getX(), pos.getY(), pos.getZ());
    }

    private PathNodeType func_237230_a_(MobEntity p_237230_1_, int p_237230_2_, int p_237230_3_, int p_237230_4_)
    {
        return this.field_237226_k_.computeIfAbsent(BlockPos.pack(p_237230_2_, p_237230_3_, p_237230_4_), (p_237229_5_) ->
        {
            return this.getPathNodeType(this.blockaccess, p_237230_2_, p_237230_3_, p_237230_4_, p_237230_1_, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
        });
    }

    public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z)
    {
        return func_237231_a_(blockaccessIn, new BlockPos.Mutable(x, y, z));
    }

    public static PathNodeType func_237231_a_(IBlockReader p_237231_0_, BlockPos.Mutable p_237231_1_)
    {
        int i = p_237231_1_.getX();
        int j = p_237231_1_.getY();
        int k = p_237231_1_.getZ();
        PathNodeType pathnodetype = func_237238_b_(p_237231_0_, p_237231_1_);

        if (pathnodetype == PathNodeType.OPEN && j >= 1)
        {
            PathNodeType pathnodetype1 = func_237238_b_(p_237231_0_, p_237231_1_.setPos(i, j - 1, k));
            pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE)
            {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS)
            {
                pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER)
            {
                pathnodetype = PathNodeType.DAMAGE_OTHER;
            }

            if (pathnodetype1 == PathNodeType.STICKY_HONEY)
            {
                pathnodetype = PathNodeType.STICKY_HONEY;
            }
        }

        if (pathnodetype == PathNodeType.WALKABLE)
        {
            pathnodetype = func_237232_a_(p_237231_0_, p_237231_1_.setPos(i, j, k), pathnodetype);
        }

        return pathnodetype;
    }

    public static PathNodeType func_237232_a_(IBlockReader p_237232_0_, BlockPos.Mutable p_237232_1_, PathNodeType p_237232_2_)
    {
        int i = p_237232_1_.getX();
        int j = p_237232_1_.getY();
        int k = p_237232_1_.getZ();

        for (int l = -1; l <= 1; ++l)
        {
            for (int i1 = -1; i1 <= 1; ++i1)
            {
                for (int j1 = -1; j1 <= 1; ++j1)
                {
                    if (l != 0 || j1 != 0)
                    {
                        p_237232_1_.setPos(i + l, j + i1, k + j1);
                        BlockState blockstate = p_237232_0_.getBlockState(p_237232_1_);

                        if (blockstate.isIn(Blocks.CACTUS))
                        {
                            return PathNodeType.DANGER_CACTUS;
                        }

                        if (blockstate.isIn(Blocks.SWEET_BERRY_BUSH))
                        {
                            return PathNodeType.DANGER_OTHER;
                        }

                        if (func_237233_a_(blockstate))
                        {
                            return PathNodeType.DANGER_FIRE;
                        }

                        if (p_237232_0_.getFluidState(p_237232_1_).isTagged(FluidTags.WATER))
                        {
                            return PathNodeType.WATER_BORDER;
                        }
                    }
                }
            }
        }

        return p_237232_2_;
    }

    protected static PathNodeType func_237238_b_(IBlockReader p_237238_0_, BlockPos p_237238_1_)
    {
        BlockState blockstate = p_237238_0_.getBlockState(p_237238_1_);
        Block block = blockstate.getBlock();
        Material material = blockstate.getMaterial();

        if (blockstate.isAir())
        {
            return PathNodeType.OPEN;
        }
        else if (!blockstate.isIn(BlockTags.TRAPDOORS) && !blockstate.isIn(Blocks.LILY_PAD))
        {
            if (blockstate.isIn(Blocks.CACTUS))
            {
                return PathNodeType.DAMAGE_CACTUS;
            }
            else if (blockstate.isIn(Blocks.SWEET_BERRY_BUSH))
            {
                return PathNodeType.DAMAGE_OTHER;
            }
            else if (blockstate.isIn(Blocks.HONEY_BLOCK))
            {
                return PathNodeType.STICKY_HONEY;
            }
            else if (blockstate.isIn(Blocks.COCOA))
            {
                return PathNodeType.COCOA;
            }
            else
            {
                FluidState fluidstate = p_237238_0_.getFluidState(p_237238_1_);

                if (fluidstate.isTagged(FluidTags.WATER))
                {
                    return PathNodeType.WATER;
                }
                else if (fluidstate.isTagged(FluidTags.LAVA))
                {
                    return PathNodeType.LAVA;
                }
                else if (func_237233_a_(blockstate))
                {
                    return PathNodeType.DAMAGE_FIRE;
                }
                else if (DoorBlock.isWooden(blockstate) && !blockstate.get(DoorBlock.OPEN))
                {
                    return PathNodeType.DOOR_WOOD_CLOSED;
                }
                else if (block instanceof DoorBlock && material == Material.IRON && !blockstate.get(DoorBlock.OPEN))
                {
                    return PathNodeType.DOOR_IRON_CLOSED;
                }
                else if (block instanceof DoorBlock && blockstate.get(DoorBlock.OPEN))
                {
                    return PathNodeType.DOOR_OPEN;
                }
                else if (block instanceof AbstractRailBlock)
                {
                    return PathNodeType.RAIL;
                }
                else if (block instanceof LeavesBlock)
                {
                    return PathNodeType.LEAVES;
                }
                else if (!block.isIn(BlockTags.FENCES) && !block.isIn(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.get(FenceGateBlock.OPEN)))
                {
                    return !blockstate.allowsMovement(p_237238_0_, p_237238_1_, PathType.LAND) ? PathNodeType.BLOCKED : PathNodeType.OPEN;
                }
                else
                {
                    return PathNodeType.FENCE;
                }
            }
        }
        else
        {
            return PathNodeType.TRAPDOOR;
        }
    }

    private static boolean func_237233_a_(BlockState p_237233_0_)
    {
        return p_237233_0_.isIn(BlockTags.FIRE) || p_237233_0_.isIn(Blocks.LAVA) || p_237233_0_.isIn(Blocks.MAGMA_BLOCK) || CampfireBlock.isLit(p_237233_0_);
    }
}
