package net.minecraft.world;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

public interface IBlockDisplayReader extends IBlockReader
{
    float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_);

    WorldLightManager getLightManager();

    int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn);

default int getLightFor(LightType lightTypeIn, BlockPos blockPosIn)
    {
        return this.getLightManager().getLightEngine(lightTypeIn).getLightFor(blockPosIn);
    }

default int getLightSubtracted(BlockPos blockPosIn, int amount)
    {
        return this.getLightManager().getLightSubtracted(blockPosIn, amount);
    }

default boolean canSeeSky(BlockPos blockPosIn)
    {
        return this.getLightFor(LightType.SKY, blockPosIn) >= this.getMaxLightLevel();
    }
}
