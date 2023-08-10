package net.minecraft.block;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntitySize;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

public class PortalSize
{
    private static final AbstractBlock.IPositionPredicate POSITION_PREDICATE = (state, blockReader, pos) ->
    {
        return state.isIn(Blocks.OBSIDIAN);
    };
    private final IWorld world;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private int portalBlockCount;
    @Nullable
    private BlockPos bottomLeft;
    private int height;
    private int width;

    public static Optional<PortalSize> func_242964_a(IWorld world, BlockPos pos, Direction.Axis axis)
    {
        return func_242965_a(world, pos, (size) ->
        {
            return size.isValid() && size.portalBlockCount == 0;
        }, axis);
    }

    public static Optional<PortalSize> func_242965_a(IWorld world, BlockPos pos, Predicate<PortalSize> sizePredicate, Direction.Axis axis)
    {
        Optional<PortalSize> optional = Optional.of(new PortalSize(world, pos, axis)).filter(sizePredicate);

        if (optional.isPresent())
        {
            return optional;
        }
        else
        {
            Direction.Axis direction$axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            return Optional.of(new PortalSize(world, pos, direction$axis)).filter(sizePredicate);
        }
    }

    public PortalSize(IWorld worldIn, BlockPos pos, Direction.Axis axisIn)
    {
        this.world = worldIn;
        this.axis = axisIn;
        this.rightDir = axisIn == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.bottomLeft = this.func_242971_a(pos);

        if (this.bottomLeft == null)
        {
            this.bottomLeft = pos;
            this.width = 1;
            this.height = 1;
        }
        else
        {
            this.width = this.func_242974_d();

            if (this.width > 0)
            {
                this.height = this.func_242975_e();
            }
        }
    }

    @Nullable
    private BlockPos func_242971_a(BlockPos pos)
    {
        for (int i = Math.max(0, pos.getY() - 21); pos.getY() > i && canConnect(this.world.getBlockState(pos.down())); pos = pos.down())
        {
        }

        Direction direction = this.rightDir.getOpposite();
        int j = this.func_242972_a(pos, direction) - 1;
        return j < 0 ? null : pos.offset(direction, j);
    }

    private int func_242974_d()
    {
        int i = this.func_242972_a(this.bottomLeft, this.rightDir);
        return i >= 2 && i <= 21 ? i : 0;
    }

    private int func_242972_a(BlockPos pos, Direction direction)
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (int i = 0; i <= 21; ++i)
        {
            blockpos$mutable.setPos(pos).move(direction, i);
            BlockState blockstate = this.world.getBlockState(blockpos$mutable);

            if (!canConnect(blockstate))
            {
                if (POSITION_PREDICATE.test(blockstate, this.world, blockpos$mutable))
                {
                    return i;
                }

                break;
            }

            BlockState blockstate1 = this.world.getBlockState(blockpos$mutable.move(Direction.DOWN));

            if (!POSITION_PREDICATE.test(blockstate1, this.world, blockpos$mutable))
            {
                break;
            }
        }

