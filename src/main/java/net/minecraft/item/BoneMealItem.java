package net.minecraft.item;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;

public class BoneMealItem extends Item
{
    public BoneMealItem(Item.Properties builder)
    {
        super(builder);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(context.getFace());

        if (applyBonemeal(context.getItem(), world, blockpos))
        {
            if (!world.isRemote)
            {
                world.playEvent(2005, blockpos, 0);
            }

            return ActionResultType.func_233537_a_(world.isRemote);
        }
        else
        {
            BlockState blockstate = world.getBlockState(blockpos);
            boolean flag = blockstate.isSolidSide(world, blockpos, context.getFace());

            if (flag && growSeagrass(context.getItem(), world, blockpos1, context.getFace()))
            {
                if (!world.isRemote)
                {
                    world.playEvent(2005, blockpos1, 0);
                }

                return ActionResultType.func_233537_a_(world.isRemote);
            }
            else
            {
                return ActionResultType.PASS;
            }
        }
    }

    public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos);

        if (blockstate.getBlock() instanceof IGrowable)
        {
            IGrowable igrowable = (IGrowable)blockstate.getBlock();

            if (igrowable.canGrow(worldIn, pos, blockstate, worldIn.isRemote))
            {
                if (worldIn instanceof ServerWorld)
                {
                    if (igrowable.canUseBonemeal(worldIn, worldIn.rand, pos, blockstate))
                    {
                        igrowable.grow((ServerWorld)worldIn, worldIn.rand, pos, blockstate);
                    }

                    stack.shrink(1);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean growSeagrass(ItemStack stack, World worldIn, BlockPos pos, @Nullable Direction side)
    {
        if (worldIn.getBlockState(pos).isIn(Blocks.WATER) && worldIn.getFluidState(pos).getLevel() == 8)
        {
            if (!(worldIn instanceof ServerWorld))
            {
                return true;
            }
            else
            {
                label80:

                for (int i = 0; i < 128; ++i)
                {
                    BlockPos blockpos = pos;
                    BlockState blockstate = Blocks.SEAGRASS.getDefaultState();

                    for (int j = 0; j < i / 16; ++j)
                    {
                        blockpos = blockpos.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);

                        if (worldIn.getBlockState(blockpos).hasOpaqueCollisionShape(worldIn, blockpos))
                        {
                            continue label80;
                        }
                    }

                    Optional<RegistryKey<Biome>> optional = worldIn.func_242406_i(blockpos);

                    if (Objects.equals(optional, Optional.of(Biomes.WARM_OCEAN)) || Objects.equals(optional, Optional.of(Biomes.DEEP_WARM_OCEAN)))
                    {
                        if (i == 0 && side != null && side.getAxis().isHorizontal())
                        {
                            blockstate = BlockTags.WALL_CORALS.getRandomElement(worldIn.rand).getDefaultState().with(DeadCoralWallFanBlock.FACING, side);
                        }
                        else if (random.nextInt(4) == 0)
                        {
                            blockstate = BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random).getDefaultState();
                        }
                    }

                    if (blockstate.getBlock().isIn(BlockTags.WALL_CORALS))
                    {
                        for (int k = 0; !blockstate.isValidPosition(worldIn, blockpos) && k < 4; ++k)
                        {
                            blockstate = blockstate.with(DeadCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.random(random));
                        }
                    }

                    if (blockstate.isValidPosition(worldIn, blockpos))
                    {
                        BlockState blockstate1 = worldIn.getBlockState(blockpos);

                        if (blockstate1.isIn(Blocks.WATER) && worldIn.getFluidState(blockpos).getLevel() == 8)
                        {
                            worldIn.setBlockState(blockpos, blockstate, 3);
                        }
                        else if (blockstate1.isIn(Blocks.SEAGRASS) && random.nextInt(10) == 0)
                        {
                            ((IGrowable)Blocks.SEAGRASS).grow((ServerWorld)worldIn, random, blockpos, blockstate1);
                        }
                    }
                }

                stack.shrink(1);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public static void spawnBonemealParticles(IWorld worldIn, BlockPos posIn, int data)
    {
        if (data == 0)
        {
            data = 15;
        }

        BlockState blockstate = worldIn.getBlockState(posIn);

        if (!blockstate.isAir())
        {
            double d0 = 0.5D;
            double d1;

            if (blockstate.isIn(Blocks.WATER))
            {
                data *= 3;
                d1 = 1.0D;
                d0 = 3.0D;
            }
            else if (blockstate.isOpaqueCube(worldIn, posIn))
            {
                posIn = posIn.up();
                data *= 3;
                d0 = 3.0D;
                d1 = 1.0D;
            }
            else
            {
                d1 = blockstate.getShape(worldIn, posIn).getEnd(Direction.Axis.Y);
            }

            worldIn.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)posIn.getX() + 0.5D, (double)posIn.getY() + 0.5D, (double)posIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);

            for (int i = 0; i < data; ++i)
            {
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextGaussian() * 0.02D;
                double d4 = random.nextGaussian() * 0.02D;
                double d5 = 0.5D - d0;
                double d6 = (double)posIn.getX() + d5 + random.nextDouble() * d0 * 2.0D;
                double d7 = (double)posIn.getY() + random.nextDouble() * d1;
                double d8 = (double)posIn.getZ() + d5 + random.nextDouble() * d0 * 2.0D;

                if (!worldIn.getBlockState((new BlockPos(d6, d7, d8)).down()).isAir())
                {
                    worldIn.addParticle(ParticleTypes.HAPPY_VILLAGER, d6, d7, d8, d2, d3, d4);
                }
            }
        }
    }
}
