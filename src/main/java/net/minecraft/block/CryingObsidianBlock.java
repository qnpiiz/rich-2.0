package net.minecraft.block;

import java.util.Random;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CryingObsidianBlock extends Block
{
    public CryingObsidianBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    /**
     * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
     * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
     * of whether the block can receive random update ticks
     */
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (rand.nextInt(5) == 0)
        {
            Direction direction = Direction.getRandomDirection(rand);

            if (direction != Direction.UP)
            {
                BlockPos blockpos = pos.offset(direction);
                BlockState blockstate = worldIn.getBlockState(blockpos);

                if (!stateIn.isSolid() || !blockstate.isSolidSide(worldIn, blockpos, direction.getOpposite()))
                {
                    double d0 = direction.getXOffset() == 0 ? rand.nextDouble() : 0.5D + (double)direction.getXOffset() * 0.6D;
                    double d1 = direction.getYOffset() == 0 ? rand.nextDouble() : 0.5D + (double)direction.getYOffset() * 0.6D;
                    double d2 = direction.getZOffset() == 0 ? rand.nextDouble() : 0.5D + (double)direction.getZOffset() * 0.6D;
                    worldIn.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}