        return 0;
    }

    private int func_242975_e()
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int i = this.func_242969_a(blockpos$mutable);
        return i >= 3 && i <= 21 && this.func_242970_a(blockpos$mutable, i) ? i : 0;
    }

    private boolean func_242970_a(BlockPos.Mutable mutablePos, int upDisplacement)
    {
        for (int i = 0; i < this.width; ++i)
        {
            BlockPos.Mutable blockpos$mutable = mutablePos.setPos(this.bottomLeft).move(Direction.UP, upDisplacement).move(this.rightDir, i);

            if (!POSITION_PREDICATE.test(this.world.getBlockState(blockpos$mutable), this.world, blockpos$mutable))
            {
                return false;
            }
        }

        return true;
    }

    private int func_242969_a(BlockPos.Mutable mutablePos)
    {
        for (int i = 0; i < 21; ++i)
        {
            mutablePos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);

            if (!POSITION_PREDICATE.test(this.world.getBlockState(mutablePos), this.world, mutablePos))
            {
                return i;
            }

            mutablePos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);

            if (!POSITION_PREDICATE.test(this.world.getBlockState(mutablePos), this.world, mutablePos))
            {
                return i;
            }

            for (int j = 0; j < this.width; ++j)
            {
                mutablePos.setPos(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
                BlockState blockstate = this.world.getBlockState(mutablePos);

                if (!canConnect(blockstate))
                {
                    return i;
                }

                if (blockstate.isIn(Blocks.NETHER_PORTAL))
                {
                    ++this.portalBlockCount;
                }
            }
        }

        return 21;
    }

    private static boolean canConnect(BlockState state)
    {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isIn(Blocks.NETHER_PORTAL);
    }

    public boolean isValid()
    {
        return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void placePortalBlocks()
    {
        BlockState blockstate = Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis);
        BlockPos.getAllInBoxMutable(this.bottomLeft, this.bottomLeft.offset(Direction.UP, this.height - 1).offset(this.rightDir, this.width - 1)).forEach((pos) ->
        {
            this.world.setBlockState(pos, blockstate, 18);
        });
    }

    public boolean validatePortal()
    {
        return this.isValid() && this.portalBlockCount == this.width * this.height;
    }

    public static Vector3d func_242973_a(TeleportationRepositioner.Result result, Direction.Axis axis, Vector3d positionVector, EntitySize size)
    {
        double d0 = (double)result.width - (double)size.width;
        double d1 = (double)result.height - (double)size.height;
        BlockPos blockpos = result.startPos;
        double d2;

        if (d0 > 0.0D)
        {
            float f = (float)blockpos.func_243648_a(axis) + size.width / 2.0F;
            d2 = MathHelper.clamp(MathHelper.func_233020_c_(positionVector.getCoordinate(axis) - (double)f, 0.0D, d0), 0.0D, 1.0D);
        }
        else
        {
            d2 = 0.5D;
        }

        double d4;

        if (d1 > 0.0D)
        {
            Direction.Axis direction$axis = Direction.Axis.Y;
            d4 = MathHelper.clamp(MathHelper.func_233020_c_(positionVector.getCoordinate(direction$axis) - (double)blockpos.func_243648_a(direction$axis), 0.0D, d1), 0.0D, 1.0D);
        }
        else
        {
            d4 = 0.0D;
        }

        Direction.Axis direction$axis1 = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double d3 = positionVector.getCoordinate(direction$axis1) - ((double)blockpos.func_243648_a(direction$axis1) + 0.5D);
        return new Vector3d(d2, d4, d3);
    }

    public static PortalInfo func_242963_a(ServerWorld world, TeleportationRepositioner.Result result, Direction.Axis axis, Vector3d offsetVector, EntitySize size, Vector3d motion, float rotationYaw, float rotationPitch)
    {
        BlockPos blockpos = result.startPos;
        BlockState blockstate = world.getBlockState(blockpos);
        Direction.Axis direction$axis = blockstate.get(BlockStateProperties.HORIZONTAL_AXIS);
        double d0 = (double)result.width;
        double d1 = (double)result.height;
        int i = axis == direction$axis ? 0 : 90;
        Vector3d vector3d = axis == direction$axis ? motion : new Vector3d(motion.z, motion.y, -motion.x);
        double d2 = (double)size.width / 2.0D + (d0 - (double)size.width) * offsetVector.getX();
        double d3 = (d1 - (double)size.height) * offsetVector.getY();
        double d4 = 0.5D + offsetVector.getZ();
        boolean flag = direction$axis == Direction.Axis.X;
        Vector3d vector3d1 = new Vector3d((double)blockpos.getX() + (flag ? d2 : d4), (double)blockpos.getY() + d3, (double)blockpos.getZ() + (flag ? d4 : d2));
        return new PortalInfo(vector3d1, vector3d, rotationYaw + (float)i, rotationPitch);
    }
}
