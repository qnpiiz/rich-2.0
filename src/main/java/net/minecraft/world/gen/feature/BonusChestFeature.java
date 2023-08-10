package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;

public class BonusChestFeature extends Feature<NoFeatureConfig>
{
    public BonusChestFeature(Codec<NoFeatureConfig> p_i231934_1_)
    {
        super(p_i231934_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        ChunkPos chunkpos = new ChunkPos(p_241855_4_);
        List<Integer> list = IntStream.rangeClosed(chunkpos.getXStart(), chunkpos.getXEnd()).boxed().collect(Collectors.toList());
        Collections.shuffle(list, p_241855_3_);
        List<Integer> list1 = IntStream.rangeClosed(chunkpos.getZStart(), chunkpos.getZEnd()).boxed().collect(Collectors.toList());
        Collections.shuffle(list1, p_241855_3_);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for (Integer integer : list)
        {
            for (Integer integer1 : list1)
            {
                blockpos$mutable.setPos(integer, 0, integer1);
                BlockPos blockpos = p_241855_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable);

                if (p_241855_1_.isAirBlock(blockpos) || p_241855_1_.getBlockState(blockpos).getCollisionShape(p_241855_1_, blockpos).isEmpty())
                {
                    p_241855_1_.setBlockState(blockpos, Blocks.CHEST.getDefaultState(), 2);
                    LockableLootTileEntity.setLootTable(p_241855_1_, p_241855_3_, blockpos, LootTables.CHESTS_SPAWN_BONUS_CHEST);
                    BlockState blockstate = Blocks.TORCH.getDefaultState();

                    for (Direction direction : Direction.Plane.HORIZONTAL)
                    {
                        BlockPos blockpos1 = blockpos.offset(direction);

                        if (blockstate.isValidPosition(p_241855_1_, blockpos1))
                        {
                            p_241855_1_.setBlockState(blockpos1, blockstate, 2);
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
