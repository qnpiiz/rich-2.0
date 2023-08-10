package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class IceBlock extends BreakableBlock
{
    public IceBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        super.harvestBlock(worldIn, player, pos, state, te, stack);

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            if (worldIn.getDimensionType().isUltrawarm())
            {
                worldIn.removeBlock(pos, false);
                return;
            }

            Material material = worldIn.getBlockState(pos.down()).getMaterial();

            if (material.blocksMovement() || material.isLiquid())
            {
                worldIn.setBlockState(pos, Blocks.WATER.getDefaultState());
            }
        }
    }

    /**
     * Performs a random tick on a block.
     */
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        if (worldIn.getLightFor(LightType.BLOCK, pos) > 11 - state.getOpacity(worldIn, pos))
        {
            this.turnIntoWater(state, worldIn, pos);
        }
    }

    protected void turnIntoWater(BlockState state, World world, BlockPos pos)
    {
        if (world.getDimensionType().isUltrawarm())
        {
            world.removeBlock(pos, false);
        }
        else
        {
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            world.neighborChanged(pos, Blocks.WATER, pos);
        }
    }

    /**
     * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
     */
    public PushReaction getPushReaction(BlockState state)
    {
        return PushReaction.NORMAL;
    }
}
