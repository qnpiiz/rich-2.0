package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlowerPotBlock extends Block
{
    private static final Map<Block, Block> POTTED_CONTENT = Maps.newHashMap();
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
    private final Block flower;

    public FlowerPotBlock(Block block, AbstractBlock.Properties properties)
    {
        super(properties);
        this.flower = block;
        POTTED_CONTENT.put(block, this);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
     */
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        ItemStack itemstack = player.getHeldItem(handIn);
        Item item = itemstack.getItem();
        Block block = item instanceof BlockItem ? POTTED_CONTENT.getOrDefault(((BlockItem)item).getBlock(), Blocks.AIR) : Blocks.AIR;
        boolean flag = block == Blocks.AIR;
        boolean flag1 = this.flower == Blocks.AIR;

        if (flag != flag1)
        {
            if (flag1)
            {
                worldIn.setBlockState(pos, block.getDefaultState(), 3);
                player.addStat(Stats.POT_FLOWER);

                if (!player.abilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }
            }
            else
            {
                ItemStack itemstack1 = new ItemStack(this.flower);

                if (itemstack.isEmpty())
                {
                    player.setHeldItem(handIn, itemstack1);
                }
                else if (!player.addItemStackToInventory(itemstack1))
                {
                    player.dropItem(itemstack1, false);
                }

                worldIn.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
            }

            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        else
        {
            return ActionResultType.CONSUME;
        }
    }

    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        return this.flower == Blocks.AIR ? super.getItem(worldIn, pos, state) : new ItemStack(this.flower);
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder
     * immediately returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        return facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public Block getFlower()
    {
        return this.flower;
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type)
    {
        return false;
    }
}
