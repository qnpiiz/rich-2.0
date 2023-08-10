package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ChorusFlowerBlock extends Block
{
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_5;
    private final ChorusPlantBlock plantBlock;

    protected ChorusFlowerBlock(ChorusPlantBlock plantBlock, AbstractBlock.Properties builder)
    {
        super(builder);
        this.plantBlock = plantBlock;
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        if (!state.isValidPosition(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
     * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
     */
    public boolean ticksRandomly(BlockState state)
    {
        return state.get(AGE) < 5;
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        BlockPos blockpos = pos.up();

        if (worldIn.isAirBlock(blockpos) && blockpos.getY() < 256)
        {
            int i = state.get(AGE);

            if (i < 5)
            {
                boolean flag = false;
                boolean flag1 = false;
                BlockState blockstate = worldIn.getBlockState(pos.down());
                Block block = blockstate.getBlock();

                if (block == Blocks.END_STONE)
                {
                    flag = true;
                }
                else if (block == this.plantBlock)
                {
                    int j = 1;

                    for (int k = 0; k < 4; ++k)
                    {
                        Block block1 = worldIn.getBlockState(pos.down(j + 1)).getBlock();

                        if (block1 != this.plantBlock)
                        {
                            if (block1 == Blocks.END_STONE)
                            {
                                flag1 = true;
                            }

                            break;
                        }

                        ++j;
                    }

                    if (j < 2 || j <= random.nextInt(flag1 ? 5 : 4))
                    {
                        flag = true;
                    }
                }
                else if (blockstate.isAir())
                {
                    flag = true;
                }

                if (flag && areAllNeighborsEmpty(worldIn, blockpos, (Direction)null) && worldIn.isAirBlock(pos.up(2)))
                {
                    worldIn.setBlockState(pos, this.plantBlock.makeConnections(worldIn, pos), 2);
                    this.placeGrownFlower(worldIn, blockpos, i);
                }
                else if (i < 4)
                {
                    int l = random.nextInt(4);

                    if (flag1)
                    {
                        ++l;
                    }

                    boolean flag2 = false;

                    for (int i1 = 0; i1 < l; ++i1)
                    {
                        Direction direction = Direction.Plane.HORIZONTAL.random(random);
                        BlockPos blockpos1 = pos.offset(direction);

                        if (worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(worldIn, blockpos1, direction.getOpposite()))
                        {
                            this.placeGrownFlower(worldIn, blockpos1, i + 1);
                            flag2 = true;
                        }
                    }

                    if (flag2)
                    {
                        worldIn.setBlockState(pos, this.plantBlock.makeConnections(worldIn, pos), 2);
                    }
                    else
                    {
                        this.placeDeadFlower(worldIn, pos);
                    }
                }
                else
                {
                    this.placeDeadFlower(worldIn, pos);
                }
            }
        }
    }

    private void placeGrownFlower(World worldIn, BlockPos pos, int age)
    {
        worldIn.setBlockState(pos, this.getDefaultState().with(AGE, Integer.valueOf(age)), 2);
        worldIn.playEvent(1033, pos, 0);
    }

    private void placeDeadFlower(World worldIn, BlockPos pos)
    {
        worldIn.setBlockState(pos, this.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
        worldIn.playEvent(1034, pos, 0);
    }

    private static boolean areAllNeighborsEmpty(IWorldReader worldIn, BlockPos pos, @Nullable Direction excludingSide)
    {
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            if (direction != excludingSide && !worldIn.isAirBlock(pos.offset(direction)))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing != Direction.UP && !stateIn.isValidPosition(worldIn, currentPos))
        {
            worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.down());

        if (blockstate.getBlock() != this.plantBlock && !blockstate.isIn(Blocks.END_STONE))
        {
            if (!blockstate.isAir())
            {
                return false;
            }
            else
            {
                boolean flag = false;

                for (Direction direction : Direction.Plane.HORIZONTAL)
                {
                    BlockState blockstate1 = worldIn.getBlockState(pos.offset(direction));

                    if (blockstate1.isIn(this.plantBlock))
                    {
                        if (flag)
                        {
                            return false;
                        }

                        flag = true;
                    }
                    else if (!blockstate1.isAir())
                    {
                        return false;
                    }
                }

                return flag;
            }
        }
        else
        {
            return true;
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    public static void generatePlant(IWorld worldIn, BlockPos pos, Random rand, int maxHorizontalDistance)
    {
        worldIn.setBlockState(pos, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).makeConnections(worldIn, pos), 2);
        growTreeRecursive(worldIn, pos, rand, pos, maxHorizontalDistance, 0);
    }

    private static void growTreeRecursive(IWorld worldIn, BlockPos branchPos, Random rand, BlockPos originalBranchPos, int maxHorizontalDistance, int iterations)
    {
        ChorusPlantBlock chorusplantblock = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
        int i = rand.nextInt(4) + 1;

        if (iterations == 0)
        {
            ++i;
        }

        for (int j = 0; j < i; ++j)
        {
            BlockPos blockpos = branchPos.up(j + 1);

            if (!areAllNeighborsEmpty(worldIn, blockpos, (Direction)null))
            {
                return;
            }

            worldIn.setBlockState(blockpos, chorusplantblock.makeConnections(worldIn, blockpos), 2);
            worldIn.setBlockState(blockpos.down(), chorusplantblock.makeConnections(worldIn, blockpos.down()), 2);
        }

        boolean flag = false;

        if (iterations < 4)
        {
            int l = rand.nextInt(4);

            if (iterations == 0)
            {
                ++l;
            }

            for (int k = 0; k < l; ++k)
            {
                Direction direction = Direction.Plane.HORIZONTAL.random(rand);
                BlockPos blockpos1 = branchPos.up(i).offset(direction);

                if (Math.abs(blockpos1.getX() - originalBranchPos.getX()) < maxHorizontalDistance && Math.abs(blockpos1.getZ() - originalBranchPos.getZ()) < maxHorizontalDistance && worldIn.isAirBlock(blockpos1) && worldIn.isAirBlock(blockpos1.down()) && areAllNeighborsEmpty(worldIn, blockpos1, direction.getOpposite()))
                {
                    flag = true;
                    worldIn.setBlockState(blockpos1, chorusplantblock.makeConnections(worldIn, blockpos1), 2);
                    worldIn.setBlockState(blockpos1.offset(direction.getOpposite()), chorusplantblock.makeConnections(worldIn, blockpos1.offset(direction.getOpposite())), 2);
                    growTreeRecursive(worldIn, blockpos1, rand, originalBranchPos, maxHorizontalDistance, iterations + 1);
                }
            }
        }

        if (!flag)
        {
            worldIn.setBlockState(branchPos.up(i), Blocks.CHORUS_FLOWER.getDefaultState().with(AGE, Integer.valueOf(5)), 2);
        }
    }

    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile)
    {
        if (projectile.getType().isContained(EntityTypeTags.IMPACT_PROJECTILES))
        {
            BlockPos blockpos = hit.getPos();
            worldIn.destroyBlock(blockpos, true, projectile);
        }
    }
}
