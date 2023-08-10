package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class OreBlock extends Block
{
    public OreBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    protected int getExperience(Random rand)
    {
        if (this == Blocks.COAL_ORE)
        {
            return MathHelper.nextInt(rand, 0, 2);
        }
        else if (this == Blocks.DIAMOND_ORE)
        {
            return MathHelper.nextInt(rand, 3, 7);
        }
        else if (this == Blocks.EMERALD_ORE)
        {
            return MathHelper.nextInt(rand, 3, 7);
        }
        else if (this == Blocks.LAPIS_ORE)
        {
            return MathHelper.nextInt(rand, 2, 5);
        }
        else if (this == Blocks.NETHER_QUARTZ_ORE)
        {
            return MathHelper.nextInt(rand, 2, 5);
        }
        else
        {
            return this == Blocks.NETHER_GOLD_ORE ? MathHelper.nextInt(rand, 0, 1) : 0;
        }
    }

    /**
     * Perform side-effects from block dropping, such as creating silverfish
     */
    public void spawnAdditionalDrops(BlockState state, ServerWorld worldIn, BlockPos pos, ItemStack stack)
    {
        super.spawnAdditionalDrops(state, worldIn, pos, stack);

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0)
        {
            int i = this.getExperience(worldIn.rand);

            if (i > 0)
            {
                this.dropXpOnBlockBreak(worldIn, pos, i);
            }
        }
    }
}
